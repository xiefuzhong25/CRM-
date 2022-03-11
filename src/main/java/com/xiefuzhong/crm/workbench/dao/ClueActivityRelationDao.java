package com.xiefuzhong.crm.workbench.dao;

import com.xiefuzhong.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    int unbundById(String id);

    int bund(ClueActivityRelation car);

    List<ClueActivityRelation> getListByClueId(String clueId);



    int delete(ClueActivityRelation clueActivityRelation);

    int getListCountByClueId(String[] ids);

    int deleteClueAndActivityList(String[] ids);
}
