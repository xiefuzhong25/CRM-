<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});

//为部分属性绑定事件

		//页面加载完毕后，取出关联的市场活动信息列表
		showActivityList();

		//为关联市场活动模块窗口中的 搜索框  绑定事件，通过触发回车键，查询并且展示所需市场活动列表
		$("#aname").keydown(function (event) {

			//如果是回车键
			if (event.keyCode==13){

				// alert("查询并展现市场活动列表")，因为发现按回车后会触发模态窗口磨人的刷新功能，所以应该禁用它
				$.ajax({
					url:"workbench/clue/getActivityListByNameAndNotByClueId.do",
					data : {
						"aname":$.trim($("#aname").val()),
						"clueId" : "${c.id}"

					},
					type : "get",
					dataType : "json",
					success : function (data) {
						var html="";
						$.each(data,function (i,n) {

							html += '<tr>';
							html += '<td><input type="checkbox"  name="xz" value="'+n.id+'"/></td>';
							html += '<td>'+n.name+'</td>';
							html += '<td>'+n.startDate+'</td>';
							html += '<td>'+n.endDate+'</td>';
							html += '<td>'+n.owner+'</td>';
							html += '</tr>';

						})
						$("#activitySearchBody").html(html);
					}
				})

				//展现完列表后，记得将模态窗口默认的回车行为禁用掉
				return false;
			}
		})


		//在页面加载之后，展现该市场活动关联的备注信息列表
		showRemarkList();
		//备注后面的小图标展现【不要忘记在大div中添加一个id叫remarkBody】
		$("#remarkBody").on("mouseover", ".remarkDiv", function () {
			$(this).children("div").children("div").show();
		})
		$("#remarkBody").on("mouseout", ".remarkDiv", function () {
			$(this).children("div").children("div").hide();
		})

		//为全选按钮绑定事件
		$("#qx").click(function () {
			$("input[name=xz]").prop("checked",this.checked);

		})

		$("#activitySearchBody").on("click", $("input[name=xz]"), function () {
			$("#qx").prop("checked", $("input[name=xz]").length == $("input[name=xz]:checked").length);

		})


		//为关联按钮绑定事件
		$("#bundBtn").click(function () {
			var $xz = $("input[name]:checked");
			if ($xz.length==0){
				alert("请选择需要关联的市场活动");

			}else{
				//选有一条或者多条

				// workbench/clue/bund.do?cid=xxx&aid=xxx&aid=xxx
				var param = "cid=${c.id}&";
				for (var i=0;i<$xz.length;i++){
					//把每个dom对象包装成jquery对象$($xz[i])
					param += "aid="+$($xz[i]).val();
					if (i<$xz.length-1){
						param += "&";
					}
				}
				$.ajax({
					url:"workbench/clue/bund.do",
					data :param,
					type : "post",
					dataType : "json",
					success : function (data) {
						if (data.success){

							//清楚文本框里的东西  复选框中的√去掉  清空activitySearchBody中的内容
							$("#aname").val("");
								//重置全选按钮
							$("#qx").prop("checked",false);
							$("#activitySearchBody").html("");
							//刷新活动列表
							showActivityList();


							//关闭模态窗口
							$("#bundModal").modal("hide");
						}else{
							alert("关联市场活动失败");
						}

					}
				})

			}

		})

		//为保存备注按钮绑定事件
		$("#saveRemarkBtn").click(function () {

			$.ajax({
				url: "workbench/clue/saveRemark.do",
				data: {
					"noteContent": $.trim($("#remark").val()),
					"clueId": "${c.id}"

				},
				type: "post",
				dataType: "json",
				success: function (data) {
					/*
                    data
                        {"success":true/false,"ar":{备注}}
                     */
					if (data.success) {
						//成功首先将文本域中的文本清空
						$("#remark").val("");
						//在拼接出一个div加在前面（上面）
						var html = "";

						html += '<div  id="' + data.ar.id + '" class="remarkDiv" style="height: 60px;" >';
						html += '<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">';
						html += '<div style="position: relative; top: -40px; left: 40px;" >';
						html += '<h5 id="e' + data.ar.id + '">' + data.ar.noteContent + '</h5>';
						html += '<font color="gray">线索</font> <font color="gray">-</font> <b>${c.fullname}${c.appellation}-${c.company}</b> <small style="color: gray;" id="s' + data.ar.id + '"> ' + (data.ar.createTime) + ' 由' + (data.ar.createBy) + '</small>';
						html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">';
						html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\'' + data.ar.id + '\')"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #FF0000;"></span></a>';
						html += '&nbsp;&nbsp;&nbsp;&nbsp;';
						html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\'' + data.ar.id + '\')"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #FF0000;"></span></a>';
						html += '</div>';
						html += '</div>';
						html += '</div>';

						$("#remarkDiv").before(html);

					} else {
						alert("添加备注信息失败")
					}

				}
			})
		})

		//为更新备注按钮绑定事件
		$("#updateRemarkBtn").click(function () {
			var id = $("#remarkId").val();
			$.ajax({
				url: "workbench/clue/updateRemark.do",
				data: {
					"id": id,
					"noteContent": $.trim($("#noteContent").val())

				},
				type: "post",
				dataType: "json",
				success: function (data) {
					/*
                    data
                        {"success":true/false,“ar”:{备注信息}}
                     */
					if (data.success) {
						//修改备注成功
						//更新div中相应的信息，例有：noteContent，editTime，editBy
						$("#e" + id).html(data.ar.noteContent);
						$("#s" + id).html(data.ar.editTime + "由" + data.ar.editBy);
						//更新之后关闭模态窗口
						$("#editRemarkModal").modal("hide");


					} else {
						//编辑修改备注失败
						alert("编辑备注信息失败")
					}
				}
			})
		})

		//为删除按钮绑定事件,执行线索删除操作
		$("#deleteBtn").click(function () {
			var id ="${c.id}";
				//删除前给一个有好的提示
				if (confirm("确定删除所选中的记录吗")) {

					var id ="${c.id}";
					// alert(param);
					//发起后台请求
					$.ajax({
						url: "workbench/clue/deleteByCids.do",
						data: {
							"id" : id
						},
						type: "post",
						dataType: "json",
						success: function (data) {

							// data
							//		{"success":true/false}
							if (data.success) {
								//删除成功后需要回到线索列表
								window.location.href = "workbench/clue/index.jsp";
							} else {
								alert("删除市场活动列表失败");
							}

						}
					})

				}



		})


	});

