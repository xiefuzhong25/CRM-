package com.xiefuzhong.crm.settings.dao;

import com.xiefuzhong.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getListByCode(String code);
}
