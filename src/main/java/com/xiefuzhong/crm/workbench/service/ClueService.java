package com.xiefuzhong.crm.workbench.service;


import com.xiefuzhong.crm.workbench.domain.Clue;
import com.xiefuzhong.crm.workbench.domain.ClueRemark;
import com.xiefuzhong.crm.workbench.domain.Tran;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import java.util.List;
import java.util.Map;

public interface ClueService {

    boolean save(Clue c);

    PaginationVo<Clue> pageList(Map<String, Object> map);

    Clue detail(String id);

    boolean unbundById(String id);

    boolean bund(String cid, String[] aids);

    boolean convert(String clueId, Tran t, String createBy);

    Map<String, Object> getUserListAndClue(String id);

    boolean update(Clue c);

    boolean deleteByCids(String[] ids);

    List<ClueRemark> getRemarkListByCid(String clueId);

    boolean saveRemark(ClueRemark ar);

    boolean updateRemark(ClueRemark cr);

    boolean deleteRemark(String id);
}
