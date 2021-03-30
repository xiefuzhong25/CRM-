package com.xiefuzhong.crm.settings.service.impl;

import com.xiefuzhong.crm.settings.dao.DicTypeDao;
import com.xiefuzhong.crm.settings.dao.DicValueDao;
import com.xiefuzhong.crm.settings.domain.DicType;
import com.xiefuzhong.crm.settings.domain.DicValue;
import com.xiefuzhong.crm.settings.service.DicService;
import com.xiefuzhong.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {

    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, List<DicValue>> getAll() {
        //将后面处理完的数据保存下来到map中
        Map<String, List<DicValue>> map = new HashMap<>();

        //将字典类型列表取出
        List<DicType>  dtList = dicTypeDao.getTypeList();

        //将字典类型便历
        for (DicType dt: dtList){

            //取得每一种类型的字典类型编码
            String code = dt.getCode();
            //根据每一个字典类型来取的字典列表
            List<DicValue> dvList = dicValueDao.getListByCode(code);
            //存下来
             map.put(code,dvList);

        }

        return map;
    }
}
