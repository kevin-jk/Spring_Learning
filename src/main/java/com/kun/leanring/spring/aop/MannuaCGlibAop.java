package com.kun.leanring.spring.aop;


import com.kun.leanring.spring.aop.service.IUserService;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Created by jrjiakun on 2018/9/30
 */
@Component
public class MannuaCGlibAop {
    @Resource
    IUserService iUserService;

    public void getUserNameDirect(){
        System.out.println(iUserService.getDefaultUserName());
    }

    public void getUserNameProxy(){
        IUserService myProxy =(IUserService) new MyProxy(iUserService).getProxy();
        System.out.println(myProxy.getDefaultUserName());
    }
}

class CGlibProxy implements MethodInterceptor {
    private Enhancer enhancer = new Enhancer();

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("CGLib中间人开始转接...");
        Object obj = methodProxy.invokeSuper(o, objects);//通过代理子类调用父类的方法
        System.out.println("CGLib中间人收到消息，转接结束...");
        return obj;
    }

    public Object getProxy(Class clazz) {
        enhancer.setSuperclass(clazz);//目标对象类
        enhancer.setCallback(this);
        return enhancer.create();//通过字节码技术创建目标对象类的子类实例作为代理
    }
}