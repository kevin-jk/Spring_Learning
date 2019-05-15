package com.kun.learning.spring.test;

import com.kun.leanring.spring.event.FlagService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @Author: jrjiakun
 * @Date: 2019/2/26 15:11
 */
public class EventTest  extends TestBase {

    @Resource
    private FlagService flagService;
    @Test
    public void testEvent(){
        flagService.setFlagTrue();
    }
}
