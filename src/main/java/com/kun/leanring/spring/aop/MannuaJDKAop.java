package com.kun.leanring.spring.aop;

import com.kun.leanring.spring.aop.service.IUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by jrjiakun on 2018/9/30
 *
 * 通过动态代理实现
 */
@Component
public class MannuaJDKAop {
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

class MyProxy{
    Object targe;
    MyProxy(Object targe){
        this.targe = targe;
    }
    public Object getProxy(){
        // 生成代理
        // classLoader 获取类加载器后，可以通过这个类型的加载器，在程序运行时，将生成的代理类加载到JVM即Java虚拟机中，以便运行时需要！
       return Proxy.newProxyInstance(targe.getClass().getClassLoader(), targe.getClass().getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("中间人开始转接...");
                // 调用真正的目标方法
                Object obj = method.invoke(targe, args);
                System.out.println("中间人收到消息，转接结束...");
                return obj;
            }
        });
    }
}