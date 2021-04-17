package com.xiefuzhong.crm.workbench.service;

import com.xiefuzhong.crm.workbench.domain.Contacts;

import java.util.List;

public interface ContactsService {
    List<Contacts> getContactsListByFullName(String aFullName);
}
