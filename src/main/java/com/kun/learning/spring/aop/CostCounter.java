package com.kun.learning.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

// 切面 ： 需要做的事情
@Component
public class CostCounter {

    // 环绕通知   ProceedingJoinPoint 需要依赖spring-aspects
    public void costCount(ProceedingJoinPoint p){
        long start = System.currentTimeMillis();
        try{
            p.proceed();
        }catch (Throwable e){

        }
        long cost = System.currentTimeMillis() - start;
        System.out.println("The cost time is "+cost);
    }
}
