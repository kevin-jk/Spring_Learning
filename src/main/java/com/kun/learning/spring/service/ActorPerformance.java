package com.kun.learning.spring.service;

import org.springframework.stereotype.Component;

@Component
public class ActorPerformance implements Performance {

    @Override
    public void perform(String music) {
        System.out.println("ActorPerformance");
    }
}
