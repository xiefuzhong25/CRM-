package com.xiefuzhong.crm.workbench.dao;

import com.xiefuzhong.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {



    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String activityId);

    int deleteRemarkById(String id);

    int saveRemark(ActivityRemark ar);
}
