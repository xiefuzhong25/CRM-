<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/10/12 0012
  Time: 18:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>
</head>
<body>

    $.ajax({
            url:"",
            data :{

            } ,
            type : "",
            dataType : "json",
            success : function (data) {

             }
    })


    //创建时间,当前的系统时间
    String createTime = DateTimeUtil.getSysTime();
    //从session域中获取当前登录用户
    String createBy =((User)request.getSession().getAttribute("user")).getName();
</body>
</html>
