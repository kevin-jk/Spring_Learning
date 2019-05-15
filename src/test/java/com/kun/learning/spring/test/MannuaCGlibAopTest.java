package com.kun.learning.spring.test;

import com.kun.leanring.spring.aop.MannuaCGlibAop;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by jrjiakun on 2018/9/30
 */
public class MannuaCGlibAopTest extends TestBase{
    @Resource
    private MannuaCGlibAop mannuaCGlibAop;
    @Test
    public void test_direct(){
        mannuaCGlibAop.getUserNameDirect();
    }

    @Test
    public void test_proxy(){
        mannuaCGlibAop.getUserNameProxy();
    }
}
