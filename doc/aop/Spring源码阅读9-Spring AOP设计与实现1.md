在Spring中，使用AOP比较简单，只需要简单配置即可。实际上，为了让AOP生效，Spring完成了一系列的内部操作，主要有

1. 为目标对象建立代理对象
2. 启动代理对象的拦截器完成横切面的织入

下面我们将分别介绍上述2个过程

# AOPProxy代理对象

首先我们看下Spring中AOP相关的类关系：

![1557889195075](C:\Users\jrjiakun\AppData\Roaming\Typora\typora-user-images\1557889195075.png)

下面我们以ProxyFactoryBean为例来配置AOP进行讲解。

