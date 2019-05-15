## Spring IOC 容器实现整体概述

在Spring中，IOC容器的设计中，主要分为2种具体的表现形式：

1.  BeanFactory为代表的容器
2.  ApplicationContext为代表的应用上下文容器

## IOC容器到底是什么？

如果用一句话来表述，Ioc容器的底层数据结构，就是一个Map, 保存着各个Bean的定义，然后通过依赖注入等方式将这些Bean管理起来。

## BeanFactory和ApplicationContext2种模式的区别

![img](https://images2015.cnblogs.com/blog/831179/201702/831179-20170222113018679-468547987.png)

如上图，为Spring IOC容器实现的大致架构，对此，上述2种具体的表现形式，其区别在于：

BeanFactory提供了最基本的IOC容器功能，是IOC容器的基本形式。  而ApplicationContext是Ioc容器的高级表现形式，如支持不同信息源， 支持应用事件等。

## IOC容器的初始化过程

回想一下，我们在使用Spring的时候，如何定义Bean的？ 

通常情况下，使用xml或者注解的方式配置好Bean，然后在合适的地方通过getBean的方式就可以获取到对应的Bean. 

上面也提到过，IOC容器实际上就是一个Map,  结合这些信息，我们如何自己实现Ioc容器呢？

显而易见， 首先我们要获取用户定义的xml(或者注解配置的)中配置的Bean（资源Resource的定位）， 然后将这些配置（特定的数据结构解析（BeanDefinition载入））存放在Map中（向IOC容器注册BeanDefinition）,最后就是在使用的Bean的时候注入需要的依赖（当然，有的Bean可以在容器初始化的时候将依赖关系注入）。

总结上述表述，我们可以得到如下的伪代码：

```java
// Resource定位
Resource resource = readResource();
// BeanDefinition的载入
BeanDefinition beanDefinition = parseBean(resource);
Map ioc = new HashMap();
//注册BeanDefiniton
registry(ioc,beanDefinition);
// 使用Bean， 依赖注入
Bean realBean = getBean();
```

后面我会按照一个具体的容器启动过程来分析，届时可以对比此处顺序，对号入座。



## 小结

说到底，IOC容器底层数据结构就是一个map,  然后在用户使用Bean的阶段，管理这些Bean的生命周期。

