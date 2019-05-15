package com.kun.leanring.spring.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author: jrjiakun
 * @Date: 2019/3/5 17:15
 */
public class Main {
    public static void main(String[]arg){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
        ComputeService flagService = (ComputeService)  applicationContext.getBean("timeAop");
        System.out.println(flagService.compute(1,1000,"+"));
        return;
    }
}
