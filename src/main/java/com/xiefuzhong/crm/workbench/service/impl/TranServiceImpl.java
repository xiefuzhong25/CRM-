package com.xiefuzhong.crm.workbench.service.impl;

import com.xiefuzhong.crm.utils.DateTimeUtil;
import com.xiefuzhong.crm.utils.SqlSessionUtil;
import com.xiefuzhong.crm.utils.UUIDUtil;
import com.xiefuzhong.crm.workbench.dao.CustomerDao;
import com.xiefuzhong.crm.workbench.dao.TranDao;
import com.xiefuzhong.crm.workbench.dao.TranHistoryDao;
import com.xiefuzhong.crm.workbench.domain.Customer;
import com.xiefuzhong.crm.workbench.domain.Tran;
import com.xiefuzhong.crm.workbench.domain.TranHistory;
import com.xiefuzhong.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tr, String customerName) {
        boolean flag= true;
        //精确判断客户名称是否存在，没有就创建拿到id,有的话就直接拿到id
         Customer   cus = customerDao.getCustomerByName(customerName);
         if (cus==null){
               cus = new Customer();
               cus.setId(UUIDUtil.getUUID());
               cus.setName(customerName);
               cus.setNextContactTime(tr.getNextContactTime());
               cus.setOwner(tr.getOwner());
               cus.setContactSummary(tr.getContactSummary());
               cus.setCreateBy(tr.getCreateBy());
               cus.setCreateTime(DateTimeUtil.getSysTime());

               //添加客户
            int count1 =  customerDao.save(cus);
            if (count1!=1){
                flag = false;
            }

         }
         tr.setCustomerId(cus.getId());

         //t添加交易
         int count2 = tranDao.save(tr);
         if (count2!=1){
             flag = false;
         }

         //创建交易历史
        TranHistory th = new TranHistory();
         th.setId(UUIDUtil.getUUID());
         th.setMoney(tr.getMoney());
         th.setStage(tr.getStage());
         th.setTranId(tr.getId());
         th.setCreateBy(tr.getCreateBy());
         th.setCreateTime(DateTimeUtil.getSysTime());
         th.setExpectedDate(tr.getExpectedDate());
       int count3 = tranHistoryDao.save(th);
       if (count3!=1){
           flag = false;
       }
        return flag;
    }

    @Override
    public Tran detail(String id) {

        Tran t = tranDao.detail(id);
        return t;
    }

    @Override
    public List<TranHistory> showHistoryList(String tranId) {
        List<TranHistory> thList = tranHistoryDao.showHistoryList(tranId);
        return thList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;
        int count1 = tranDao.changeStage(t);
        if (count1!=1){
            flag = false;
        }

        //交易阶段改变后，生成一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        th.setStage(t.getStage());

        int count2 = tranHistoryDao.save(th);
        if (count2!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        //取得total
       int total =  tranDao.getTotal();
        //取得dataList
        List<Map<String,Object>>  dataList = tranDao.getCharts();
        //将total和dataList保存到map中
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);

        return map;
    }


}
