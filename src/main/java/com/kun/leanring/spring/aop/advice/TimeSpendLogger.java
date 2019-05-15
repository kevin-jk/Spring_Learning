package com.kun.leanring.spring.aop.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Service;


/**
 * @author: jrjiakun
 * Created on 2019/5/15 11:02
 */
@Service
public class TimeSpendLogger implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = invocation.proceed();
        System.out.println("Cost time is "+ (System.currentTimeMillis()-startTime));
        return result;
    }
}
