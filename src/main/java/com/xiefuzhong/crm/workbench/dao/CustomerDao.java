package com.xiefuzhong.crm.workbench.dao;

import com.xiefuzhong.crm.workbench.domain.Customer;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer cus);
}
