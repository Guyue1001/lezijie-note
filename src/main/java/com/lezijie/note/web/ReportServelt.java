package com.lezijie.note.web;

import com.lezijie.note.po.Note;
import com.lezijie.note.po.User;
import com.lezijie.note.service.NoteService;
import com.lezijie.note.util.JsonUtil;
import com.lezijie.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/report")
public class ReportServelt extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置导航栏的高亮
        request.setAttribute("menu_page","user");

        //得到用户行为
        String actionName = request.getParameter("actionName");

        //判断用户行为
        if("info".equals(actionName)){
            //进入报表页面
            reportInfo(request,response);
        }else if("month".equals(actionName)){
            queryNoteCountByMonth(request,response);
        }else if("location".equals(actionName)){
            //查询用户发布云记时的坐标
            queryNoteLonAndLat(request, response);
        }

    }

    //查询用户发布云记的坐标
    private void queryNoteLonAndLat(HttpServletRequest request, HttpServletResponse response) {
        //从Session的作用域中获取用户对象
        User user = (User) request.getSession().getAttribute("user");

        //调用Service层的查询方法，返回ResultInfo对象
        ResultInfo<List<Note>> resultInfo = noteService.queryNoteLonAndLat(user.getUserId());

        // 将ResultInfo对象转换成JSON格式的字符串，响应给AJAX的回调函数
        JsonUtil.toJson(response, resultInfo);
    }

    //通过月份查询对应的云记数量
    private void queryNoteCountByMonth(HttpServletRequest request, HttpServletResponse response) {
        //从Seesion作用域中获取用户对象
        User user = (User) request.getSession().getAttribute("user");

        //调用Service层的查询方法，返回ResultInfo对象
        ResultInfo<Map<String,Object>>  resultInfo = noteService.queryNoteCountByMonth(user.getUserId());

        //// 将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数

        JsonUtil.toJson(response,resultInfo);
    }

    private void reportInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            //设置页面首页动态包含的页面值
        request.setAttribute("changePage","report/info.jsp");

            //请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }
}
