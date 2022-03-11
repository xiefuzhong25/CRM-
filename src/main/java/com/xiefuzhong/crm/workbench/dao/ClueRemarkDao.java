package com.xiefuzhong.crm.workbench.dao;

import com.xiefuzhong.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    List<ClueRemark> getListByClueId(String clueId);

    int delete(ClueRemark clueRemark);

    int getCountByCids(String[] ids);

    int deleteByCids(String[] ids);

    int saveRemark(ClueRemark ar);

    int updateRemark(ClueRemark cr);

    int deleteRemark(String id);
}
