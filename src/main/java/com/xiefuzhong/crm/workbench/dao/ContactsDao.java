package com.xiefuzhong.crm.workbench.dao;

import com.xiefuzhong.crm.workbench.domain.Contacts;

import java.util.List;

public interface ContactsDao {

    int save(Contacts con);

    List<Contacts> getContactsListByFullName(String aFullName);
}
