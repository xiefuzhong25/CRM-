<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

	Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
	Set<String> set = pMap.keySet();
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>

	<script>

		//拼接java脚本来将java中pMap转换成json
		// 注意①有个硬性要求key必须套用在双引号当中,value不用  ②拼接json的时候不用考虑最后一个需要 判断 来使用逗号，因为首先我们声明的就是一个json 内部会自动处理好
		var json = {

			<%

				for (String key:set){
					String value = pMap.get(key);

			%>

				"<%=key%>":<%=value%>,

			<%
				}

			%>
		};
		//弹出object  object 说明我们的拼接json就是正确的
		// alert(json);

		/*
		* 	关于阶段和可能性
		* 		是一种一一对应的关系
		* ·		一个阶段一个可能性
		* 		我们现在可以将阶段和可能性想象成一种键值对之间的对应关系
		* 		以阶段为key，通过选中的阶段，触发可能性value
		*
		* stage			possibility
		* key			value
		* 01资质审查 	10
		* 02			25
		* 03			40
		* ...			...
		* 	对于以上的数据，通过观察得到结论
		* 	（1） 数据量不是很大
		* 	（2）这是一种键值对的对应关系
		*
		* 		如果满足以上两种结论，那么我们将这样的数据保存到数据库表中就没有什么意义了
		* 			最好就是properties属性文件来进行保存【这里注意这种文件对中文不太友好，需要转码进行保存】
		* stage2Possibility.properties
		* 	01资质审查=10
		* 	02需求分析=20
		* 	......
		*
		* stage2Possibility.properties这个文件表示的是这个文件表示的是阶段和键值对之间的对应关系
		* 	将来，我们通过stage，以及对应关系，来取得可能性这个值
		*
		* 	所以我们就需要将该文件解析到服务器缓存中
		* 	application.setAttribute(stage2Possibility.properties文件内容)
		*
		*
		 */

		$(function () {

			//自动补全插件
			$("#create-customerName").typeahead({
				source: function (query, process) {
					$.get(
							"workbench/transaction/getCustomerName.do",
							{ "name" : query },
							function (data) {
								//alert(data);
								/**
								 * data
								 * 	[{客户名称1}，{2}]
								 */
								process(data);
							},
							"json"
					);
				},
				//延迟的加载时间，过了这个时间，，把动画列出来
				delay: 1500
			});


			//时间插件
			$(".time1").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			//时间插件
			$(".time2").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "top-left"
			});

			//点击小放大镜打来模态窗口【这里直接写了两个的】
			$("#openFindMarketActivityModel").click(function () {
				//打开前清空原来的东西
				$("#aname").val("");
				$("#findMarketActivityBody").html("");
				//打开模态窗口
				$("#findMarketActivity").modal("show");

			})

			$("#openFindContactsModel").click(function () {
				//打开前清空原来的东西
				$("#aFullName").val("");
				$("#findContactsBody").html("");
				//打开模态窗口
				$("#findContacts").modal("show");

			})


			//为查找市场活动绑定事件
			$("#aname").keydown(function (event) {
				if (event.keyCode==13){


					$.ajax({
						url:"workbench/transaction/getActivityListByName.do",
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
							$("#findMarketActivityBody").html(html);

						}
					})
					return false;
				}

			})

			//为查找市场活动模态窗口中的 确定按钮 绑定事件
			$("#submitActivityBtn").click(function () {
				//取得选中市场活动的id
				var $xz = $("input[name=xz]:checked");
				var id = $xz.val();

				// 取得市场活动的名字
				var name = $("#"+id).html();

				//将数据填入文本框中
				$("#create-activitySrc").val(name);
				$("#create-activitySrcId").val(id);

				//关闭模态窗口
				$("#findMarketActivity").modal("hide");

			})

			//为查找联系人模态窗口绑定事件
			$("#aFullName").keydown(function (event) {
				if (event.keyCode==13){

					$.ajax({
						url:"workbench/transaction/getContactsListByFullName.do",
						data :{
							"aFullName" : $.trim($("#aFullName").val())

						} ,
						type : "get",
						dataType : "json",
						success : function (data) {

							var html = "";
							$.each(data,function (i,n) {

							html += '<tr>';
							html += '<td><input type="radio"  name="xz" value="'+n.id+'"/></td>';
							html += '<td id="'+n.id+'">'+n.fullname+'</td>';
							html += '<td>'+n.email+'</td>';
							html += '<td>'+n.mphone+'</td>';
							html += '</tr>';

							})
						$("#findContactsBody").html(html);

						}
					})
					return false;
				}

			})

			//为查找联系人模态窗口中的确定按钮绑定事件
			$("#submitContactsBtn").click(function () {
				//取得选中市场活动的id
				var $xz = $("input[name=xz]:checked");
				var id = $xz.val();

				// 取得市场活动的名字
				var name = $("#"+id).html();

				//将数据填入文本框中
				$("#create-contactsName").val(name);
				$("#create-contactsNameId").val(id);

				//关闭模态窗口
				$("#findContacts").modal("hide");

			})

			//为阶段的下拉框，绑定选中下拉框的事件，根据选中的阶段填写可能性
			$("#create-stage").change(function () {

				var stage = $("#create-stage").val();
				/**
				 * 目标：填写可能性
				 *
				 * 阶段有了stage
				 * 阶段和可能性之间的对应关系pMap，但是pMap是java语言中的键值对关系（java中的map对象）[现在是在jsp中]
				 * 我们首先得将pMap转换为js中的键值对关系json
				 * 		pMap.put("01资质审查“,10)
				 * 		......
				 * 		转换为
				 * 		var jsom={"01资质审查"：10,"02需求分析"：25..}
				 *
				 *在前面接近头部script的位置 全局的位置上  我们就声明处理好了转json
				 *
				 * 接下来取可能性
				 *
				 */

				/**
				 * 我们现在以json.key的形式不能取得value
				 * 因为今天的stage是一个可变的变量
				 * 如果是这样的key,那么我们就不能以传统的json.key的形式来取值
				 * 	取值方式应该为
				 * 		json[key]
				 */

				var possibility = json[stage];
				// alert(possibility);
				//为可能性的文本框赋值
				$("#create-possibility").val(possibility);

			})


			//为保存按钮绑定事件，执行交易添加操作
			$("#saveBtn").click(function () {
				//使用传统请求提交表单
				$("#tranForm").submit();

			})






		})
	</script>
