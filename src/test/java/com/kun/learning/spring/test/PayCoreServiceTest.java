package com.kun.learning.spring.test;

import com.kun.leanring.spring.usageaop.service.PayCoreServiceFacade;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Random;

public class PayCoreServiceTest extends TestBase {

    private static final String fromAccount = "Bob";
    private static final String toAccount = "Kevin";



    @Resource
    private PayCoreServiceFacade payCoreServiceFacade;


    /**
     * 测试正常的业务调用
     * */
    @Test
    public void test_payCoreServiceFacadeNormal() throws Exception{
        payCoreServiceFacade.pay(fromAccount, toAccount,payMoneyCount());
    }
    private int payMoneyCount(){
        return new Random().nextInt(10000);
    }
    /**
     * 现在需求变更了，需要记录每一步操作，以确保安全。
     *
     * 首先需要定义需要记录的日志： {@link com.kun.leanring.spring.usageaop.LogService} 该类用来记录每个阶段的日志
     *
     * 然后需要对AOP进行配置， 可参见spring-context中 <aop:config > 相关的配置
     */
    @Test
    public void test_payCoreServiceFacadeWithLog() throws Throwable{
        payCoreServiceFacade.pay(fromAccount, toAccount,payMoneyCount());
    }

}
