package com.xiefuzhong.crm.workbench.dao;

import com.xiefuzhong.crm.workbench.domain.Tran;


public interface TranDao {

    int save(Tran t);


    Tran detail(String id);
}