</head>
<body>
<!-- 查找联系人 -->
<div class="modal fade" id="findContacts" role="dialog">
	<div class="modal-dialog" role="document" style="width: 80%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span>
				</button>
				<h4 class="modal-title">查找联系人</h4>
			</div>
			<div class="modal-body">
				<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
					<form class="form-inline" role="form">
						<div class="form-group has-feedback">
							<input type="text" class="form-control" id="aFullName" style="width: 300px;" placeholder="请输入联系人名称，支持模糊查询">
							<span class="glyphicon glyphicon-search form-control-feedback"></span>
						</div>
					</form>
				</div>
				<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
					<thead>
					<tr style="color: #B3B3B3;">
						<td></td>
						<td>名称</td>
						<td>邮箱</td>
						<td>手机</td>
					</tr>
					</thead>
					<tbody id="findContactsBody">


					</tbody>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				<button type="button" class="btn btn-primary" id="submitContactsBtn">提交</button>
			</div>
		</div>
	</div>
</div>

<!-- 查找市场活动 -->
	<div class="modal fade" id="findMarketActivity" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">查找市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control"  id="aname" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable3" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
							</tr>
						</thead>
						<tbody id="findMarketActivityBody">


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


	
	<div style="position:  relative; left: 30px;">
		<h3>创建交易</h3>
	  	<div style="position: relative; top: -40px; left: 70%;">
			<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
			<button type="button" class="btn btn-default">取消</button>
		</div>
		<hr style="position: relative; top: -40px;">
	</div>
	<form  action="workbench/transaction/save.do" id="tranForm" method="post" class="form-horizontal" role="form" style="position: relative; top: -30px;">
		<div class="form-group">
			<label for="create-transactionOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionOwner" name="owner">

					<option></option>
					<c:forEach items="${uList}" var="u">
<%--						这里有一个知识点：jq中也可以使用三目运算符，只是用的相对较少【实现默认选中当前登录账户sessionScope.user.id】--%>
						<option value="${u.id}" ${user.id eq u.id ? "selected" : ""}>${u.name}</option>
					</c:forEach>

				</select>
			</div>
			<label for="create-amountOfMoney" class="col-sm-2 control-label">金额</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-amountOfMoney" name="money">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-transactionName" name="name">
			</div>
			<label for="create-expectedClosingDate" class="col-sm-2 control-label">预计成交日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time1" id="create-expectedClosingDate" name="expectedDate">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-customerName" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-customerName" name="customerName" placeholder="支持自动补全，输入客户不存在则新建">
			</div>
			<label for="create-stage" class="col-sm-2 control-label">阶段<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
			  <select class="form-control" id="create-stage" name="stage">
			  	<option></option>

				  <c:forEach items="${stage}" var="s" >
					  <option value="${s.value}">${s.text}</option>
				  </c:forEach>

			  </select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionType" class="col-sm-2 control-label">类型</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionType" name="type">
				  <option></option>

					<c:forEach items="${transactionType}" var="t" >
						<option value="${t.value}">${t.text}</option>
					</c:forEach>

				</select>
			</div>
			<label for="create-possibility" class="col-sm-2 control-label">可能性</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-possibility">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-clueSource" class="col-sm-2 control-label">来源</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-clueSource" name="source">
				  <option></option>

					<c:forEach items="${source}" var="so" >
						<option value="${so.value}">${so.text}</option>
					</c:forEach>

				</select>
			</div>
			<label for="create-activitySrc" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="openFindMarketActivityModel" ><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-activitySrc">
				<input type="hidden" name="activityId" id="create-activitySrcId">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-contactsName" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);" id="openFindContactsModel" ><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-contactsName">
				<input type="hidden" name="contactsId" id="create-contactsNameId">

			</div>
		</div>
		
		<div class="form-group">
			<label for="create-describe" class="col-sm-2 control-label">描述</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-describe" name="description"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-contactSummary" name="contactSummary"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time2" id="create-nextContactTime" name="nextContactTime">
			</div>
		</div>
		
	</form>
</body>
</html>