package com.xiefuzhong.crm.settings.web.controller;

import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.settings.service.UserService;
import com.xiefuzhong.crm.settings.service.impl.UserServiceImpl;
import com.xiefuzhong.crm.utils.MD5Util;
import com.xiefuzhong.crm.utils.PrintJson;
import com.xiefuzhong.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController  extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.service(req, resp);
        System.out.println("进入到用户控制器");

        String path = request.getServletPath();

        if("/settings/user/login.do".equals(path)){
            login(request,response);

        }else  if("/settings/user/xxx.do".equals(path)){

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到验证登录操作");
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将明文转化为MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        //接收浏览器端的ip地址
        String ip = request.getRemoteAddr();
        System.out.println("ip-------:"+ip);
        //未来登录操作尽量都走事务
           //UserService us = new UserServiceImpl();
        //传张三得到李四 代理对象
        UserService us =(UserService) ServiceFactory.getService(new UserServiceImpl());


        try {

            User user = us.login(loginAct,loginPwd,ip);
            //调用请求对象向Tomcat索要当前用户在服务端的私人储物柜。.将数据添加到用户私人储物柜
            // 将user保存在session域中保存起来
            request.getSession().setAttribute("user",user);
            //执行到这里说明业务层没有为controller抛出任何异常
            //表示登录成功
            /*
            *只要返回{"success"："true:}
            * 解析为json字符串
            * */
            //将boolean值解析为json串   {"success":true}
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
           e.printStackTrace();
            //返回具体的异常
            String msg = e.getMessage();
            //执行了catch就表示业务层为我们查询失败了抛出了异常
            // 表登录失败了
            /*
             *返回{"success"："false"."msg":  " xxx"}
             * 如果这个展现的信息将来还会大量的使用，我们就创建一个vo类 使用方便
             * 如果对与这个展现信息只有在这个需求中能够使用 我们就是用mao来储存就可以
             * */
            Map<String,Object> map = new HashMap<>();
            map.put("success",false);
            map.put("msg",msg);
            //将对象解析为json串
            PrintJson.printJsonObj(response,map);

        }

    }
}
