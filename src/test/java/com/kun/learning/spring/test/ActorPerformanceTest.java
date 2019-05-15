package com.kun.learning.spring.test;

import com.kun.learning.spring.service.ActorPerformance;
import org.junit.Test;

import javax.annotation.Resource;

public class ActorPerformanceTest extends TestBase{

    // 如果此时没有加proxy-target-class = true时候，会报错
    @Resource
    private ActorPerformance actorPerformance;


    @Test
    public void test_aop(){
        actorPerformance.perform("test");
    }
}
