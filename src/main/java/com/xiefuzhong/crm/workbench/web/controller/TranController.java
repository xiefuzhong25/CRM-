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
import javax.servlet.ServletContext;
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

        }else  if("/workbench/transaction/save.do".equals(path)){
            save(request,response);

        }else  if("/workbench/transaction/detail.do".equals(path)){
            detail(request,response);

        }else  if("/workbench/transaction/showHistoryList.do".equals(path)){
            showHistoryList(request,response);

        }else  if("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);

        }


    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行改变阶段操作");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
         boolean flag  = ts.changeStage(t);

         //处理可能性[从application域中取得pMap,阶段与可能性的对应关系]
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
         String possibility = pMap.get(stage);

         Map<String,Object> map = new HashMap<>();
         map.put("success",flag);
         map.put("t",t);
         map.put("possibility",possibility);

         PrintJson.printJsonObj(response,map);


    }

    private void showHistoryList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到刷新历史阶段列表");
        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory>  thList = ts.showHistoryList(tranId);

        //将交易列表便历
            //取得application中pMap，得到阶段和可能性的对应关系
        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");

        for (TranHistory th : thList){
            //根据每一条交易历史，取出每个阶段
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            th.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response,thList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("跳转到详细信息页");
        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran   t = ts.detail(id);
        //处理可能性
        /**
         * 阶段t
         * 阶段和可能性之间的对应关系 pMap
         *      pMap在服务器缓存中，直接用用不了
         *          但是可以间接使用[三种方式]
         *            ServletContext  application1 =   this.getServletContext();
         *             ServletContext  application2=   request.getServletContext();
         *              ServletContext  application3 =   this.getServletConfig().getServletContext();
         *
         */
        String stage = t.getStage();
        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(stage);


        request.setAttribute("t",t);
        request.setAttribute("possibility",possibility);

        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }


    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("进入到保存交易");

        String  id = UUIDUtil.getUUID();
        String  owner = request.getParameter("owner");
        String  money = request.getParameter("money");
        String  name = request.getParameter("name");
        String  expectedDate = request.getParameter("expectedDate");
        String  customerName = request.getParameter("customerName"); //因为没有id，所以先把名字存下来后面再判断
        String  stage = request.getParameter("stage");
        String  type = request.getParameter("type");
        String  source = request.getParameter("source");
        String  activityId = request.getParameter("activityId");
        String  contactsId = request.getParameter("contactsId");
        String  createBy = ((User)request.getSession().getAttribute("user")).getName();
        String  createTime = DateTimeUtil.getSysTime();
        String  description = request.getParameter("description");
        String  contactSummary = request.getParameter("contactSummary");
        String  nextContactTime = request.getParameter("nextContactTime");

        Tran tr = new Tran();
        tr.setActivityId(activityId);
        tr.setContactsId(contactsId);
        tr.setContactSummary(contactSummary);
        tr.setCreateBy(createBy);
        tr.setCreateTime(createTime);
        tr.setDescription(description);
        tr.setExpectedDate(expectedDate);
        tr.setId(id);
        tr.setMoney(money);
        tr.setName(name);
        tr.setNextContactTime(nextContactTime);
        tr.setOwner(owner);
        tr.setSource(source);
        tr.setStage(stage);
        tr.setType(type);

        TranService  ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.save(tr,customerName);
         if(flag){
             //重定向跳转【因为地址栏要变，而且不用使用request存值】
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
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
