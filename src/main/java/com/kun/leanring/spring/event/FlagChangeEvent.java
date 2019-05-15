package com.kun.leanring.spring.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Author: jrjiakun
 * @Date: 2019/2/26 14:58
 *
 * 建立事件类
 *
 *
 */

public class FlagChangeEvent extends ApplicationEvent {

    public FlagChangeEvent(Object source) {
        super(source);
        System.out.println("This is FlagChangeEvent");
    }
}
