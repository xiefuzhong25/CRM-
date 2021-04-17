package com.xiefuzhong.crm.web.listener;

import com.xiefuzhong.crm.settings.domain.DicValue;
import com.xiefuzhong.crm.settings.service.DicService;
import com.xiefuzhong.crm.settings.service.impl.DicServiceImpl;
import com.xiefuzhong.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {

    /*
    该方法是用来监听上下文域对象的方法，当服务器启动，上下文域对象创建
    对象创建完毕后，马上执行该方法

    event：该参数能够取得监听的对象
            监听的是什么对象，就可以通过该参数能取得什么对象
            例如我们现在监听的是上下文域对象，通过该参数就可以取得上下文域对象

     */

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("服务器缓存数据处理数据字典开始");

        // System.out.println("上下文域对象创建了");

        ServletContext application = event.getServletContext();

//取数据字典
        //调用业务层，问自己需要向业务层要什么呢【以前是控制器，只不过现在是监听器】
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        /*
        应该管业务层要 7个list
        [数据库现有有7个不同类型的数据】

        可以打包成一个map【可以list包裹map，或者map包裹list}
            业务层应该这样保存数据：
                map.put("appellation",dvList1);
                map.put("clueState",dvList2);
                map.put("stageList",dvList3);
                ....
         */
        Map<String, List<DicValue>> map = ds.getAll();
        //将map解析为上下域对象中保存的键值对
        Set<String> set = map.keySet();
        for (String key:set){
            //application.setAttribute(key,数据字典);
            application.setAttribute(key,map.get(key));
        }

        System.out.println("服务器缓存数据处理数据字典结束");

//==========================================================================================

        /**
         * 处理stage2Possibility.properties文件
         *  步骤：
         *      解析该文件，将该属性文件中的键值对关系处理成java中的键值对关系（map）
         *      map<String(阶段stage),String（可能性possibility）>  pMap=...
         *      pMap.put(key,value)
         *      ....
         *
         *      pMap保存值之后，放在服务器缓存中
         *      application.setAttribute("pMap",pMap);
         *
         *
         */

        //解析properties文件[注意.properties后缀记得删掉]
        Map<String,String> pMap = new  HashMap<String,String>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
         Enumeration<String> e = rb.getKeys();

         //使用迭代器【效率高，比其他for效率高】
         while (e.hasMoreElements()){

             //阶段
             String key = e.nextElement();
             //可能性
             String value = rb.getString(key);

             pMap.put(key,value);
         }
         //将pMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);
    }


}
