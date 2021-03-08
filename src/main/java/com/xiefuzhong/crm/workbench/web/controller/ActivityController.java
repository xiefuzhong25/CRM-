package com.xiefuzhong.crm.workbench.web.controller;

import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.settings.service.UserService;
import com.xiefuzhong.crm.settings.service.impl.UserServiceImpl;
import com.xiefuzhong.crm.utils.*;
import com.xiefuzhong.crm.workbench.domain.Activity;
import com.xiefuzhong.crm.workbench.service.ActivityService;
import com.xiefuzhong.crm.workbench.service.impl.ActivityServiceImpl;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;
import org.apache.ibatis.reflection.SystemMetaObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.service(req, resp);
        System.out.println("进入到市场活动控制器");

        String path = request.getServletPath();

        if("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);


        }else  if("/workbench/activity/save.do".equals(path)){
            save(request,response);

        }else  if("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);

        }else  if("/workbench/activity/delete.do".equals(path)){
            delete(request,response);

        }else  if("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);

        }else  if("/workbench/activity/update.do".equals(path)){
            update(request,response);

        }else  if("/workbench/activity/detail.do".equals(path)){
            detail(request,response);

        }
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到详细信息页的操作,使用传统请求方式");
        String id = request.getParameter("id");
       ActivityService as= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
       Activity a =as.detail(id);
       //使用传统方式来实现功能，使用转发或者重定向的方式
        //使用request域就足够来存数据
        // 想要使用er表达式取request域取值就必须要用转发
        // 路径写法不用/项目名，因为是内部路径
        request.setAttribute("a",a);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);

    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入市场活动的修改中的模态窗口中的更新操作");
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //修改时间,当前的系统时间createTime
        String editTime = DateTimeUtil.getSysTime();
        //从session域中获取当前修改用户
        String editBy =((User)request.getSession().getAttribute("user")).getName();

        //封装上面这些参数 方便 后面传进方法中当参数
        Activity  a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setEditTime(editTime);
        a.setEditBy(editBy);


        ActivityService  as = (ActivityService) ServiceFactory.getService( new ActivityServiceImpl());
        boolean flag = as.update(a);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询用户信息列表和市场活动id查询单条记录的操作");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //前端要什么就返回啥 uList     a
        //复用率太低直接使用map  处理过程叫业务层去做
        Map<String,Object> map = as.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);

    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动的删除操作");
        String ids[] = request.getParameterValues("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag =  as.delete(ids);
        PrintJson.printJsonFlag(response,flag);


    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询市场活动信息的操作（结合条件查询+分页查询）");


        String pageNoStr = request.getParameter("pageNo");
             int pageNo=Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
             int pageSize = Integer.valueOf(pageSizeStr);
        //计算略过的记录数,sql语句需要的是这个参数 XXX limit  skipCount,pageSize
        int skipCount = (pageNo-1)*pageSize;

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");


        Map<String,Object>  map = new HashMap<>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);

        ActivityService  as = (ActivityService) ServiceFactory.getService( new ActivityServiceImpl());
        /*
        前端需要：市场活动信息列表
                    查询的总条数
         业务层拿到了以上两项信息之后，就要做返回给前端，可以使用两种形式map  或者vo
                map
                    map.put("dataList":dataList)
                    map.put("total":total)
                    printJson map-->json
                    {"total":100,"dataList":[{市场活动1}，{2}，{3}]

                vo
                PaginationVo<T>{
                    private int total;
                    private List<T> dataList;
                    }
                    //使用
                    PaginationVo<Activity> vo = new PaginationVo<>;
                    vo.setTotal(total);
                    vo.setDataList(dataList);
                    PrintJson vo --->json
                     {"total":100,"dataList":[{市场活动1}，{2}，{3}]
               将来分页查询，每个模块都有，所有我们选择使用一个通用的vo，操作起来比较方便
         */

        //因为业务层将数据封装在了vo类中，所以返回的数据类型是这个vo类
        PaginationVo<Activity> vo = as.pageList(map);
        //将数据以json形式返回前端  {"total":100,"dataList":[{市场活动1}，{2}，{3}
        PrintJson.printJsonObj(response,vo);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动的添加操作");

      String id = UUIDUtil.getUUID();
      String owner = request.getParameter("owner");
      String name = request.getParameter("name");
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");
      String cost = request.getParameter("cost");
      String description = request.getParameter("description");
      //创建时间,当前的系统时间createTime
      String createTime = DateTimeUtil.getSysTime();
      //从session域中获取当前登录用户
      String createBy =((User)request.getSession().getAttribute("user")).getName();

        //封装上面这些参数 方便 后面传进方法中当参数
        Activity  a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);


        ActivityService  as = (ActivityService) ServiceFactory.getService( new ActivityServiceImpl());
        boolean flag = as.save(a);
        PrintJson.printJsonFlag(response,flag);


    }


    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得用户信息列表");
        UserService us = (UserService) ServiceFactory.getService( new UserServiceImpl());
        List<User> uList = us.getUserList();
        //将得到的结果解析成json形式
        PrintJson.printJsonObj(response,uList);
    }


}
