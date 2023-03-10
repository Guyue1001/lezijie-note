package com.lezijie.note.filter;

import com.lezijie.note.po.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/*
* 非法访问拦截
*   拦截的资源
*       所有的资源
*   放行的资源
*       指定页面放行 (登录，注册)
*       静态资源放行(存放static目录下的：css、js....)
*
*免登录(自动登录)
* 通过cookie和session对象实现
* 什么时候使用用户免登录
*   当用书处理未登录状态，且去请求需要登录才能访问的资源时，调用自动登录功能
*
*   实现；
*   1.获取cookie数组 request.getCookies()
*   2、判断cookie数组
*   3、遍历cookie数组，获取指定的cookie对象(name为user的cookie对象)
*   4、得到对应的cookie对象的value
*   5、通过split方法分隔对应的姓名和密码值
*   6、从数组中分别得到对应的姓名与密码值
*   7.请求转发到登录操作
*   8、return
* */
@WebFilter("/*")
public class LoginAccessFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //得到访问的路径
        String path = request.getRequestURI();//格式：项目路径/资源路径

        //指定页面，放行
        if(path.contains("/login.jsp")){
            filterChain.doFilter(request,response);
            return;
        }

        //静态资源，放行
        if(path.contains("/statics")){
            filterChain.doFilter(request,response);
            return;
        }

        //指定行为，放行
        if(path.contains("/user")){
            //得到用户行为
            String acionName = request.getParameter("actionName");
            if("login".equalsIgnoreCase(acionName)){
                filterChain.doFilter(request,response);
                return;
            }
        }
        //登录状态，放行
        User user = (User) request.getSession().getAttribute("user");
        //判断user对象是否为空
        if(user != null){
            filterChain.doFilter(request,response);
            return;
        }

        //免登录操作
        /*
        *   1、获取cookie数组
            2、判断cookie数组
            3、遍历cookie数组，获取指定的cookie对象
（name为user的cookie对象）
            4、得到指定cookie对象的value    （姓名与
密码：userName-userPwd）
            5、通过split()方法将value字符串转换成数组
            6、从数组中别得到姓名和密码
            7、请求转发跳转到登录操作    user?
actionName=login&userName=姓名&userPwd=密码
        * */
        //获取cookie
        Cookie[] cookies = request.getCookies();
        //判断cookies对象
        if(cookies != null && cookies.length > 0){
            //遍历cookie数组
            for (Cookie cookie : cookies){
                //判读是否是只当的cookie
                if("user".equals(cookie.getName())){
                    //得到对应cookie的valuse
                    String value = cookie.getValue();

                    //通过split分割
                    String[] val = value.split("-");

                    //从数组中获取name和value
                    String userName = val[0];
                    String userPwd = val[1];

                    //请求转发
                    String url = "user?actionName=login&rem=1&userName="+userName+"&userPwd="+userPwd;

                    request.getRequestDispatcher(url).forward(request,response);
                    return;
                }
            }
        }

        System.out.println("123");
        //拦截请求，重定向跳转到登录页面
        response.sendRedirect("login.jsp");
    }
}
