package com.xiefuzhong.crm.workbench.service;

import com.xiefuzhong.crm.workbench.domain.Activity;
import com.xiefuzhong.crm.workbench.domain.ActivityRemark;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    boolean save(Activity a);

    PaginationVo<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids);


    Map<String, Object> getUserListAndActivity(String id);

    boolean update(Activity a);

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAid( String activityId);

    boolean deleteRemark(String id);

    boolean saveRemark(ActivityRemark ar);
}
