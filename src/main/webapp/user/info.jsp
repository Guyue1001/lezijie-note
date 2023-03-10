<%--
  Created by IntelliJ IDEA.
  User: GuYue
  Date: 2023/2/8
  Time: 14:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div class="col-md-9">
    <div class="data_list">
        <div class="data_list_title"><span class="glyphicon glyphicon-edit"></span>&nbsp;个人中心 </div>
        <div class="container-fluid">
            <div class="row" style="padding-top: 20px;">
                <div class="col-md-8">
                    <form class="form-horizontal" method="post" action="user" enctype="multipart/form-data" onsubmit="return checkUser();">
                        <div class="form-group">
<%--                           隐藏域 存放用户行为actionName --%>
                            <input type="hidden" name="actionName" value="updateUser">
                            <label for="nickName" class="col-sm-2 control-label">昵称:</label>
                            <div class="col-sm-3">
                                <input class="form-control" name="nick" id="nickName" placeholder="昵称" value="${user.nick}">
                            </div>
                            <label for="img" class="col-sm-2 control-label">头像:</label>
                            <div class="col-sm-5">
                                <input type="file" id="img" name="img">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="mood" class="col-sm-2 control-label">心情:</label>
                            <div class="col-sm-10">
                                <textarea class="form-control" name="mood" id="mood" rows="3">${user.mood}</textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-2 col-sm-10">
                                <button onclick="return updateUser()" type="submit" id="btn" class="btn btn-success">修改</button>&nbsp;&nbsp;<span style="color:#ff0000" id="msg"></span>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="col-md-4"><img style="width:240px;height:190px" src="user?actionName=userHead&imageName=${user.head}"></div>
            </div>
        </div>
    </div>
</div>
<script>
    $("#nickName").blur(function (){
        //获取昵称文本的值
        var nickName = $("#nickName").val();

        //判断值是否为空
        if(isEmpty(nickName)){
            //如果为空，提示用户，禁用按钮，并return
            $("#msg").html("用户昵称不能为空！");
            $("#btn").prop("disabled",true);
        }

        //判断昵称是否做了修改
        var nick = '${user.nick}';

        //如果没有修改
        if(nickName == nick){
            return ;
        }

        //如果昵称做了修改，发送ajax请求后台，验证昵称是否可用
        $.ajax({
            type:"get",
            url:"user",
            data:{
                actionName:"checkNick",
                nick:nickName
            },
            success:function (code){
                //如果可用，清空提示信息，按钮可用
                console.log(code);
                if(code == 1){
                    //清空提示信息
                    $("#msg").html("");
                    //按钮可用
                    $("#btn").prop("");
                }else {
                    //清空提示信息
                    $("#msg").html("该昵称以及存在，请重写输入")
                    //按钮可用
                    $("#brn").prop("disabled",true);
                }
            }
        })
        //做了修改就发送ajax请求，验证昵称是否可用
    }).focus(function (){
        // 1. 清空提示信息
        $("#msg").html("")
        // 2. 按钮可用
        $("#btn").prop("disabled", false);
    });

    function updateUser(){
        //获取昵称文本值
        var nickName = $("#nickName").val();
        //判断值是否为空
        if(isEmpty(nickName)){
            //如果为空，提醒用户，禁用按钮u，并return
            $("#msg").html("用户昵称不能为空")
            $("#btn").prop("disabled",true);
            return false;
        }
        return true;
    }
</script>
</body>
</html>
