package com.kun.learning.spring.test;

import com.kun.leanring.spring.aop.advice.BeforeMethodAdvice;
import com.kun.leanring.spring.aop.service.UserBizService;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import javax.annotation.Resource;

/**
 * Created by jrjiakun on 2018/9/30
 *
 * aop在spring的使用
 */
public class UserBizServiceTest extends TestBase {

    @Resource
    UserBizService userBizService;

    @Resource
    UserBizService userBizServiceProxy;

    @Test
    public void test_code_aop(){
        BeforeMethodAdvice beforeMethodAdvice = new BeforeMethodAdvice();
        ProxyFactory pf = new ProxyFactory();
        //proxy-target-class 属性设为true
        pf.setProxyTargetClass(true);
        pf.setTarget(userBizService);
        pf.addAdvice(beforeMethodAdvice);
        UserBizService proxy = (UserBizService)pf.getProxy();
        proxy.userNameGain();
    }
    @Test
    public void test_xml_proxy(){
        userBizServiceProxy.userNameGain();
    }
}
