package com.xiefuzhong.crm.workbench.service.impl;

import com.xiefuzhong.crm.settings.dao.UserDao;
import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.utils.SqlSessionUtil;
import com.xiefuzhong.crm.workbench.dao.ActivityDao;
import com.xiefuzhong.crm.workbench.dao.ActivityRemarkDao;
import com.xiefuzhong.crm.workbench.domain.Activity;
import com.xiefuzhong.crm.workbench.domain.ActivityRemark;
import com.xiefuzhong.crm.workbench.service.ActivityService;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);

    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);

    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);


    @Override
    public boolean save(Activity a) {
        boolean flag = true;
        int count = activityDao.save(a);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVo<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取得dataList
        List<Activity> dataList = activityDao.getActivityListByCondition(map);
        //将total和dataList封装到vo中
        PaginationVo<Activity> vo = new PaginationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        //将vo返回
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        //查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);

        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = activityRemarkDao.deleteByAids(ids);


        if (count1 != count2) {
            flag = false;
        }
        //删除市场活动
        int count3 = activityDao.delete(ids);
        if (count3 != ids.length) {
            flag = false;
        }


        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //取用户uList
        List<User> uList = userDao.getUserList();

        //取a
        Activity a = activityDao.getById(id);
        //返回map就行
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uList", uList);
        map.put("a", a);
        return map;
    }

    @Override
    public boolean update(Activity a) {
        boolean flag = true;
        int count = activityDao.update(a);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> arList = activityRemarkDao.getRemarkListByAid(activityId);
        return arList;
    }

    @Override
    public boolean deleteRemark(String id) {

        boolean flag = true;
        int count = activityRemarkDao.deleteRemarkById(id);
        if (count != 1) {
            flag = false;

        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(ar);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(ar);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> aList = activityDao.getActivityListByClueId(clueId);

        return aList;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {
        List<Activity> aList = activityDao.getActivityListByNameAndNotByClueId(map);
        return aList;
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        List<Activity> aList = activityDao.getActivityListByName(aname);

        return aList;
    }


}
