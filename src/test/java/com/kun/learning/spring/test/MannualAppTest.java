package com.kun.learning.spring.test;

import com.kun.leanring.spring.aop.MannuaJDKAop;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by jrjiakun on 2018/9/30
 */
public class MannualAppTest extends TestBase {

    @Resource
    private MannuaJDKAop mannuaJDKAop;
    @Test
    public void test_direct(){
        mannuaJDKAop.getUserNameDirect();
    }

    @Test
    public void test_proxy(){
        mannuaJDKAop.getUserNameProxy();
    }
}
