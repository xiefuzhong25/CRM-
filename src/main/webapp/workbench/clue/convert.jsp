<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){
		//时间插件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});

		//为放大镜图标绑定事件，打开搜索市场活动的模态窗口
		$("#openSearchModalBtn").click(function () {

			$("#searchActivityModal").modal("show");

		})

		//为搜索操作模态窗口的 搜索框绑定事件，执行搜索并展现市场活动列表的操作
		$("#aname").keydown(function (event) {

			if (event.keyCode==13){

				$.ajax({
					url:"workbench/clue/getActivityListByName.do",
					data :{
						"aname" : $.trim($("#aname").val())

					} ,
					type : "get",
					dataType : "json",
					success : function (data) {

						var  html = "";
						$.each(data,function (i,n) {

							html += '<tr>';
							html += '<td><input type="radio" name="xz" value="'+n.id+'"/></td>';
							html += '<td id="'+n.id+'">'+n.name+'</td>';
							html += '<td>'+n.startDate+'</td>';
							html += '<td>'+n.endDate+'</td>';
							html += '<td>'+n.owner+'</td>';
							html += '</tr>';

						})
						$("#searchActivityModalBody").html(html);

					}
				})
				//关闭回车的默认关闭模态窗口功能
			return false;
			}

		})

		//为提交（市场活动）按钮绑定事件，填充市场活动源（填写两项信息：name+id)
		$("#submitActivityBtn").click(function () {

			//取得选中市场活动的id
			var $xz = $("input[name=xz]:checked");
			var id = $xz.val();

			// 取得市场活动的名字
			var name = $("#"+id).html();

			//将以上信息填写到，交易表单的视窗活动原中
			$("#activityId").val(id);
			$("#activityName").val(name);

			//将模态窗口关闭掉
			$("#searchActivityModal").modal("hide");

		})

		//为转换按钮绑定事件，执行限速的转换操作
		$("#convertBtn").click(function () {

			if ($("#isCreateTransaction").prop("checked")){
				// alert("需要创建交易");
				//除了传递创建交易的clueId还有交易表中的信息，金额，预计成交日期，交易名称，阶段，市场活动源（id）

				<%--window.location.href = "workbench/clue/convert.do?clueId=${param.id}...."--%>
				// 如果使用问号后面拼接很多参数的形式来上传参数很麻烦，而且一但表单扩充，挂载的参数可能超出浏览器地址上限
				//所以想到使用提交表单的形式来发出本次请求，就不用手动挂载参数了，而且提交表单可以使用post请求，安全性高

				//提交表单
				$("#tranForm").submit();


			}else {
				// alert("不需要创建交易");
				//使用传统跳转方式
				window.location.href = "workbench/clue/convert.do?clueId=${param.id}";
			}

			

		})



	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" id="aname" class="form-control" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="searchActivityModalBody">


						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="submitActivityBtn">提交</button>
				</div>
			</div>
		</div>
	</div>

	<div id="title" class="page-header" style="position: relative; left: 20px;">
<%--		el表达式为我们提供了多个隐含对象，除了xxxScope系列的隐含对象我们可以省略，其他的都不可以省略--%>
<%--		此处从一个页面中url带参的形式跳转到这个页面，要使用el表达式param属性一定不能省略(如果省略掉会从域中取值)--%>
<%--		param.xxx相当于request.getParameter("");--%>
	<h4>转换线索 <small>${param.fullname}${param.appellation}-${param.company}</small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
		新建客户：${param.company}
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
		新建联系人：${param.fullname}
	</div>
	<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
		<input type="checkbox" id="isCreateTransaction"/>
		为客户创建交易
	</div>
	<div id="create-transaction2" style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #F7F7F7; display: none;" >
		<!--提交表单行为结果：
			workbench/clue/convert.do?clueId=xxx&money=xxx&name=xxx...

		u-->
		<form id="tranForm" action="workbench/clue/convert.do" method="post">
<%--			隐藏域来保存线索id--%>
			<input type="hidden" name="clueId" value="${param.id}"/>
<%--	隐藏域来保存是否用户勾选了这个表单，然后标记这个请求，利于后台区分请求 接受参数--%>
			<input type="hidden" name="flag" value="a"/>

		  <div class="form-group" style="width: 400px; position: relative; left: 20px;">
		    <label for="amountOfMoney">金额</label>
		    <input type="text" class="form-control" id="amountOfMoney" name="money">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="tradeName">交易名称</label>
		    <input type="text" class="form-control" id="tradeName" name="name">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="expectedClosingDate">预计成交日期</label>
		    <input type="text" class="form-control time" id="expectedClosingDate" name="expectDate">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="stage">阶段</label>
		    <select id="stage"  class="form-control" name="stage">
		    	<option></option>
				<c:forEach items="${stage}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>
		    </select>
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="activityName">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
		    <input type="text" class="form-control" id="activityName" placeholder="点击上面搜索" readonly>
			  <input type="hidden" id="activityId" name="activityId">
		  </div>
		</form>
		
	</div>
	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
		记录的所有者：<br>
		<b>${param.owner}</b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
		<input class="btn btn-primary" type="button" value="转换" id="convertBtn">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>