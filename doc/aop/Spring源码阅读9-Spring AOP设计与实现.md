在Spring中，使用AOP比较简单，只需要简单配置即可。实际上，为了让AOP生效，Spring完成了一系列的内部操作，主要有

1. 为目标对象建立代理对象
2. 启动代理对象的拦截器完成横切面的织入

下面我们将分别介绍上述2个过程

# AopProxy代理对象

首先我们看下Spring中AOP相关的类关系：

![aop类继承关系](E:\project\kun_practice\Spring_Learning\doc\img\aop类继承关系.png)

下面我们以ProxyFactoryBean为例来配置AOP进行讲解。通常我们需要类似这样的配置：

```java
  <bean name="timeAop" class="org.springframework.aop.framework.ProxyFactoryBean">
        <property name="target">
            <ref bean="computeServiceImpl"></ref>
        </property>
        <property name="interceptorNames">
            <list>
                <value>timeSpendLogger</value>
            </list>
        </property>
        <property name="interfaces">
            <value>com.kun.leanring.spring.aop.service.ComputeService</value>
        </property>
    </bean>
```

其中关键点在于：target 属性和interceptorNames 这2个属性的配置。那么它是如何生成AopProxy代理对象的呢？

实际上，ProxyFactoryBean 实现了FactoryBean的getObject方法，前面的IOC章节中我们也讲解了FactoryBean的一些原理。 对于ProxyFactoryBean而言，需要做的工作就是： 将target目标对象进行增强， 而这一切都是通过getObject方法进行了封装。

下面我们以getObject为切入点，对AOP过程进行阅读。

```java
 public Object getObject() throws BeansException {
     // 初始化通知器联调
        this.initializeAdvisorChain();
        if (this.isSingleton()) {
            return this.getSingletonInstance();
        } else {
            if (this.targetName == null) {
                this.logger.info("Using non-singleton proxies with singleton targets is often undesirable. Enable prototype proxies by setting the 'targetName' property.");
            }

            return this.newPrototypeInstance();
        }
    }
```

当应用第一次通过ProxyFactoryBean 获取target对象的时候，就会进行初始化通知器调用链：

```java
	private synchronized void initializeAdvisorChain() throws AopConfigException, BeansException {
        // 如果已经初始化过，直接返回
		if (this.advisorChainInitialized) {
			return;
		}

		if (!ObjectUtils.isEmpty(this.interceptorNames)) {
			if (this.beanFactory == null) {
				throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) " +
						"- cannot resolve interceptor names " + Arrays.asList(this.interceptorNames));
			}

			// Globals can't be last unless we specified a targetSource using the property...
			if (this.interceptorNames[this.interceptorNames.length - 1].endsWith(GLOBAL_SUFFIX) &&
					this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE) {
				throw new AopConfigException("Target required after globals");
			}

			// Materialize interceptor chain from bean names.
            // 添加Advisor链的调用
			for (String name : this.interceptorNames) {
				if (logger.isTraceEnabled()) {
					logger.trace("Configuring advisor or advice '" + name + "'");
				}

				if (name.endsWith(GLOBAL_SUFFIX)) {
					if (!(this.beanFactory instanceof ListableBeanFactory)) {
						throw new AopConfigException(
								"Can only use global advisors or interceptors with a ListableBeanFactory");
					}
					addGlobalAdvisor((ListableBeanFactory) this.beanFactory,
							name.substring(0, name.length() - GLOBAL_SUFFIX.length()));
				}

				else {
					// If we get here, we need to add a named interceptor.
					// We must check if it's a singleton or prototype.
					Object advice;
                    // 根据Singleton与否，进行不同的处理
					if (this.singleton || this.beanFactory.isSingleton(name)) {
						// Add the real Advisor/Advice to the chain.
						// 通过BeanFactory获取对应的Advice Bean ,因为ProxyFactoryBean实现了BeanFactoryAware接口
						advice = this.beanFactory.getBean(name);
					}
					else {
						// It's a prototype Advice or Advisor: replace with a prototype.
						// Avoid unnecessary creation of prototype bean just for advisor chain initialization.
						advice = new PrototypePlaceholderAdvisor(name);
					}
					addAdvisorOnChainCreation(advice, name);
				}
			}
		}
        // 设置为已经初始化
		this.advisorChainInitialized = true;
	}

```

当advisor链初始化完成后，我们看getObject方法中，会根据不同的Bean类型去生成AopProxy代理对象。其中，我们先看下getSingletonInstance方法。

```java
private synchronized Object getSingletonInstance() {
		if (this.singletonInstance == null) {
			this.targetSource = freshTargetSource();
			if (this.autodetectInterfaces && getProxiedInterfaces().length == 0 && !isProxyTargetClass()) {
				// Rely on AOP infrastructure to tell us what interfaces to proxy.
                //根据AOP框架来判断需要代理的接口
				Class<?> targetClass = getTargetClass();
				if (targetClass == null) {
					throw new FactoryBeanNotInitializedException("Cannot determine target class for proxy");
				}
                // 设置代理对象的接口
				setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
			}
			// Initialize the shared singleton instance.
			super.setFrozen(this.freezeProxy);
            // 使用ProxyFacotry来生成Proxy
			this.singletonInstance = getProxy(createAopProxy());
		}
		return this.singletonInstance;
	}
```

接着我们看下关键的地方，createAopProxy方法，通过跟踪代码发现，这个方法的根本实现是在DefaultAopProxyFactory中：

```java
public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
		if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
				return new JdkDynamicAopProxy(config);
			}
			return new ObjenesisCglibAopProxy(config);
		}
		else {
			return new JdkDynamicAopProxy(config);
		}
	}
```

上面的代码很清楚：当目标对下那个是接口类，那么会使用JDK来生成代理对象，否则Spring会使用CGlib来生成目标对象的代理对象。

使用JDK或者CGLib生成代理对象的过程，这里暂时先不再敖述。

自此，我们可以发现，ProxyFactoryBean的getObject方法得到的是一个AopProxy代理对象。下面的内容，我们将看下是如何拦截的？