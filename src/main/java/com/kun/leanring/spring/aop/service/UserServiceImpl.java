package com.kun.leanring.spring.aop.service;

import org.springframework.stereotype.Component;


/**
 * Created by jrjiakun on 2018/9/30
 */
@Component
public class UserServiceImpl implements IUserService {
    public String getDefaultUserName() {
        System.out.println("Get UserName");
        BizProcessMock.bizProcess();
        return "Kevin Jia";
    }

    public String getDefaultUserSex() {
        System.out.println("Get Sex");
        BizProcessMock.bizProcess();
        return "Man";
    }


}
