package com.lezijie.note.filter;

import cn.hutool.core.util.StrUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/*
* 乱码原因：
* 服务器默认编码：IOS-8859-1 不支持中文
* 乱码情况
*   post
*       tomcat7以下 乱码
*       tomcat8以上 乱码
*   get
*       tomcat7以下 乱码
*       tomcat8以上 不乱码
* */
@WebFilter("/*")
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //基于HTTP
        HttpServletRequest request  =(HttpServletRequest)servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;

        //处理POST请求
        request.setCharacterEncoding("UTF-8");

        // 得到请求类型 （GET|POST）
        String method = request.getMethod();

        // 如果是GET请求，则判断服务器版本
        if("GET".equalsIgnoreCase(method)){
            //服务器版本
            String serverInfo = request.getServletContext().getServerInfo();
            String version = serverInfo.substring(serverInfo.lastIndexOf("/")+1,serverInfo.indexOf("."));

            //判断服务器版本
            if(version != null && Integer.parseInt(version) < 8){
                MyWapper myWapper = new MyWapper(request);
                //放行资源
                filterChain.doFilter(myWapper,response);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }


    class MyWapper extends  HttpServletRequestWrapper{

        //定义成员变量
        private HttpServletRequest request;

        //得到需要处理的request对象
        public MyWapper(HttpServletRequest request) {
            super(request);
            this.request= request;
        }

        @Override
        public String getParameter(String name) {
            String value = request.getParameter(name);
            //判断参数是否为空
            if(StrUtil.isBlank(value)){
                return  value;
            }

            //通过new String处理乱码
            try {
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
            }catch (Exception e){
                e.printStackTrace();
            }
            return super.getParameter(name);
        }
    }



}

