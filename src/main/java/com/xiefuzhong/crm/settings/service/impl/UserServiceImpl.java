package com.xiefuzhong.crm.settings.service.impl;

import com.xiefuzhong.crm.exception.LoginException;
import com.xiefuzhong.crm.settings.dao.UserDao;
import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.settings.service.UserService;
import com.xiefuzhong.crm.utils.DateTimeUtil;
import com.xiefuzhong.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    //使用SqlSession 创建dao接口的代理对象
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);

        //查询数据库
        User user = userDao.login(map);
        if (user==null){
            //让其抛出自定义的异常
            throw  new LoginException("账户密码错误");
        }
        //走到这里了说明账号密码正确  继续验证其他

        //验证失效时间
        String expireTime = user.getExpireTime();
            //获取当前系统时间
        String currentTime = DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime)<0){
            throw  new  LoginException("账号已经失效");
        }
        //验证是否锁定状态
        String lockState = user.getLockState();
        if ("0".equals(lockState)){
            throw  new LoginException("账号已经锁定");
        }
        //判断ip
        String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){
            throw  new LoginException("ip受限");
        }


        return user;
    }




    //市场活动发出来的业务需求需要处理
    @Override
    public List<User> getUserList() {
        //调dao层拿到结果就行
        List<User> uList = userDao.getUserList();
        return uList;
    }

    @Override
    public String getUserNameById(String id) {
        String  name = userDao.getUserNameById(id);
        return name;
    }
}
