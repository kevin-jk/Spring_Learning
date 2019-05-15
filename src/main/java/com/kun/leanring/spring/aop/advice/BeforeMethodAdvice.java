package com.kun.leanring.spring.aop.advice;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by jrjiakun on 2018/9/30
 */
@Component
public class BeforeMethodAdvice implements MethodBeforeAdvice {
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("before advice:"+method);
    }
}
