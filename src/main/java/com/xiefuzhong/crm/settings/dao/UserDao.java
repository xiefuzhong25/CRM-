package com.xiefuzhong.crm.settings.dao;

import com.xiefuzhong.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    User login(Map<String, String> map);

    List<User> getUserList();

    String getUserNameById(String id);
}
