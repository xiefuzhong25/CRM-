package com.xiefuzhong.crm.workbench.web.controller;

import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.settings.service.UserService;
import com.xiefuzhong.crm.settings.service.impl.UserServiceImpl;
import com.xiefuzhong.crm.utils.*;
import com.xiefuzhong.crm.workbench.domain.Activity;
import com.xiefuzhong.crm.workbench.domain.ActivityRemark;
import com.xiefuzhong.crm.workbench.domain.Clue;
import com.xiefuzhong.crm.workbench.domain.ClueActivityRelation;
import com.xiefuzhong.crm.workbench.service.ActivityService;
import com.xiefuzhong.crm.workbench.service.ClueService;
import com.xiefuzhong.crm.workbench.service.impl.ActivityServiceImpl;
import com.xiefuzhong.crm.workbench.service.impl.ClueServiceImpl;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.service(req, resp);
        System.out.println("进入到线索控制器");

        String path = request.getServletPath();

        if("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);


        }else  if("/workbench/clue/save.do".equals(path)){
            save(request,response);

        }else  if("/workbench/clue/pageList.do".equals(path)){
            pageList(request,response);

        }else  if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);

        }else  if("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);

        }else  if("/workbench/clue/unbundById.do".equals(path)){
            unbundById(request,response);

        }else  if("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)){
            getActivityListByNameAndNotByClueId(request,response);

        }else  if("/workbench/clue/bund.do".equals(path)){
            bund(request,response);

        }

    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到关联市场活动功能按钮事件");

        String cid = request.getParameter("cid");
        //存一个数组变量
        String[] aids = request.getParameterValues("aid");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag= cs.bund(cid,aids);
        PrintJson.printJsonFlag(response,flag);


    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("查询市场活动列表，（根据名称模糊查询+排除掉已经关联指定线索的列表）");

        String  aname = request.getParameter("aname");
        String  clueId = request.getParameter("clueId");

        Map<String,String> map = new HashMap<>();
        map.put("aname",aname);
        map.put("clueId",clueId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByNameAndNotByClueId(map);
        PrintJson.printJsonObj(response,aList);

    }


    private void unbundById(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("解除关联操作");
        String id =  request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
         boolean flag = cs.unbundById(id);
         PrintJson.printJsonFlag(response,flag);

    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到市场活动关联功能");

        String clueId = request.getParameter("clueId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,aList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到跳转到详细信息页的操作,使用传统请求方式");

        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue  c= cs.detail(id);
        request.setAttribute("c",c);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);

    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到线索信息的操作（结合条件查询+分页查询）");


        String pageNoStr = request.getParameter("pageNo");
        int pageNo=Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算略过的记录数,sql语句需要的是这个参数 XXX limit  skipCount,pageSize
        int skipCount = (pageNo-1)*pageSize;

        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String source = request.getParameter("source");
        String createBy = request.getParameter("createBy");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");


        Map<String,Object>  map = new HashMap<>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("phone",phone);
        map.put("source",source);
        map.put("createBy",createBy);
        map.put("mphone",mphone);
        map.put("state",state);



        ClueService  as = (ClueService) ServiceFactory.getService( new ClueServiceImpl());

        //因为业务层将数据封装在了vo类中，所以返回的数据类型是这个vo类
        PaginationVo<Clue> vo = as.pageList(map);
        //将数据以json形式返回前端  {"total":100,"dataList":[{市场活动1}，{2}，{3}
        PrintJson.printJsonObj(response,vo);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行线索的添加操作");

       String id =UUIDUtil.getUUID();
       String fullname =request.getParameter("fullname");
       String appellation =request.getParameter("appellation");
       String owner =request.getParameter("owner");
       String company =request.getParameter("company");
       String job =request.getParameter("job");
       String email =request.getParameter("email");
       String phone =request.getParameter("phone");
       String website =request.getParameter("website");
       String mphone =request.getParameter("mphone");
       String state =request.getParameter("state");
       String source =request.getParameter("source");
       String createBy =((User)request.getSession().getAttribute("user")).getName();
       String createTime =DateTimeUtil.getSysTime();
       String editBy =request.getParameter("editBy");
       String editTime =request.getParameter("editTime");
       String description =request.getParameter("description");
       String contactSummary =request.getParameter("contactSummary");
       String nextContactTime =request.getParameter("nextContactTime");
       String address =request.getParameter("address");

        Clue c = new Clue();
        c.setAddress(address);
        c.setNextContactTime(nextContactTime);
        c.setContactSummary(contactSummary);
        c.setDescription(description);
        c.setEditBy(editBy);
        c.setEditTime(editTime);
        c.setCreateTime(createTime);
        c.setCreateBy(createBy);
        c.setSource(source);
        c.setState(state);
        c.setMphone(mphone);
        c.setWebsite(website);
        c.setPhone(phone);
        c.setEmail(email);
        c.setJob(job);
        c.setCompany(company);
        c.setOwner(owner);
        c.setAppellation(appellation);
        c.setFullname(fullname);
        c.setId(id);


      ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
      boolean flag = cs.save(c);

//      Map<String,Object> map = new HashMap<>();
//      map.put("success",flag);
//      map.put("c",c);

//     PrintJson.printJsonObj(response,map);
    PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到 取得用户信息列表");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList =  us.getUserList();
        PrintJson.printJsonObj(response,uList);


    }





}