//定义各种功能函数
	function deleteRemark(id) {
		//这是删除备注信息调用的方法
		/*
        这里传进一个id，需要特别注意细节，对于动态生成的元素所触发的方法，其中的参数必须套用在字符串当中
         */
		// alert(123);
		$.ajax({
			url: "workbench/clue/deleteRemark.do",
			data: {
				"id": id
			},
			type: "post",
			dataType: "json",
			success: function (data) {
				/*
                data
                    {"flag":true/false}
                 */

				//
				if (data.success) {
					//删除备注成功,通过div的id直接删除那个div就行
					$("#" + id).remove();

				} else {
					alert("删除备注失败");
				}

			}
		})

	}

	function editRemark(id) {
		//这是一个编辑修改备注的方法
		// 【分析，由于只需要备注内荣填进到模态窗口中，就只使用了直接从页面中拿值，而不过后台取值】

		//将模态窗口中隐藏域id赋上值
		$("#remarkId").val(id);

		//找到指定的存放备注信息的h5标签
		var noteContent = $("#e" + id).html();

		//将h5中展现出来的信息，富裕到修改操作模态窗口的文本域中
		$("#noteContent").val(noteContent);

		//将以上信息处理完毕后，将修改备注的模态窗口打开
		$("#editRemarkModal").modal("show");


	}

	function showRemarkList() {
		//这是一个使用Ajax展示备注信息列表的处理方法
		$.ajax({
			url: "workbench/clue/getRemarkListByCid.do",
			data: {
				//在js代码中使用el表达式必须套在字符串当中，所以要加双引号
				"clueId": "${c.id}"

			},
			type: "get",
			dataType: "json",
			success: function (data) {
				/*
                data
                    【{备注1}，{备注2}】
                 */
				var html = "";
				$.each(data, function (i, n) {
					html += '<div  id="' + n.id + '" class="remarkDiv" style="height: 60px;" >';
					html += '<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">';
					html += '<div style="position: relative; top: -40px; left: 40px;" >';
					html += '<h5 id="e' + n.id + '">' + n.noteContent + '</h5>';
					html += '<font color="gray">线索</font> <font color="gray">-</font> <b>${c.fullname}${c.appellation}-${c.company}</b> <small style="color: gray;" id="s' + n.id + '"> ' + (n.editFlag == 0 ? n.createTime : n.editTime) + ' 由' + (n.editFlag == 0 ? n.createBy : n.editBy) + '</small>';
					html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">';
					html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\'' + n.id + '\')"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #FF0000;"></span></a>';
					html += '&nbsp;&nbsp;&nbsp;&nbsp;';
					html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\'' + n.id + '\')"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #FF0000;"></span></a>';
					html += '</div>';
					html += '</div>';
					html += '</div>';

				})
				$("#remarkDiv").before(html);
			}
		})
	}

	function showActivityList() {
		$.ajax({
			url:"workbench/clue/getActivityListByClueId.do",
			data :{
				"clueId" : "${c.id}"
			} ,
			type : "get",
			dataType : "json",
			success : function (data) {
				/*
				data:
					【{市场活动1},{市场活动2}】
				 */
				var html = "";
				$.each(data,function (i,n) {

					html += '<tr>';
					html += '<td>'+n.name+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td><a href="javascript:void(0);"  onclick="unbund(\''+n.id+'\')" style="text-decoration: none; "><span class="glyphicon glyphicon-remove"></span>解除关联</a></td>';
					html += '</tr>';

				})
				$("#activityBody").html(html);
			}

		})




	}

	function unbund(id) {

		$.ajax({
			url:"workbench/clue/unbundById.do",
			data :{
				"id" :id

			} ,
			type : "get",
			dataType : "json",
			success : function (data) {
				if (data.success){
					//刷新市场活动
					showActivityList();
				}else{
					alert("解除关联失败");
				}

			}
		})



	}

	
