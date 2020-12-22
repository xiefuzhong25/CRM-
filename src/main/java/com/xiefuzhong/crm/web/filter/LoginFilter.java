package com.xiefuzhong.crm.web.filter;

import com.xiefuzhong.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入到验证有没有登录的页面");
        //ServletRequest是httpServletRequest的父亲
        // ServletRequest中没有getSession()方法,儿子中有
        //上级转下级要强制（强转）   下级转上级不用强转
        //父亲ServletRequest 做不到 所以要变成儿子来做
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //处理刚开始登录的时候的必要展的现资源页面 放行
        //获取前后端资源路径
        String path = request.getServletPath();
        if("/login.jsp".equals(path)||"/settings/user/login.do".equals(path)){
            chain.doFilter(req,resp);

        }else{
            //拿到服务器中session域中的用户数据，
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user!=null){
                //存在用户则放行
                chain.doFilter(req,resp);
            }else{
            /*
            请求转发 的路径写法
                使用一种特殊的绝对路径的使用方法，这种绝对路径前面不加 /项目名，这种路径也称为内部路径
             重定向  的路径写法
                使用传统的绝对路径写法，前面必须以  【 “/项目名开头，后面跟具体的资源路径”】
             */
                //重定向到登录叶
                //获取项目名request.getContextPath()
                System.out.println("获取地址");
                System.out.println(request.getContextPath());
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }

        }


    }
}
