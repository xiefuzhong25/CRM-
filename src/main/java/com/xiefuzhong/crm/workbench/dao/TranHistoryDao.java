package com.xiefuzhong.crm.workbench.dao;

import com.xiefuzhong.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int save(TranHistory th);

    List<TranHistory> showHistoryList(String tranId);
}
