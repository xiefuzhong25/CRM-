package com.xiefuzhong.crm.workbench.service.impl;

import com.xiefuzhong.crm.settings.dao.UserDao;
import com.xiefuzhong.crm.settings.domain.User;
import com.xiefuzhong.crm.utils.DateTimeUtil;
import com.xiefuzhong.crm.utils.SqlSessionUtil;
import com.xiefuzhong.crm.utils.UUIDUtil;
import com.xiefuzhong.crm.workbench.dao.*;
import com.xiefuzhong.crm.workbench.domain.*;
import com.xiefuzhong.crm.workbench.service.ClueService;
import com.xiefuzhong.crm.workbench.vo.PaginationVo;

import java.lang.ref.Cleaner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClueServiceImpl implements ClueService {

    //用户相关的表
    private UserDao  userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    //联系人相关表
    private  ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private  ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private  ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    //交易相关表
    private  TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

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

    @Override
    public boolean unbundById(String id) {
        boolean flag = true;
         int count = clueActivityRelationDao.unbundById(id);
         if (count!=1){
             flag=false;
         }
        return flag;
    }

    @Override
    public boolean bund(String cid, String[] aids) {
        boolean flag = true;
        for (String aid:aids){
            //取得每一个aid和cid做关联
            ClueActivityRelation car = new ClueActivityRelation();
            car.setActivityId(aid);
            car.setClueId(cid);
            car.setId(UUIDUtil.getUUID());
            int count = clueActivityRelationDao.bund(car);
            if (count!=1){
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        String createTime = DateTimeUtil.getSysTime();

        boolean flag = true;

        //第一步：通过线索id获取线索的对象（线索的对象当中封装了线索的信息）
        Clue c = clueDao.getById(clueId);
        //第二步：通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精准匹配，判断该客户是够存在）
        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        if (cus==null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setAddress(c.getAddress());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setName(company);
            cus.setDescription(c.getDescription());
            cus.setCreateBy(createBy);
            cus.setCreateTime(createTime);
            cus.setContactSummary(c.getContactSummary());

            //添加客户
             int count1 = customerDao.save(cus);
             if (count1!=1){
                 flag = false;
             }

        }

    //通过第二步处理，客户信息我们就一定拥有了，将来在处理其他表的时候，如果需要使用到客户的id,直接使用cus.getId();
        //第三步：通过线索对象提取联系人信息，保存联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getPhone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setDescription(c.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateBy(createBy);
        con.setCreateTime(createTime);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());
        //添加联系人
        int count2 = contactsDao.save(con);
        if (count2!=1){
            flag = false;
        }

      //通过第三步处理，联系人信息我们就一定拥有了，将来在处理其他表的时候，如果需要使用联系人的id,直接使用con.getId()
        //第四步：线索备注转换到客户备注以及联系人备注
        //查询出该线索关联的备注信息列表【根据clueId来查】
       List<ClueRemark>  clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        for (ClueRemark clueRemark : clueRemarkList){

            //取出每一条线索的备注
            String noteContent = clueRemark.getNoteContent();
            //创建客户备注对象，添加备注信息
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int count3 = customerRemarkDao.save(customerRemark);
            if (count3!=1){
                flag = false;
            }
            //创建联系人备注对象，添加备注信息
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4 = contactsRemarkDao.save(contactsRemark);
            if (count4!=1){
                flag = false;
            }
        }

        //第五步“线索和市场活动”的活动转换到“联系人和市场活动的关系”
        //查询出与该条线索关联的市场活动，查询与市场活动的相关联列表
        List<ClueActivityRelation> clueActivityRelationsList = clueActivityRelationDao.getListByClueId(clueId);
        //便历出每一条与市场活动关联的关系记录
        for (ClueActivityRelation clueActivityRelation: clueActivityRelationsList){

            //从每一条便历出来的记录中取出关联的市场活动id
            String activityId = clueActivityRelation.getActivityId();

            //创建 联系人与市场活动的关联对象 让第三步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(con.getId());
            contactsActivityRelation.setActivityId(activityId);
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5!= 1){
                flag = false;
            }
        }

        //第六步：如果有创建交易的需求，创建一条交易
        if (t!=null){
            /**
             * t对象在controller里面已经封装好的信息如下
             *      id money name expectedDate stage activity createBy createTime
             *
             *      接下来可以通过第一步生成的c对象取出一些信息，继续完善对t对象的封装【这一步可有可无，看客户要求】
             */

            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());

            //添加交易
          int count6 =   tranDao.save(t);
          if (count6!=1){
              flag = false;
          }

          //第七步：如果创建了交易，则创建一条该交易下的交易历史
          TranHistory th = new TranHistory();
          th.setId(UUIDUtil.getUUID());
          th.setCreateBy(createBy);
          th.setCreateTime(createTime);
          th.setExpectedDate(t.getExpectedDate());
          th.setMoney(t.getMoney());
          th.setStage(t.getStage());
          th.setTranId(t.getId());
          int count7 = tranHistoryDao.save(th);
          if (count7!=1){
              flag = false;
          }
        }

        //第八步：删除线索备注[tbl_clue_remark]
        for (ClueRemark clueRemark:clueRemarkList){

            int count8 = clueRemarkDao.delete(clueRemark);
            if (count8!=1){
                flag = false;
            }
        }

        //第九步：删除线索和市场活动关系
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationsList){

            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if (count9!=1){
                flag = false;
            }
        }
        //第十步：删除线索
        int count10 = clueDao.delete(clueId);
        if (count10!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndClue(String id) {
        //取用户uList
        List<User> uList = userDao.getUserList();
        //取得线索数据
        Clue  c = clueDao.getById(id);

        Map<String,Object>  map = new HashMap<>();
        map.put("uList",uList);
        map.put("c",c);

        return map;
    }

    @Override
    public boolean update(Clue c) {
        boolean flag= true;
        int count = clueDao.update(c);
        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean deleteByCids(String[] ids) {
        boolean flag = true;
        //查询出需要删除的备注的数量
        int count1 = clueRemarkDao.getCountByCids(ids);

        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = clueRemarkDao.deleteByCids(ids);

        if (count1 != count2) {
            flag = false;
        }

        //查询出线索关联的市场活动的数量
        int count3 = clueActivityRelationDao.getListCountByClueId(ids);
        //删除线索-市场活动表中的数据
        int count4 = clueActivityRelationDao.deleteClueAndActivityList(ids);
        if (count3 != count4){
            flag = false;
        }

        //删除线索
        int count5 = clueDao.deleteByCids(ids);
        if (count5 != ids.length) {
            flag = false;
        }

        return flag;
    }

    @Override
    public List<ClueRemark> getRemarkListByCid(String clueId) {
        List<ClueRemark>  crList = clueRemarkDao.getListByClueId(clueId);
        return crList;
    }

    @Override
    public boolean saveRemark(ClueRemark ar) {
        boolean flag = true;
        int count = clueRemarkDao.saveRemark(ar);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ClueRemark cr) {
        boolean flag = true;
        int count = clueRemarkDao.updateRemark(cr);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = clueRemarkDao.deleteRemark(id);
        if (count !=1){
            flag = false;
        }
        return  flag;
    }


}
