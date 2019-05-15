package com.kun.leanring.spring.aop.service;


import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by jrjiakun on 2018/9/30
 */
@Component
public class UserBizService {
    @Resource
    IUserService iUserService;

    public String userNameGain(){
        System.out.println("UserName Gain");
        BizProcessMock.bizProcess();
        return  iUserService.getDefaultUserName();
    }
    public String userSexGain(){
        System.out.println("UserSex Gain");
        BizProcessMock.bizProcess();
        return  iUserService.getDefaultUserSex();
    }
}
