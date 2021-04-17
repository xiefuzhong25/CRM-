package com.xiefuzhong.crm.workbench.web.controller;

import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.settings.service.UserService;
import com.xiefuzhong.crm.settings.service.impl.UserServiceImpl;
import com.xiefuzhong.crm.utils.PrintJson;
import com.xiefuzhong.crm.utils.ServiceFactory;
import com.xiefuzhong.crm.workbench.domain.Activity;
import com.xiefuzhong.crm.workbench.domain.Contacts;
import com.xiefuzhong.crm.workbench.service.ActivityService;
import com.xiefuzhong.crm.workbench.service.TranService;
import com.xiefuzhong.crm.workbench.service.impl.ActivityServiceImpl;
import com.xiefuzhong.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ContactsController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.service(req, resp);
        System.out.println("进入到联系人控制器");

        String path = request.getServletPath();

//        if("/workbench/transaction/aa.do".equals(path)){
//            add(request,response);


//        }else  if("/workbench/transaction/aa.do".equals(path)){
//            getActivityListByName(request,response);

//        }
    }



}
