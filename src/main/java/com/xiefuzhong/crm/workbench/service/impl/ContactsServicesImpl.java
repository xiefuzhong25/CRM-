package com.xiefuzhong.crm.workbench.service.impl;

import com.xiefuzhong.crm.utils.SqlSessionUtil;
import com.xiefuzhong.crm.workbench.dao.ContactsActivityRelationDao;
import com.xiefuzhong.crm.workbench.dao.ContactsDao;
import com.xiefuzhong.crm.workbench.dao.ContactsRemarkDao;
import com.xiefuzhong.crm.workbench.domain.Contacts;
import com.xiefuzhong.crm.workbench.service.ContactsService;

import java.util.List;

public class ContactsServicesImpl implements ContactsService {

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao  contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    @Override
    public List<Contacts> getContactsListByFullName(String aFullName) {
       List<Contacts>  cList = contactsDao.getContactsListByFullName(aFullName);
        return cList;
    }
}
