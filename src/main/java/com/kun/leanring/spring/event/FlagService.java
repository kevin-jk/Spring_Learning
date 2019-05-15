package com.kun.leanring.spring.event;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @Author: jrjiakun
 * @Date: 2019/2/26 15:05
 *
 * //1.  建立事件类
 * // 2、 建立监听类
 * // 3. 建立事件发布类，发布事件。 需要实现ApplicationContextAware接口，并发布事件
 *
 *
 */

public class FlagService implements ApplicationContextAware {


    private ApplicationContext applicationContextAware;
    private volatile  boolean flag =false;
    public void setFlagTrue(){
        flag = true;
        FlagChangeEvent changeEvent = new FlagChangeEvent(flag);
        applicationContextAware.publishEvent(changeEvent);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContextAware = applicationContext;
    }

}
