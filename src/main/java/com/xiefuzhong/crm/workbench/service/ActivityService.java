package com.xiefuzhong.crm.workbench.service;

import com.xiefuzhong.crm.workbench.domain.Activity;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import java.util.Map;

public interface ActivityService {
    boolean save(Activity a);

    PaginationVo<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids);


    Map<String, Object> getUserListAndActivity(String id);

    boolean update(Activity a);
}
