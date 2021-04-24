package com.xiefuzhong.crm.workbench.service;

import com.xiefuzhong.crm.workbench.domain.Contacts;
import com.xiefuzhong.crm.workbench.domain.Tran;
import com.xiefuzhong.crm.workbench.domain.TranHistory;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import java.util.List;
import java.util.Map;

public interface TranService {

    boolean save(Tran tr, String customerName);


    Tran detail(String id);

    List<TranHistory> showHistoryList(String tranId);

    boolean changeStage(Tran t);
}
