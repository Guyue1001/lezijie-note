<%--
  Created by IntelliJ IDEA.
  User: GuYue
  Date: 2023/2/10
  Time: 9:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="col-md-9">
    <div class="data_list">
        <div class="data_list_title">
            <span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;
            <c:if test="${empty noteInfo}">
                发布云记
            </c:if>
            <c:if test="${!empty noteInfo}">
                修改云记
            </c:if>
        </div>
        <div class="container-fluid">
            <div class="container-fluid">
                <div class="row" style="padding-top: 20px;">
                    <div class="col-md-12">
                        <%-- 判断类型列表是否为空，如果为空，提示用户先添加类型 --%>
                        <c:if test="${emptytypeList}">
                        <h2>暂未查询到云记类型！
                        </h2>
                        <h4><a href="type?actionName=list">添加类型</a> </h4>
                        </c:if>
                        <c:if test="${!emptytypeList}">
                        <form class="form-horizontal" method="post" action="note">

                            <input type="hidden" name="noteId" value="${noteInfo.noteId}">
                                <%--  隐藏域：用来存放noteId--%>
                            <input type="hidden" name="actionName" value="addOrUpdate">
<%--                            地区的经纬度--%>
                            <input type="hidden" name="lon" id="lon">
                            <input type="hidden" name="lat" id="lat">

                            <div class="form-group">
                                <label for="typeId" class="col-sm-2 control-label">
                                    类别:
                                </label>
                                <div class="col-sm-8">
                                    <select id="typeId" class="form-control" name="typeId">

                                        <option value="">请选择云记类别...</option>

                                        <c:forEach var="item" items="${typeList}">
                                           <c:choose>
                                               <c:when test="${!empty resultInfo}">
                                                   <option <c:if test="${resultInfo.result.typeId == item.typeId}">selected</c:if>  value="${item.typeId}">${item.typeName}</option>
                                               </c:when>
                                               <c:otherwise>
                                                   <option <c:if test="${noteInfo.typeId == item.typeId}">selected</c:if>  value="${item.typeId}">${item.typeName}</option>
                                               </c:otherwise>
                                           </c:choose>
                                        </c:forEach>

                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="title" class="col-sm-2 control-label">
                                    标题:
                                </label>
                                <div class="col-sm-8">
                                    <c:choose>
                                        <c:when test="${!empty resultInfo}">
                                            <input class="form-control" name="title" id="title" placeholder="云记标题" value="${resultInfo.result.title}">
                                        </c:when>
                                        <c:otherwise>
                                            <input class="form-control" name="title" id="title" placeholder="云记标题" value="${noteInfo.title}">
                                        </c:otherwise>
                                    </c:choose>

                                </div>
                            </div>
                            <div class="form-group">
                                <label for="title" class="col-sm-2 control-label">
                                    内容:
                                </label>
                                <div class="col-sm-8">
                                    <c:choose>
                                        <c:when test="${!empty resultInfo}">
                                            <%-- 准备容器，加载富文本编辑器 --%>
<%--                                            <script id="content" name="content" type="text/plain" style="width:1024px;height:500px;">--%>

<%--                                            </script>--%>
                                            <textarea id="content" name="content">
                                                ${resultInfo.result.content}
                                            </textarea>
                                        </c:when>
                                        <c:otherwise>
                                            <%-- 准备容器，加载富文本编辑器 --%>
                                            <textarea id="content" name="content">
                                                    ${noteInfo.content}
                                            </textarea>
                                        </c:otherwise>
                                    </c:choose>

                                </div>
                                <div class="col-sm-12">

                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-4 col-sm-4">
                                    <input type="submit" class="btn btn-primary" onclick="return checkForm()"  value="保存">&nbsp;
                                    <span id="msg" style="font-size: 12px;color: red"></span>
                                </div>
                            </div>
                        </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ue;
    $(function (){
       //加载富文本编辑器
        ue = UE.getEditor('content');
    });
    /*
* 1、获取表单元素的值
*    获取下拉框的选项
*    获取文本框中的选项
*    获取富文本编辑器的内容
*2、参数的非空判断
*    如果为空，提示用户，并return fasle
*    如果参数不为空，则return 提交表单
*3、如果参数不为空，则return ture 提交表单
* */
    function checkForm(){
        //获取表单元素的值
        var typeId = $("#typeId").val();
        //获取文本框的值
        var title = $("#title").val();
        //获取富文本编辑器的尼尔
        var content = ue.getContent();
        //参数的非空判断

        console.log(typeId)
        console.log(title)
        console.log(content)

        if(isEmpty(typeId)){
            $("#msg").html("请选择类型");
            return  false;
        }
        if(isEmpty(title)){
            $("#msg").html("云记标题不能为空");
            return  false;
        }
        if(isEmpty(content)){
            $("#msg").html("云记内容不能为空");
            return  false;
        }

        return true;
    }
</script>