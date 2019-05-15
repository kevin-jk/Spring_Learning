package com.kun.leanring.spring.aop.service;

import java.util.Random;

/**
 * Created by jrjiakun on 2018/9/30
 */
public class BizProcessMock {
    public static void bizProcess(){
        try{
            Random random = new Random(System.currentTimeMillis());
            Thread.sleep(random.nextInt(5000));
        }catch (Exception e){

        }
    }
}
