package com.xiefuzhong.crm.workbench.web.controller;

import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.settings.service.UserService;
import com.xiefuzhong.crm.settings.service.impl.UserServiceImpl;
import com.xiefuzhong.crm.utils.DateTimeUtil;
import com.xiefuzhong.crm.utils.PrintJson;
import com.xiefuzhong.crm.utils.ServiceFactory;
import com.xiefuzhong.crm.utils.UUIDUtil;
import com.xiefuzhong.crm.workbench.domain.*;
import com.xiefuzhong.crm.workbench.service.*;
import com.xiefuzhong.crm.workbench.service.impl.*;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import javax.print.ServiceUIFactory;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.service(req, resp);
        System.out.println("进入到交易控制器");

        String path = request.getServletPath();

        if("/workbench/transaction/add.do".equals(path)){
            add(request,response);


        }else  if("/workbench/transaction/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);

        }else  if("/workbench/transaction/getContactsListByFullName.do".equals(path)){
            getContactsListByFullName(request,response);

        }else  if("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);

        }

    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得客户名称列表（按照客户名称进行模糊查询）");

        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> sList = cs.getCustomerName(name);
        PrintJson.printJsonObj(response,sList);

    }

    private void getContactsListByFullName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查找联系人模态操作（根据联系人模糊查）");
        String aFullName = request.getParameter("aFullName");
        System.out.println(aFullName);
        ContactsService tr = (ContactsService) ServiceFactory.getService(new ContactsServicesImpl());
         List<Contacts>  cList = tr.getContactsListByFullName(aFullName);

         PrintJson.printJsonObj(response,cList);
    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查找市场活动列表（根据名称模糊查）");
        String aname = request.getParameter("aname");
        ActivityService  as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity>  aList = as.getActivityListByName(aname);
        PrintJson.printJsonObj(response,aList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到跳转到交易添加页的操作[传统请求request域存值搭转发]");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User>  uList = us.getUserList();
        request.setAttribute("uList",uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);

    }


}
