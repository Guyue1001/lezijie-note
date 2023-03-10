package com.lezijie.note.web;

import com.lezijie.note.po.Note;
import com.lezijie.note.po.User;
import com.lezijie.note.service.NoteService;
import com.lezijie.note.util.Page;
import com.lezijie.note.vo.NoteVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置首页导航高亮
        request.setAttribute("menu_page","index");
        //得到用户行为
        String actionName = request.getParameter("actionName");
        if("searchTitle".equals(actionName)){
            //标题搜索
            String title = request.getParameter("title");
            //将查询条件设置到请求域中
            request.setAttribute("title",title);
            //标题搜索
            noteList(request,response,title,null,null);
        }else if("searchDate".equals(actionName)){
            //得到查询条件 日期查询
            String date = request.getParameter("date");
            request.setAttribute("date",date);
            noteList(request,response,null,date,null);
        }else if("searchType".equals(actionName)){
            String typeId = request.getParameter("typeId");
            noteList(request,response,null,null,typeId);
        }
        else {
            //不做条件查询
            // 分页查询云记列表
            noteList(request,response,null,null,null);
        }

        request.setAttribute("changePage","note/list.jsp");
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /*
    * 查询云记列表
    * */
    private void noteList(HttpServletRequest request, HttpServletResponse response,String title,String date,String typeId){
        //接受参数
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");

        //获取Session作用域中的user对象
        User user = (User) request.getSession().getAttribute("user");
        //调用Sercive层查询方法
        Page<Note> page = new NoteService().findNoteListByPage(pageNum,pageSize, user.getUserId(),title,date,typeId);

        request.setAttribute("page",page);

        //通过日期分组查询当前用户下的云记数量
        List<NoteVo> dateInfo = new NoteService().findNoteCountByDate(user.getUserId());
        //设置集合存放在
        request.getSession().setAttribute("dateInfo",dateInfo);

        //通过类型分组
        List<NoteVo> typeInfo = new NoteService().findNoteCountByType(user.getUserId());
        request.getSession().setAttribute("typeInfo",typeInfo);
    }
}
