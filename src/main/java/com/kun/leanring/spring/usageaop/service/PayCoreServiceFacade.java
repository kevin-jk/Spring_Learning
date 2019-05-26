package com.kun.leanring.spring.usageaop.service;

/**
 * 支付接口
 * @author  jiakun
 * */
public interface PayCoreServiceFacade {
    /**
     * 支付方法
     *
     * @param  fromCount 转出账户
     * @param  toAccount 转入账户
     *@param  money 转出金额
     * */
    void pay(String fromCount, String toAccount,int money) throws InterruptedException;
}
