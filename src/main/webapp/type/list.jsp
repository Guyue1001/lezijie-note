<%--
  Created by IntelliJ IDEA.
  User: GuYue
  Date: 2023/2/7
  Time: 20:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="col-md-9">
    <div class="data_list">
        <div class="data_list_title">
            <span class="glyphicon glyphicon-list"></span>&nbsp;类型列表
            <span class="noteType_add">
			<button class="btn btn-sm btn-success" type="button" id="addBtn">添加类别</button>
		</span>

        </div>
        <div id="myDiv">
<%--            通过JSTI的if标签，判断集合是否存在--%>
            <c:if test="${empty typeList}">
                <h2>暂未查询到类型的数据！</h2>
            </c:if>
            <c:if test="${!empty typeList}">


            <table class="table table-hover table-striped " id="myTable">
                <tbody>
                <tr>
                    <th>编号</th>
                    <th>类型</th>
                    <th>操作</th>
                </tr>
                <c:forEach items="${typeList}" var="item">
                     <tr id="tr_${item.typeId}">
                        <td>${item.typeId}</td>
                        <td>${item.typeName}</td>
                        <td>
                            <button class="btn btn-primary" type="button" onclick="openUpdateDialog(${item.typeId})">修改</button>
                            <button class="btn btn-danger del" type="button" onclick="deleteType(${item.typeId})">删除</button>
                        </td>
                     </tr>
                </c:forEach>
                </tbody>
            </table>
            </c:if>
        </div>
    </div>

<%--    添加/修改的模态框--%>
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span></button>
                    <h4 class="modal-title" id="myModalLabel">新增</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="typename">类型名称</label>
                        <input type="hidden" id="typeId" name="typeId">
                        <input type="text" name="typename" class="form-control" id="typeName" placeholder="类型名称">
                    </div>
                </div>
                <div class="modal-footer">
                    <span id="msg"></span>

                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <span class="glyphicon glyphicon-remove"></span>关闭</button>
                    <button type="button" id="btn_submit" class="btn btn-primary">
                        <span class="glyphicon glyphicon-floppy-disk"></span>保存</button>
                </div>
            </div>
        </div>
    </div>
