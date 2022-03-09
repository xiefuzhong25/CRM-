package com.xiefuzhong.crm.settings.service;

import com.xiefuzhong.crm.exception.LoginException;
import com.xiefuzhong.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();

    String getUserNameById(String id);
}
