<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html>
<head>
    <base href="<%=basePath%>">
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css"
          rel="stylesheet"/>

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
    <script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

    <script type="text/javascript">

        //这个index.jsp文件是点击市场活动触发的

        $(function () {

            /*
                bootstrap-datetimepicker.js  里面的时间插件
                datetimepicker   方法也是内部写好的
                想引用的话 直接用class
                */
            $(".time").datetimepicker({
                minView: "month",
                language: 'zh-CN',
                format: 'yyyy-mm-dd',
                autoclose: true,
                todayBtn: true,
                pickerPosition: "bottom-left"
            });

            //为创建按钮绑定事件，打开添加操作的模态窗口
            $("#addBtn").click(function () {

                /*
                操作模态窗口的jQuery对象，调用modal方法，为该方法传递参数 show：打开模态窗口    hide关闭模态窗口
                 */
                // alert(123);
                // $("#createActivityModal").modal("show");
                //走后台，目的是为了取得用户信息列表，为所有下拉列表赋值
                $.ajax({
                    url: "workbench/activity/getUserList.do",
                    // data : {
                    //
                    // },
                    type: "get",
                    dataType: "json",
                    success: function (data) {
                        /*
                        data
                            [{用户一}，{用户2}，...]
                         */

                        var html = "<option></option>";
                        //便历出来的每一个n，就是一个user对象
                        $.each(data, function (i, n) {
                            html += "<option value='" + n.id + "'> " + n.name + " </option>";

                        })

                        $("#create-owner").html(html);

                        /*
                        取得当前用户的id  在session域中
                        在js中使用el表达式一定要套在字符串中
                         */
                        //将当前的登录用户，设置为默认的选项
                        var id = "${user.id}";
                        $("#create-owner").val(id);

                        //所有者下拉框处理完毕后，展现模态窗口
                        $("#createActivityModal").modal("show");
                    }

                })

            })

            //为保存按钮 绑定事件，执行添加操作
            $("#saveBtn").click(function () {
                $.ajax({
                    url: "workbench/activity/save.do",
                    data: {
                        "owner": $.trim($("#create-owner").val()),
                        "name": $.trim($("#create-name").val()),
                        "startDate": $.trim($("#create-startDate").val()),
                        "endDate": $.trim($("#create-endDate").val()),
                        "cost": $.trim($("#create-cost").val()),
                        "description": $.trim($("#create-description").val())
                    },
                    type: "post",
                    dataType: "json",
                    success: function (data) {
                        if (data.success) {
                            //添加成功后  要刷新市场活动信息列表（局部刷新）
                            // pageList(1,2);
                            /**
                             *
                             *$("#activityPage").bs_pagination('getOption','currentPage')
                             *    操作后停留在当前页
                             * $("#activityPage").bs_pagination('getOption','rowsPerPage')
                             *    操作后维持已经设置好的每页展现的记录数
                             *
                             *    这两个参数不需要我们进行任何的修改操作
                             *        直接使用即可
                             */
                            //做完添加操作后，应该回到第一页，维持每页展现的记录数
                            pageList(1
                                , $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                            /*
                            jquery中没有为我们提供reset方法，但是原生js为我们提供了reset方法
                            所以我们要将jQuery对象转换为原生dom对象
                                jQuery转dom对象
                                    jQuery对象【下标】
                                dom转jQuery对象
                                    $(dom)
                             */
                            //清空添加操作模态窗口中的数据
                            $("#activityAddForm")[0].reset();
                            //关闭添加操作的模态窗口
                            $("#createActivityModal").modal("hide");

                        } else {
                            alert("添加失败")
                        }


                    }

                })

            })

            //页面加载完毕后触发一个方法
            //默认展开列表的第一页，每页展示两条记录
            pageList(1, 2);

            //为查询按钮绑定事件
            $("#searchBtn").click(function () {
                //点击查询按钮的时候，应该将搜索框中的信息保存起来到页面的隐藏域
                $("#hidden-name").val($.trim($("#search-name").val()));
                $("#hidden-owner").val($.trim($("#search-owner").val()));
                $("#hidden-startDate").val($.trim($("#search-startDate").val()));
                $("#hidden-endDate").val($.trim($("#search-endDate").val()));

                pageList(1, 2);
            })

            //为全选的复选框绑定事件，触发全选操作
            $("#qx").click(function () {
                $("input[name=xz]").prop("checked", this.checked);
            })


            /*
            为单个市场活动的复选框绑定事件
            动态生成的元素是不可以以普通绑定事件的形式来进行操作
            动态生成的元素要以on的形式来绑定
            正确语法
            $(需要绑定元素的有效的外层元素）.on(绑定的事件方式，需要绑定的元素的jQuery对象，回调函数）

            下面是错误的
             $("#xz").click(function () {
                alert(123);
            })
             */
            $("#activityBody").on("click", $("input[name=xz]"), function () {
                $("#qx").prop("checked", $("input[name=xz]").length == $("input[name=xz]:checked").length);

            })

            //为删除按钮绑定事件,执行删除市场活动删除操作
            $("#deleteBtn").click(function () {
                var $xz = $("input[name=xz]:checked");
                if ($xz.length == 0) {
                    alert("请选择需要删除的记录");

                    //走下面就可能是一条或者多条
                } else {

                    //删除前给一个有好的提示
                    if (confirm("确定删除所选中的记录吗")) {
                        /*
                        这次换一种方式来穿参数 ，应为参数名都叫id  但是value却不同使用json不好传
                            url : workbench/activity/delete.do？id=XXX&id=CCC&id=BBB
                             */

                        //拼接参数
                        var param = ""
                        //将$xz中的每一个dom对象便历出来，取其value值，就相当于取得了需要删除的记录的id
                        for (var i = 0; i < $xz.length; i++) {
                            param += "id=" + $($xz[i]).val();
                            if (i < $xz.length - 1) {
                                param += "&";
                            }
                        }
                        // alert(param);
                        //发起后台请求
                        $.ajax({
                            url: "workbench/activity/delete.do",
                            data: param,
                            type: "post",
                            dataType: "json",
                            success: function (data) {

                                // data
                                //		{"success":true/false}
                                if (data.success) {
                                    //删除成功后需要刷新市场活动列表
                                    //回到第一页，维持每一页展现数
                                    //pageList(1,2);
                                    pageList(1
                                        , $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                                } else {
                                    alert("删除市场活动列表失败");
                                }

                            }
                        })

                    }

                }

            })

            //修改窗口绑定事件，打开修改窗口的模态窗口
            $("#editBtn").click(function () {
                var $xz = $("input[name=xz]:checked");
                if ($xz.length == 0) {
                    alert("请选择一条修改记录")
                } else if ($xz.length > 1) {
                    alert("最多只能选择一条");
                } else {
                    var id = $xz.val();
                }
                $.ajax({
                    url: "workbench/activity/getUserListAndActivity.do",
                    data: {
                        "id": id

                    },
                    type: "get",
                    dataType: "json",
                    success: function (data) {
                        /*
                        前台需要 {"uList":[{用户1}，{用户2}，。。],"a":{一个市场活动列表}}

                         */
                        //处理所有者下拉框
                        var html = "<option></option>";
                        $.each(data.uList, function (i, n) {
                            html += "<option value='" + n.id + "'>" + n.name + "</option>";

                        })
                        $("#edit-owner").html(html);

                        //处理单条activity

                        $("#edit-id").val(data.a.id);
                        $("#edit-name").val(data.a.name);
                        $("#edit-owner").val(data.a.owner);
                        $("#edit-startDate").val(data.a.startDate);
                        $("#edit-endDate").val(data.a.endDate);
                        $("#edit-cost").val(data.a.cost);
                        $("#edit-description").val(data.a.description);

                        //所有的值都填好了就打开模态窗口
                        $("#editActivityModal").modal("show");

                    }
                })

            })


            //点市场活动的修改按钮后，为模态窗户的
            // 更新按钮绑定事件
            $("#updateBtn").click(function () {

                $.ajax({
                    url: "workbench/activity/update.do",
                    data: {
                        "id": $.trim($("#edit-id").val()),
                        "owner": $.trim($("#edit-owner").val()),
                        "name": $.trim($("#edit-name").val()),
                        "startDate": $.trim($("#edit-startDate").val()),
                        "endDate": $.trim($("#edit-endDate").val()),
                        "cost": $.trim($("#edit-cost").val()),
                        "description": $.trim($("#edit-description").val())
                    },
                    type: "post",
                    dataType: "json",
                    success: function (data) {
                        if (data.success) {
                            //修改成功后  要刷新市场活动信息列表（局部刷新）
                            // pageList(1,2);
                            /*
                             修改操作后，应该维持在当前页，维持每页展现的记录数
                             */
                            pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
                                , $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                            //关闭修改操作的模态窗口
                            $("#editActivityModal").modal("hide");

                        } else {
                            alert("更新失败")
                        }


                    }

                })


            })


        });

        /*
        对于所有关系型数据库，做前端的分页相关的操作的基础组件
        就是
        pageNo:页码
        pageSize:每页展现的记录数

        pageList方法：就是发出ajax请求到后台，从后台取得最新的市场活动信息列表数据
                        通过响应回来的数据，局部刷新市场活动列表
                哪些情况需要调用pageList方法？
                    点击左侧菜单中的 市场活动  页面加载完毕，局部刷新市场活动列表
                    添加，修改，删除后，局部刷新市场活动列表
                    点击查询按钮的时候，局部刷新市场活动列表
                    点击分页组件的时候，局部刷新市场活动列表
         */
        function pageList(pageNo, pageSize) {
            //每次刷新市场活动列表后应该重置全选框
            $("#qx").prop("checked", false);

            //每次查询前将隐藏域中的信息取出来
            $("#search-name").val($.trim($("#hidden-name").val()));
            $("#search-owner").val($.trim($("#hidden-owner").val()));
            $("#search-startDate").val($.trim($("#hidden-startDate").val()));
            $("#search-endDate").val($.trim($("#hidden-endDate").val()));

            $.ajax({
                url: "workbench/activity/pageList.do",
                data: {
                    "pageNo": pageNo,
                    "pageSize": pageSize,
                    "name": $.trim($("#search-name").val()),
                    "owner": $.trim($("#search-owner").val()),
                    "startDate": $.trim($("#search-startDate ").val()),
                    "endDate": $.trim($("#search-endDate").val())
                },
                type: "get",
                dataType: "json",
                success: function (data) {
                    /*
                    我们需要的，市场活动信息列表【{市场活动1}，{2}，{3}】 ----->   list<Activity> aList
                    然后还有一个分页插件需要的总记录数 {"total":100}
                        json形式:
                        {“total”：100，“dataList":【{市场活动1}，{2}，{3}】}
                     */
                    var html = "";
                    //每一个n就是一个市场活动对象
                    $.each(data.dataList, function (i, n) {
                        html += '<tr class="active"> ';
                        html += '<td><input type="checkbox"  name="xz"  value="' + n.id + '"/></td> ';
                        html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id=' + n.id + '\';">' + n.name + '</a></td>';
                        html += '<td>' + n.owner + '</td>';
                        html += '<td>' + n.startDate + '</td>';
                        html += '<td>' + n.endDate + '</td>';
                        html += '</tr>';
                    })
                    $("#activityBody").html(html);
                    /*
                    这里引用了bootstrap的分页插件
                    这里实现了点击插件分页的时候也会局部刷新列表
                     */
                    //计算总页数
                    var totalPages = data.total % pageSize == 0 ? data.total / pageSize : parseInt(data.total / pageSize) + 1;
                    //数据处理完毕后，结合分页插件，对前端展现分页信息
                    $("#activityPage").bs_pagination({
                        currentPage: pageNo, // 页码
                        rowsPerPage: pageSize, // 每页显示的记录条数
                        maxRowsPerPage: 20, // 每页最多显示的记录条数
                        totalPages: totalPages, // 总页数
                        totalRows: data.total, // 总记录条数

                        visiblePageLinks: 3, // 显示几个卡片

                        showGoToPage: true,
                        showRowsPerPage: true,
                        showRowsInfo: true,
                        showRowsDefaultInfo: true,
                        //该回调函数是在点击分页组件的时候触发的
                        onChangePage: function (event, data) {
                            //我们定义写的方法名，参数千万不要改 是引用的插件自己内部的
                            pageList(data.currentPage, data.rowsPerPage);
                        }
                    });


                }
            })

        }
    </script>
</head>
<body>
<%--        由隐藏域来暂时保存条件查询数据--%>
<input type="hidden" id="hidden-name"/>
<input type="hidden" id="hidden-owner"/>
<input type="hidden" id="hidden-startDate"/>
<input type="hidden" id="hidden-endDate"/>

<!-- 修改市场活动的模态窗口 -->
<div class="modal fade" id="editActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form">
                    <%--隐藏域存放id用来后面更新到数据库知道是谁--%>
                    <input type="hidden" id="edit-id">

                    <div class="form-group">
                        <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-owner">
                                <%--								<option>zhangsan</option>--%>
                                <%--								<option>lisi</option>--%>
                                <%--								<option>wangwu</option>--%>
                            </select>
                        </div>
                        <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-name">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="edit-startDate">
                        </div>
                        <label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="edit-endDate">

                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-cost">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <!--关于文本域textArea
                                (1)一定是要以标签对的形式来呈现的，正常状态下标签对要紧紧的挨着
                                (2)textArea虽然是以标签对的形式来呈现的，但是他也是属于表单元素范畴
                                我们所有的对他的取值和赋值，应该统一使用val()方法【而不是使用html()方法】
                                -->
                            <textarea class="form-control" rows="3" id="edit-description"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="updateBtn">更新</button>
            </div>
        </div>
    </div>
</div>
<!-- 创建市场活动的模态窗口 -->
<div class="modal fade" id="createActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" role="form" id="activityAddForm">

                    <div class="form-group">
                        <label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-owner">
                                <%--for 	create-marketActivityOwner--%>
                            </select>
                        </div>
                        <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-name">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="create-startTime" class="col-sm-2 control-label ">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="create-startDate" readonly>
                        </div>
                        <label for="create-endTime" class="col-sm-2 control-label ">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="create-endDate" readonly>
                        </div>
                    </div>
                    <div class="form-group">

                        <label for="create-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-cost">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="create-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="create-description"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveBtn">保存</button>
            </div>
        </div>
    </div>
</div>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>市场活动列表</h3>
        </div>
    </div>
</div>
<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="search-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="search-owner">
                    </div>
                </div>


                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">开始日期</div>
                        <input class="form-control " type="text" id="search-startDate"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">结束日期</div>
                        <input class="form-control " type="text" id="search-endDate">

                    </div>
                </div>

                <button type="button" class="btn btn-default" id="searchBtn">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span>
                    创建
                </button>
                <button type="button" class="btn btn-default" id="editBtn"><span
                        class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button type="button" class="btn btn-danger" id="deleteBtn"><span
                        class="glyphicon glyphicon-minus"></span> 删除
                </button>
            </div>

        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="qx"/></td>
                    <td>名称</td>
                    <td>所有者</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                </tr>
                </thead>
                <tbody id="activityBody">

                <%--里面动态生成数据--%>

                </tbody>
            </table>
        </div>

        <div style="height: 50px; position: relative;top: 30px;">
            <div id="activityPage"></div>


        </div>

    </div>

</div>
</body>
</html>