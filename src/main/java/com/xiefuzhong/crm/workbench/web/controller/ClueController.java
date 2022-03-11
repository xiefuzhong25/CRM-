package com.xiefuzhong.crm.workbench.web.controller;

import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.settings.service.UserService;
import com.xiefuzhong.crm.settings.service.impl.UserServiceImpl;
import com.xiefuzhong.crm.utils.*;
import com.xiefuzhong.crm.workbench.domain.*;
import com.xiefuzhong.crm.workbench.service.ActivityService;
import com.xiefuzhong.crm.workbench.service.ClueService;
import com.xiefuzhong.crm.workbench.service.impl.ActivityServiceImpl;
import com.xiefuzhong.crm.workbench.service.impl.ClueServiceImpl;
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

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.service(req, resp);
        System.out.println("进入到线索控制器");

        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)) {
            getUserList(request, response);


        } else if ("/workbench/clue/save.do".equals(path)) {
            save(request, response);

        } else if ("/workbench/clue/pageList.do".equals(path)) {
            pageList(request, response);

        } else if ("/workbench/clue/detail.do".equals(path)) {
            detail(request, response);

        } else if ("/workbench/clue/getActivityListByClueId.do".equals(path)) {
            getActivityListByClueId(request, response);

        } else if ("/workbench/clue/unbundById.do".equals(path)) {
            unbundById(request, response);

        } else if ("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)) {
            getActivityListByNameAndNotByClueId(request, response);

        } else if ("/workbench/clue/bund.do".equals(path)) {
            bund(request, response);

        } else if ("/workbench/clue/getActivityListByName.do".equals(path)) {
            getActivityListByName(request, response);

        } else if ("/workbench/clue/convert.do".equals(path)) {
            convert(request, response);

        } else if ("/workbench/clue/getUserListAndClue.do".equals(path)) {
            getUserListAndClue(request, response);

        } else if ("/workbench/clue/update.do".equals(path)) {
            update(request, response);

        } else if ("/workbench/clue/deleteByCids.do".equals(path)) {
            deleteByCids(request, response);

        } else if ("/workbench/clue/getRemarkListByCid.do".equals(path)) {
            getRemarkListByCid(request, response);

        } else if ("/workbench/clue/saveRemark.do".equals(path)) {
            saveRemark(request, response);

        } else if ("/workbench/clue/updateRemark.do".equals(path)) {
            updateRemark(request, response);

        } else if ("/workbench/clue/deleteRemark.do".equals(path)) {
            deleteRemark(request, response);

        }

    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据id,进行到删除备注操作");

        String id = request.getParameter("id");

        ClueService as = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = as.deleteRemark(id);

        PrintJson.printJsonFlag(response, flag);
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到编辑修改备注的模态窗口");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ClueRemark cr = new ClueRemark();
        cr.setId(id);
        cr.setNoteContent(noteContent);
        cr.setEditTime(editTime);
        cr.setEditBy(editBy);
        cr.setEditFlag(editFlag);

        ClueService as = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = as.updateRemark(cr);

        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("ar", cr);

        PrintJson.printJsonObj(response, map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到添加备注操作");

        String noteContent = request.getParameter("noteContent");
        String clueId = request.getParameter("clueId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        ClueRemark ar = new ClueRemark();
        ar.setNoteContent(noteContent);
        ar.setClueId(clueId);
        ar.setId(id);
        ar.setCreateTime(createTime);
        ar.setEditBy(createBy);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);

        ClueService as = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = as.saveRemark(ar);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", flag);
        map.put("ar", ar);

        PrintJson.printJsonObj(response, map);
    }

    private void getRemarkListByCid(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据线索id，取得备注信息列表");
        String clueId = request.getParameter("clueId");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        List<ClueRemark> crList = cs.getRemarkListByCid(clueId);
        PrintJson.printJsonObj(response, crList);

    }

    private void deleteByCids(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行线索模块中的删除操作");
        String ids[] = request.getParameterValues("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.deleteByCids(ids);
        PrintJson.printJsonFlag(response, flag);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行线索的更新操作");

        String id = request.getParameter("id");
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue c = new Clue();
        c.setId(id);
        c.setAddress(address);
        c.setNextContactTime(nextContactTime);
        c.setContactSummary(contactSummary);
        c.setDescription(description);
        c.setEditBy(editBy);
        c.setEditTime(editTime);
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


        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.update(c);

        PrintJson.printJsonFlag(response, flag);
    }

    private void getUserListAndClue(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询用户信息列表和通过线索的id查询单条记录的操作");
        String id = request.getParameter("id");
        //调业务层
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        //前端要什么就返回啥 uList     c
        //复用率太低直接使用map  处理过程叫业务层去做
        Map<String, Object> map = cs.getUserListAndClue(id);
        PrintJson.printJsonObj(response, map);

    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("进入到执行线索转换操作");

        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");

        Tran t = null;

        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        //如果需要创建交易
        if ("a".equals(flag)) {
            t = new Tran();
            //接收交易表中的参数
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectDate = request.getParameter("expectDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();


            t.setActivityId(activityId);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectDate);
            t.setStage(stage);
            t.setId(id);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);

        }
        //调业务层
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag1 = cs.convert(clueId, t, createBy);
        //因为使用的是传统请求，所以作响应应该使用重定向或者转发
        //使用request存值一般使用转发，但一般我们玩的都是重定向
        if (flag1) {

            response.sendRedirect(request.getContextPath() + "/workbench/clue/index.jsp");
        }


    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("查询市场活动列表（根据名称模糊查）");
        String aname = request.getParameter("aname");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByName(aname);
        PrintJson.printJsonObj(response, aList);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到关联市场活动功能按钮事件");

        String cid = request.getParameter("cid");
        //存一个数组变量
        String[] aids = request.getParameterValues("aid");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.bund(cid, aids);
        PrintJson.printJsonFlag(response, flag);


    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("查询市场活动列表，（根据名称模糊查询+排除掉已经关联指定线索的列表）");

        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        Map<String, String> map = new HashMap<>();
        map.put("aname", aname);
        map.put("clueId", clueId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByNameAndNotByClueId(map);
        PrintJson.printJsonObj(response, aList);

    }


    private void unbundById(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("解除关联操作");
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.unbundById(id);
        PrintJson.printJsonFlag(response, flag);

    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到市场活动关联功能");

        String clueId = request.getParameter("clueId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response, aList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到跳转到详细信息页的操作,使用传统请求方式");

        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue c = cs.detail(id);
        request.setAttribute("c", c);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request, response);

    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到线索信息的操作（结合条件查询+分页查询）");


        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算略过的记录数,sql语句需要的是这个参数 XXX limit  skipCount,pageSize
        int skipCount = (pageNo - 1) * pageSize;

        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String source = request.getParameter("source");
        String createBy = request.getParameter("createBy");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");


        Map<String, Object> map = new HashMap<>();
        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);
        map.put("fullname", fullname);
        map.put("company", company);
        map.put("phone", phone);
        map.put("source", source);
        map.put("createBy", createBy);
        map.put("mphone", mphone);
        map.put("state", state);


        ClueService as = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        //因为业务层将数据封装在了vo类中，所以返回的数据类型是这个vo类
        PaginationVo<Clue> vo = as.pageList(map);
        //将数据以json形式返回前端  {"total":100,"dataList":[{市场活动1}，{2}，{3}
        PrintJson.printJsonObj(response, vo);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行线索的添加操作");

        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String editBy = request.getParameter("editBy");
        String editTime = request.getParameter("editTime");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

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

        PrintJson.printJsonFlag(response, flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到 取得用户信息列表");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();
        PrintJson.printJsonObj(response, uList);


    }


}
