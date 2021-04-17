package com.xiefuzhong.crm.workbench.service.impl;

import com.xiefuzhong.crm.utils.SqlSessionUtil;
import com.xiefuzhong.crm.workbench.dao.CustomerDao;
import com.xiefuzhong.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerName(String name) {
        List<String>  sList = customerDao.getCustomerName(name);
        return sList;
    }
}
