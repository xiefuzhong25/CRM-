package com.xiefuzhong.crm.workbench.service;


import com.xiefuzhong.crm.workbench.domain.Clue;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import java.util.Map;

public interface ClueService {

    boolean save(Clue c);

    PaginationVo<Clue> pageList(Map<String, Object> map);

    Clue detail(String id);
}
