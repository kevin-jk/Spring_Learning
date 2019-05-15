package com.kun.leanring.spring.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * @Author: jrjiakun
 * @Date: 2019/2/26 15:01
 */
@Service
public class FlagChangeListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if(applicationEvent instanceof  FlagChangeEvent){
            System.out.println("Flag change event");
        }
    }
}
