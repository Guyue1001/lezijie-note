package com.lezijie.note.web;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.lezijie.note.po.NoteType;
import com.lezijie.note.po.User;
import com.lezijie.note.service.NoteTypeService;
import com.lezijie.note.util.JsonUtil;
import com.lezijie.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/type")
public class NoteTypeServlet extends HttpServlet {
    private NoteTypeService typeService = new NoteTypeService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置首页导航的高亮值
        request.setAttribute("menu_page","type");
        //得到用户行为
        String actionName = request.getParameter("actionName");
        //判断用户行为，调用对应的方法

        if("list".equalsIgnoreCase(actionName)){
            //查询用户列表
            typeList(request,response);
        }else if("delete".equals(actionName)){
            //删除云记列表
            deleteType(request,response);
        }else  if("addOrUpdate".equals(actionName)){
            addOrUpdate(request,response);
        }
    }

    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) {
        //1、接受参数
        String typeName = request.getParameter("typeName");
        String typeId = request.getParameter("typeId");

        //获取Session作用域中的user对象
        User user = (User) request.getSession().getAttribute("user");

        //调用Service层的更新方法 ， 返回ResultInfo对象
        ResultInfo<Integer> resultInfo = typeService.addOrUpdate(typeName,user.getUserId(),typeId);
        JsonUtil.toJson(response,resultInfo);
    }

    /*
    * 删除类型
    *
    * */
    private void deleteType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //接受参数
        String typeId = request.getParameter("typeId");
        //调用service层的更新操作
        ResultInfo<NoteType> resultInfo = typeService.deleteType(typeId);
        //将ResultInfo转成JSON字符串
        //设置响应类型以及编码格式(JSON类型)
        response.setContentType("application/json;charset=UTF-8");
        //输出给前端
        JsonUtil.toJson(response,resultInfo);
    }

    private void typeList(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
        //获取session作用域的user对象
        User user = (User) request.getSession().getAttribute("user");
        //调用Service层的查询方法，查询当前用户登录的类型集合，返回集合
        List<NoteType> typeList = typeService.findTypeList(user.getUserId());

        //将类型列表设置到request请求域中
        request.setAttribute("typeList",typeList);
        //设置首页动态包含的页面值
        request.setAttribute("changePage","type/list.jsp");
        // 请求转发到首页
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }
}