</div>
<script>

    //删除类型
    function deleteType(typeId){
        //弹出提示框用户是否确认删除
        swal({
            title:"",//标题
            text:"<h3>你确认要删除该记录吗？</h3>",
            type:"warning",
            showCancelButton:true,
            confirmButtonColor:"orange",
            confirmButtonText:"确定",
            cancelButtonText:"取消",
        }).then(function (){
            //确认删除
            $.ajax({
                type:"post",
                url:"type",
                data:{
                    actionName:"delete",
                    typeId:typeId
                },
                success:function (result){
                    //判断是否删除
                    console.log("result:"+result.code);
                    if(result.code == 1){
                        swal("","<h3>删除成功</h3>","success");
                        //执行删除成功后的DOM参数
                        deleteDom(typeId);
                    }else {
                     //提示用户信息
                        swal("","<h3>删除失败</h3>","error");
                    }
                }
            })
        });
    }

    function deleteDom(typeId){
        //通过id得到表格对象
        var myTable = $("#myTable");
        //得到表格的tr
        var trLength = $("#myTable tr").length;
        if(trLength == 2){
            //此时只有一条数据，直接删除表格
            $("#myTable").remove();
            //设置提示信息
            $("#myDiv").html("<h2>暂未查询到类型数据</h2>");
            console.log("删除表格")
        }else {
            $("#tr_"+typeId).remove();
            console.log("删除一个数据")
        }

        $("#li_"+typeId).remove();

    }

    //添加绑定模态框
    $("#addBtn").click(function (){
        //设置添加模态框的标题
        $("#myModalLabel").html("新增类型");

        //清空模态框中文本框与隐藏域的值
        $("#typeId").val("");
        $("#typeName").val("");

        //清空提示信息
        $("#msg").html("");

        //打开模态框
        $("#myModal").modal("show");
    });

    //修改对话的模态框
    function openUpdateDialog(typeId) {
        // 设置修改模态框的标题
        $("#myModalLabel").html("修改类型");
        // 得到当前修改按钮对应的类型记录
        // 通过id选择器，获取当前的tr对象
        var tr = $("#tr_"+typeId);
        // 得到tr具体的单元格的值 （第二个td，下标是1）
        var typeName = tr.children().eq(1).text();
        // 将类型名称设置给模态框中的文本框
        $("#typeName").val(typeName);
        // 得到要修改的记录的类型ID （第一个td，下标是0）
        var typeId = tr.children().eq(0).text();
        // 将类型ID设置到模态框中的隐藏域中
        $("#typeId").val(typeId);
        // 清空提示信息
        $("#msg").html("");
        $("#myModal").modal("show");
    }

    /*
    * 添加或修改类型
    * */
    $("#btn_submit").click(function (){
        //获取参数
        var typeName = $("#typeName").val();

        //获取类型id，如果存在就是修改，否则就是删除
        var typeId = $("#typeId").val();

        if(isEmpty(typeName)){
            //如果参数为空
            $("#msg").html("类型名称不能为空");
            return ;
        }

        //发送ajax
        $.ajax({
            type:"post",
            url:"type",
            data:{
                actionName:"addOrUpdate",
                typeName:typeName,
                typeId:typeId
            },
            success:function (result){
                //判断是否更新成功
                console.log(result.code)
                if(result.code == 1){
                    //关闭模态框
                    $("#myModal").modal("hide");
                    //判断类型ID是否为空
                    if(isEmpty(typeId)){
                        //为空，执行添加操作的
                        addDom(typeName,result.result)
                    }else {

                        //不为空，执行修改的DOM操作
                        updateDom(typeName,typeId)
                    }
                }else {
                    $("#msg").html(result.msg);
                }
            }
        })

    });

    /**
     * 添加类型的DOM操作
     1. 添加tr记录
     2. 添加左侧类型分组导航栏的列表项
     * @param typeName
     * @param typeId
     */
    function addDom(typeName, typeId) {
        /* 1. 添加tr记录 */
        // 1.1. 拼接tr标签
        var tr = '<tr id="tr_'+typeId+'"> <td>'+typeId+'</td><td>'+typeName+'</td>';
        tr += '<td><button class="btn btn-primary"type="button"onclick="openUpdateDialog('+typeId+')">修改 </button>&nbsp;';
        tr += '<button class="btn btn-danger del"type="button" onclick="deleteType('+typeId+')">删除</button></td></tr>';
        // 1.2. 通过id属性值，获取表格对象
        var myTable = $("#myTable");
        // 1.3. 判断表格对象是否存在 （长度是否大于0）
        if (myTable.length > 0) { // 如果length大于0，表示表格存在
            // 1.4. 将tr标签追加到表格对象中
            myTable.append(tr);
        } else { // 表示表格不存在
            // 拼接table标签及tr标签
            myTable = '<table id="myTable"class="table table-hover table-striped">';
            myTable += '<tbody> <tr> <th>编号</th> <th>类型</th> <th>操作</th> </tr>';
            myTable += tr + '</tbody></table>';
            // 追加到div中
            $("#myDiv").html(myTable);
        }
        /* 2. 添加左侧类型分组导航栏的列表项 */
        // 2.1. 拼接li元素
        var li = '<li id="li_'+typeId+'"><a href=""> <span id="sp_'+typeId+'">'+typeName+'</span> <span class="badge">0</span></a></li>';
        // 2.3 设置ul标签的id属性值，将li元素追加到ul中
        $("#typeUl").append(li);

    }

    /**
     * 修改的DOM操作
     1. 修改指定tr记录
     2. 修改左侧类型分组导航栏的列表项
     给左侧类型名称设置span标签，并指定id属性值，修
     改span元素的文本值
     * @param typeName
     * @param typeId
     */
    function updateDom(typeName, typeId) {
        /* 1. 修改指定tr记录 */
        // 1.1. 通过id选择器，得到tr对象
        var tr = $("#tr_" + typeId);
        // 1.2. 修改tr指定单元格的文本值
        tr.children().eq(1).text(typeName);
        /* 2. 修改左侧类型分组导航栏的列表项 */
        // 修改span元素的文本值
        $("#sp_"+typeId).html(typeName);
    }


</script>