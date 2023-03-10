package com.lezijie.note.web;

import cn.hutool.core.util.StrUtil;
import com.lezijie.note.po.Note;
import com.lezijie.note.po.NoteType;
import com.lezijie.note.po.User;
import com.lezijie.note.service.NoteService;
import com.lezijie.note.service.NoteTypeService;
import com.lezijie.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       //得到用户行为
        String actionName = request.getParameter("actionName");
        //判断用户行为
        if("view".equals(actionName)){
            //进入发布云记页面
            noteView(request,response);
        }else if("addOrUpdate".equals(actionName)){
            //添加或修改云日记
            addOrUpdate(request,response);
        }else if("detail".equals(actionName)){
            //云日记详情
            noteDetail(request,response);
        }else if("delete".equals(actionName)){
            //删除云记列表
            noteDelete(request,response);
        }
    }

    //删除云记列
    private void noteDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //接受参数
    String noteId = request.getParameter("noteId");

        //调用service层删除方法，返回状态码
        Integer code = noteService.deleteNote(noteId);
        //通过流将结果响应给ajax的回调函数
        response.getWriter().write(code+"");
        response.getWriter().close();
    }

    private void noteDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            //接受参数
        String noteId = request.getParameter("noteId");
        //调用Service层方法

        Note note = noteService.findNoteById(noteId);
        //将Note对象设置到request请求域中
        request.setAttribute("note",note);
        //设置首页动态包含的页面值
        request.setAttribute("changePage","note/detail.jsp");
        ///请求转发到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /*
    * 添加或修改操作
    *   1、接受参数 (类型ID、标题、内容)
    *   2、调用Service层方法，返回后ResuleInfo对象
    *   3、判断resultInfo的code值
    *       如果code = 1 表示成功
    *       重定向到跳转首页 index
    *       如果code = 0 表示失败
    *
    * */
    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    //接受参数
        String typeId = request.getParameter("typeId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        //如果是修改操作，需要接受noteId
        String noteId = request.getParameter("noteId");

        //调用Service层方法，返回ResuleInfo
        ResultInfo<Note> resultInfo = noteService.addOrUpdate(typeId,title,content,noteId);
        //判断resultInfo的code值
        if(resultInfo.getCode() == 1){
            //成功，重定向到首页
            response.sendRedirect("index");
        }else {
            //将resustInfo设置到作用域中
            request.setAttribute("resultInfo",resultInfo);
            // 请求转发跳转到note?actionName=vie
            String url = "note?actionName=view";
            if(!StrUtil.isBlank(noteId)){
                url += "&noteId="+noteId;
            }
            request.getRequestDispatcher(url).forward(request,response);
        }
    }

    /*
    * 进入发布云记录
    * 设置首页动态包含的页面值
    * 请求转发跳转到index.jsp页面
    * */
    private void noteView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //修改云记ID操作
        String noteId = request.getParameter("noteId");
        //通过noteId查询云记对象
        Note note = noteService.findNoteById(noteId);
        //将note对象设置到请求域中
        
        request.setAttribute("noteInfo",note);


        //获取用户id
        User user =(User) request.getSession().getAttribute("user");

        //通过id查询对应的类型
        List<NoteType> typeList = new NoteTypeService().findTypeList(user.getUserId());

        request.setAttribute("typeList",typeList);
        //设置首页动态包含值
        request.setAttribute("changePage","note/view.jsp");

        //请求转发跳转
        request.getRequestDispatcher("index.jsp").forward(request,response);

    }
}
