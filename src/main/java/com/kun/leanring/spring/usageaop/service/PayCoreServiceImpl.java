package com.kun.leanring.spring.usageaop.service;

import org.springframework.stereotype.Service;


/**
 * 模拟支付的业务
 *
 * */
@Service
public class PayCoreServiceImpl implements PayCoreServiceFacade {
    @Override
    public void pay(String fromCount, String toAccount, int money) throws InterruptedException {
        System.out.println("Start to pay, please waiting");
        Thread.sleep(1000);
        System.out.println("Congratulations, pay success. From "+  fromCount + " account pay success "+money+ " to "+toAccount);
    }
}
