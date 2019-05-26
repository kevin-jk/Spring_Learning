这一章节我们讲解如何使用AOP. 根据前面的讲解，aop是可以为目标方法织入特有的功能。例如日志的记录，安全的检查等。下面我们就以记录日志为例
看看aop到底是怎么使用的。

# 目标方法的定义
首先我们定义自己的正常业务方法（目标方法）。在此处我们模拟支付，定义如下的接口和实现：

1. 业务接口的模拟，支付接口定义

```java
/**
 * 支付接口
 * @author  jiakun
 * */
public interface PayCoreServiceFacade {
    /**
     * 支付方法
     *
     * @param  fromCount 转出账户
     * @param  toAccount 转入账户
     *@param  money 转出金额
     * */
    public void pay(String fromCount, String toAccount,int money) throws InterruptedException;
}
```

2. 业务接口的实现

   ```java
   /**
    * 模拟支付的业务
    *
    * */
   @Service
   public class PayCoreServiceImpl implements PayCoreServiceFacade {
       @Override
       public void pay(String fromCount, String toAccount, int money) throws InterruptedException {
           System.out.println("Start to pay, please waiting");
           Thread.sleep(1000);
           System.out.println("Congratulations, pay success. From "+  fromCount + " account pay success "+money+ " to "+toAccount);
       }
   }
   ```

