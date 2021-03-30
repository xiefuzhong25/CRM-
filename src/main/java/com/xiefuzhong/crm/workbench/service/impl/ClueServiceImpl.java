package com.xiefuzhong.crm.workbench.service.impl;

import com.xiefuzhong.crm.settings.dao.UserDao;
import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.utils.SqlSessionUtil;
import com.xiefuzhong.crm.workbench.dao.ClueDao;
import com.xiefuzhong.crm.workbench.domain.Activity;
import com.xiefuzhong.crm.workbench.domain.Clue;
import com.xiefuzhong.crm.workbench.service.ClueService;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import java.util.List;
import java.util.Map;


public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);


    @Override
    public boolean save(Clue c) {
        boolean flag= true;
        int count = clueDao.save(c);
        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVo<Clue> pageList(Map<String, Object> map) {
        //取得total
        int total = clueDao.getTotalByCondition(map);
        //取得dataList
        List<Clue> dataList = clueDao.getClueListByCondition(map);
        //将total和dataList封装到vo中
        PaginationVo<Clue> vo = new PaginationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        //将vo返回
        return vo;

    }

    @Override
    public Clue detail(String id) {
        Clue c = clueDao.detail(id);
        return c;
    }
}
