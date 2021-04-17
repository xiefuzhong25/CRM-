package com.xiefuzhong.crm.workbench.service.impl;

import com.xiefuzhong.crm.utils.SqlSessionUtil;
import com.xiefuzhong.crm.workbench.dao.TranDao;
import com.xiefuzhong.crm.workbench.dao.TranHistoryDao;
import com.xiefuzhong.crm.workbench.domain.TranHistory;
import com.xiefuzhong.crm.workbench.service.TranService;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranTranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);


}
