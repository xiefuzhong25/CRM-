package com.xiefuzhong.crm.workbench.dao;

import com.xiefuzhong.crm.workbench.domain.ClueActivityRelation;

public interface ClueActivityRelationDao {


    int unbundById(String id);

    int bund(ClueActivityRelation car);
}