3. 下面我们用JUnit测试，看下接口是否正常工作：

   ```java
   @Test
   public void test_payCoreServiceFacadeNormal() throws Exception{
       payCoreServiceFacade.pay(fromAccount, toAccount,payMoneyCount());
   }
   private int payMoneyCount(){
       return new Random().nextInt(10000);
   }
   ```

   运行后发现程序可以正常的启动，如果发现程序不能正常测试，请查阅spring和junit的测试相关内容，此处不再讲解。

   ## XML AOP配置 

   现在，由于支付业务的重要性，我们要求记录详细的日志，以方便解决各种问题，并需要记录支付接口的调用时间，以进行优化。因此，根据需求，我们定义需要的操作：

   ```java
   @Service
   public class LogService {
   
       //    方法执行前通知
       public void beforeLog() {
           System.out.println("开始执行前置通知  开始调用");
       }
       //    方法执行完后通知
       public void afterLog() {
           System.out.println("开始执行后置通知 调用完成");
       }
       //    执行成功后通知
       public void afterReturningLog() {
           System.out.println("方法成功执行后通知 开始返回");
       }
       //    抛出异常后通知
       public void afterThrowingLog() {
           System.out.println("方法抛出异常后执行通知 异常了");
       }
   
       //    环绕通知
       public Object aroundLog(ProceedingJoinPoint joinpoint) {
           Object result = null;
           try {
               System.out.println("环绕通知开始 记录执行时间开始");
               long start = System.currentTimeMillis();
   
               //有返回参数 则需返回值
               result =  joinpoint.proceed();
   
               long end = System.currentTimeMillis();
               System.out.println("总共执行时长" + (end - start) + " 毫秒");
               System.out.println("环绕通知结束 记录执行时间结束");
           } catch (Throwable t) {
               System.out.println("Exception");
           }
           return result;
       }
   
   }
   ```

   如果想使用aop， 那么还差最后一步，就是切面的配置，在spring context配置文件中进行如下的配置：

   ```xml
   <aop:config>
       <aop:aspect ref="logService">
           <aop:before pointcut="execution(** com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade.pay(..))" method="beforeLog"/>
           <aop:after pointcut="execution(** com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade.pay(..))" method="afterLog"/>
           <aop:after-returning pointcut="execution(** com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade.pay(..))" method="afterReturningLog"/>
           <aop:after-throwing pointcut="execution(** com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade.pay(..))" method="afterThrowingLog"/>
           <aop:around pointcut="execution(** com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade.pay(..))" method="aroundLog"/>
       </aop:aspect>
   </aop:config>
   ```

   最后我们执行下上面的测试用例，会发现实现了我们的功能，结果如下：

   *开始执行前置通知  开始调用*
   *环绕通知开始 记录执行时间开始*
   *Start to pay, please waiting*
   *Congratulations, pay success. From Bob account pay success 3148 to Kevin*
   *总共执行时长1000 毫秒*
   *环绕通知结束 记录执行时间结束*
   *方法成功执行后通知 开始返回*
   *开始执行后置通知 调用完成*

   

   自此，我们使用xml进行aop的使用到此完毕。 不过细心的少年，等等，上面的aop的配置，有很多重复的配置，我们需要优化下。

   ```xml
   <aop:config>
       <aop:aspect ref="logService">
           <aop:pointcut id="pay" expression="execution(** com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade.pay(..))"/>
           <aop:before pointcut-ref="pay" method="beforeLog"/>
           <aop:after pointcut-ref="pay" method="afterLog"/>
           <aop:after-returning pointcut-ref="pay" method="afterReturningLog"/>
           <aop:after-throwing pointcut-ref="pay" method="afterThrowingLog"/>
           <aop:around pointcut-ref="pay" method="aroundLog"/>
       </aop:aspect>
   </aop:config>
   ```

   很简单的，这样减少了很多的字。其实spring aop简单的使用已经结束，实际上，有很多的aop配置方法，下面我们介绍下另外一种自动注解的方法，这样就不需要xml配置。 但是其原理是一样的，只是表现形式不一样。

   ## 自动注解AOP

   同样的，新需求来了，需要记录支付操作的每一步，使用自动注解aop，我们该怎么做呢？

   首先，还是需要定义我们插入的功能：记录日志和记录接口调用时间。我们需要将上面的LogService稍微改造下：

   ```java
   // 这2个注解必不可少
   @Aspect
   @Service
   public class LogServiceAutoAspect {
       @Pointcut("execution(** com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade.pay(..))")
       public void pointCut(){
   
       }
       //    方法执行前通知
       @Before("pointCut()")
       public void beforeLog() {
           System.out.println("开始执行前置通知  开始调用");
       }
       //    方法执行完后通知
       @After("pointCut()")
       public void afterLog() {
           System.out.println("开始执行后置通知 调用完成");
       }
       //    执行成功后通知
       @AfterReturning("pointCut()")
       public void afterReturningLog() {
           System.out.println("方法成功执行后通知 开始返回");
       }
       //    抛出异常后通知
       @AfterThrowing("pointCut()")
       public void afterThrowingLog() {
           System.out.println("方法抛出异常后执行通知 异常了");
       }
   
       //    环绕通知
       @Around("pointCut()")
       public Object aroundLog(ProceedingJoinPoint joinpoint) {
           Object result = null;
           try {
               System.out.println("环绕通知开始 记录执行时间开始");
               long start = System.currentTimeMillis();
   
               //有返回参数 则需返回值
               result =  joinpoint.proceed();
   
               long end = System.currentTimeMillis();
               System.out.println("总共执行时长" + (end - start) + " 毫秒");
               System.out.println("环绕通知结束 记录执行时间结束");
           } catch (Throwable t) {
               System.out.println("Exception");
           }
           return result;
       }
   
   }
   ```

我们将xml中的aop config注释掉，跑测试用例，看看效果是否和我们预期一致。 

很遗憾，你会发现，测试用例只是调用了其真实的方法，并没有完成我们的日制的织入。 其实我们还差一个步骤，就是启动AspectJ自动注解，如下：

```xml
<aop:aspectj-autoproxy/>
```

自此，运行测试用用例，你会发现和xml配置的aop效果一样。

总结下，自动注解，就是将通知，切点定义在我们的扩展功能类中，然后使用注解去标识，不要忘记的是需要我们显示的启动自动aspectJ自动注解功能。