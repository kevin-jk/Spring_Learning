package com.kun.leanring.spring.usageaop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Service;

@Aspect
@Service
public class LogServiceAutoAspect {
    @Pointcut("execution(* com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade.*(..))")
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
