package com.lezijie.note.web;

import com.lezijie.note.po.User;
import com.lezijie.note.service.UserService;
import com.lezijie.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;

@MultipartConfig
@WebServlet("/user")
public class UserServlet extends HttpServlet {
    //设置首页导航高亮

    private UserService userService = new UserService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //导航栏高亮显示
        request.setAttribute("menu_page","user");
        //接受用户行为
        String  actionName = request.getParameter("actionName");
        //判断用户行为，调用对应的方法
        if("login".equals(actionName)){
            //用户登录
            userLogin(request,response);
        }else if("logout".equals(actionName)){
            //用户退出
            userLogOut(request,response);
        }else if("userCenter".equals(actionName)){
            //进入个人中心
            userCenter(request,response);
        }else if("userHead".equals(actionName)){
            //加载头像
            userHead(request,response);
        }else if("checkNick".equalsIgnoreCase(actionName)){
            //验证用户名的唯一性
            checkNick(request,response);
        }else if("updateUser".equalsIgnoreCase(actionName)){
            //修改用户信息
            updateUser(request,response);
        }
    }

    //修改用户信息
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //调用Service层的方法，传递request对象作为参数，返回resultInfo对象
        ResultInfo<User> resultInfo = userService.updateUser(request);
        //将resultInfo对象存到request作用域中
        request.setAttribute("resultinfo",resultInfo);
        //请求转发跳转到个人中心页面
        request.getRequestDispatcher("user?actionName=userCenter").forward(request,response);
    }

    /*
    * 验证用户名的唯一性
    * 获取参数
    * 从session作用域获取用户对象，得到用户ID
    * 调用servive层的方法，得到返回的结果
    * 通过字符删除流将结果响应给前台的ajax
    * 关闭资源
    * */
    private void checkNick(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取参数
        String nick = request.getParameter("nick");
        //从session作用域获取用户对象，得到用户ID
        User user = (User) request.getSession().getAttribute("user");
        //调用servive层的方法，得到返回的结果
        Integer code = userService.checkNick(nick,user.getUserId());

        //通过字符删除流将结果响应给前台的ajax
        response.getWriter().write(code + "");
        //关闭资源
        response.getWriter().close();
    }

    //加载头像
    private void userHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1、获取参数 进入个人中心，图片的超链接自动发送请求链接携带参数（登录者用户保持的信息）
        String head = request.getParameter("imageName");
        //2、得到图片的存放路径
        String realPath = request.getServletContext().getRealPath("/WEB-INF/upload/");
        //3、通过图片的完整路径
        File file = new File(realPath + "/" + head);
        //4、通过截取，得到图片的后缀
        String pic = head.substring(head.lastIndexOf(".")+1);
        // 5. 通过不同的图片后缀，设置不同的响应的类型
        if("PNG".equalsIgnoreCase(pic)){
            response.setContentType("image/png");
        }else if("JPG".equalsIgnoreCase(pic) || "JPEG".equalsIgnoreCase(pic)){
            response.setContentType("image/jpeg");
        }else if("GIF".equalsIgnoreCase(pic)){
            response.setContentType("image/gif");
        }
        // 6. 利S用FileUtils的copyFile()方法，将图片拷贝给浏览器
        FileUtils.copyFile(file,response.getOutputStream());
    }

    //用户登录
    private void userLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1、获取参数
        String userName = request.getParameter("userName");
        String userPwd = request.getParameter("userPwd");
        //调用service层的方法，
        ResultInfo<User> resultInfo = userService.userLogin(userName,userPwd);

        //判断是否登录成功
        if(resultInfo.getCode() == 1){
            //将用户信息设置到session中
            request.getSession().setAttribute("user",resultInfo.getResult());
            //判断用户是否选择记住密码
            String rem = request.getParameter("rem");
            //
            if("1".equals(rem)){
                //得到Cookie对象
                Cookie cookie = new Cookie("user",userName+"-"+userPwd);
                //设置失效时间
                cookie.setMaxAge(3*24*60*60);
                response.addCookie(cookie);
            }else {
                //清空原有的cookie
                Cookie cookie = new Cookie("user",null);
                //删除cookie
                cookie.setMaxAge(0);
                //响应给客户端
                response.addCookie(cookie);
            }
            //重定向到index
           response.sendRedirect("index");
        }
        else {
            //失败
           //将resultInfo设置到request作用域
           request.setAttribute("resultInfo",resultInfo);
           //请求转发到登录页面
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
    }

    //用户退出
    private void userLogOut(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //销毁session
        request.getSession().invalidate();
        //删除cookie
        Cookie cookie = new Cookie("user",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        //重定向跳转到登录页面
        response.sendRedirect("login.jsp");
    }

    //进入个人中心
    private void userCenter(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //设置首页动态包含的页面值
        request.setAttribute("changePage","user/info.jsp");
        //设置请求转发跳转到index
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }
}