</script>

</head>
<body>

	<!-- 关联市场活动的模态窗口 -->
	<div class="modal fade" id="bundModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">关联市场活动</h4>
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
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td><input type="checkbox" id="qx"/></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="activitySearchBody">


						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="bundBtn">关联</button>
				</div>
			</div>
		</div>
	</div>

    <!-- 修改线索的模态窗口 -->
    <div class="modal fade" id="editClueModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 90%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">修改线索</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form">

                        <div class="form-group">
                            <label for="edit-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-clueOwner">
                                    <option>zhangsan</option>
                                    <option>lisi</option>
                                    <option>wangwu</option>
                                </select>
                            </div>
                            <label for="edit-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-company" value="动力节点">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-call" class="col-sm-2 control-label">称呼</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-call">
                                    <option></option>
                                    <option selected>先生</option>
                                    <option>夫人</option>
                                    <option>女士</option>
                                    <option>博士</option>
                                    <option>教授</option>
                                </select>
                            </div>
                            <label for="edit-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-surname" value="李四">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-job" class="col-sm-2 control-label">职位</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-job" value="CTO">
                            </div>
                            <label for="edit-email" class="col-sm-2 control-label">邮箱</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-email" value="lisi@bjpowernode.com">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-phone" class="col-sm-2 control-label">公司座机</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-phone" value="010-84846003">
                            </div>
                            <label for="edit-website" class="col-sm-2 control-label">公司网站</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-website" value="http://www.bjpowernode.com">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-mphone" class="col-sm-2 control-label">手机</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-mphone" value="12345678901">
                            </div>
                            <label for="edit-status" class="col-sm-2 control-label">线索状态</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-status">
                                    <option></option>
                                    <option>试图联系</option>
                                    <option>将来联系</option>
                                    <option selected>已联系</option>
                                    <option>虚假线索</option>
                                    <option>丢失线索</option>
                                    <option>未联系</option>
                                    <option>需要条件</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-source" class="col-sm-2 control-label">线索来源</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-source">
                                    <option></option>
                                    <option selected>广告</option>
                                    <option>推销电话</option>
                                    <option>员工介绍</option>
                                    <option>外部介绍</option>
                                    <option>在线商场</option>
                                    <option>合作伙伴</option>
                                    <option>公开媒介</option>
                                    <option>销售邮件</option>
                                    <option>合作伙伴研讨会</option>
                                    <option>内部研讨会</option>
                                    <option>交易会</option>
                                    <option>web下载</option>
                                    <option>web调研</option>
                                    <option>聊天</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="edit-describe">这是一条线索的描述信息</textarea>
                            </div>
                        </div>

                        <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>

                        <div style="position: relative;top: 15px;">
                            <div class="form-group">
                                <label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="3" id="edit-contactSummary">这个线索即将被转换</textarea>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
                                <div class="col-sm-10" style="width: 300px;">
                                    <input type="text" class="form-control" id="edit-nextContactTime" value="2017-05-01">
                                </div>
                            </div>
                        </div>

                        <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="edit-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="edit-address">北京大兴区大族企业湾</textarea>
                                </div>
                            </div>
                        </div>
                    </form>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal">更新</button>
                </div>
            </div>
        </div>
    </div>

	<!-- 修改线索详情中备注的模态窗口 -->
	<div class="modal fade" id="editRemarkModal" role="dialog">
		<%-- 备注的id --%>
		<input type="hidden" id="remarkId">
		<div class="modal-dialog" role="document" style="width: 40%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" >修改备注</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
						<div class="form-group">
							<label for="noteContent" class="col-sm-2 control-label">内容</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="noteContent"></textarea>
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateRemarkBtn">更新</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${c.fullname}${c.appellation}<small>${c.company}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 500px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" onclick="window.location.href='workbench/clue/convert.jsp?id=${c.id}&fullname=${c.fullname}&appellation=${c.appellation}&company=${c.company}&owner=${c.owner}';"><span class="glyphicon glyphicon-retweet"></span> 转换</button>
<%--			<button type="button" class="btn btn-default" data-toggle="modal" data-target="#editClueModal"><span class="glyphicon glyphicon-edit"></span> 编辑</button>--%>
			<button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: -70px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${c.fullname}${c.appellation}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${c.owner}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">公司</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${c.company}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">职位</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${c.job}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">邮箱</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${c.email}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">公司座机</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${c.phone}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">公司网站</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${c.website}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">手机</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${c.mphone}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">线索状态</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${c.state}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">线索来源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${c.source}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${c.createBy}&nbsp;</b><small style="font-size: 10px; color: gray;">${c.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${c.editBy}&nbsp;</b><small style="font-size: 10px; color: gray;">${c.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>${c.description}</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${c.contactSummary}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${c.nextContactTime}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px; "></div>
		</div>
        <div style="position: relative; left: 40px; height: 30px; top: 100px;">
            <div style="width: 300px; color: gray;">详细地址</div>
            <div style="width: 630px;position: relative; left: 200px; top: -20px;">
                <b>
                    ${c.address}
                </b>
            </div>
            <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
        </div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 40px; left: 40px;" id="remarkBody">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		
		<!-- 备注1 -->
<%--		<div class="remarkDiv" style="height: 60px;">--%>
<%--			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">--%>
<%--			<div style="position: relative; top: -40px; left: 40px;" >--%>
<%--				<h5>哎呦！</h5>--%>
<%--				<font color="gray">线索</font> <font color="gray">-</font> <b>李四先生-动力节点</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>--%>
<%--				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">--%>
<%--					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--					&nbsp;&nbsp;&nbsp;&nbsp;--%>
<%--					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--				</div>--%>
<%--			</div>--%>
<%--		</div>--%>
		
		<!-- 备注2 -->
<%--		<div class="remarkDiv" style="height: 60px;">--%>
<%--			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">--%>
<%--			<div style="position: relative; top: -40px; left: 40px;" >--%>
<%--				<h5>呵呵！</h5>--%>
<%--				<font color="gray">线索</font> <font color="gray">-</font> <b>李四先生-动力节点</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>--%>
<%--				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">--%>
<%--					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--					&nbsp;&nbsp;&nbsp;&nbsp;--%>
<%--					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--				</div>--%>
<%--			</div>--%>
<%--		</div>--%>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary" id="saveRemarkBtn">保存</button>
				</p>
			</form>
		</div>
	</div>
	
	<!-- 市场活动 -->
	<div>
		<div style="position: relative; top: 60px; left: 40px;">
			<div class="page-header">
				<h4>市场活动</h4>
			</div>
			<div style="position: relative;top: 0px;">
				<table class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td>名称</td>
							<td>开始日期</td>
							<td>结束日期</td>
							<td>所有者</td>
							<td></td>
						</tr>
					</thead>
					<tbody id="activityBody">

					</tbody>
				</table>
			</div>
			
			<div>
				<a href="javascript:void(0);"  data-toggle="modal"  data-target="#bundModal"  style="text-decoration: none;"><span class="glyphicon glyphicon-plus"></span>关联市场活动</a>
			</div>
		</div>
	</div>
	
	
	<div style="height: 200px;"></div>
</body>
</html>