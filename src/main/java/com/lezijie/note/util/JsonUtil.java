package com.lezijie.note.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class JsonUtil {
    //将数据转化成JSON格式发送给JSON()
public static void toJson(HttpServletResponse response, Object result){
    // 设置响应类型及编码格式 （json类型）
    response.setContentType("application/json;charset=UTF-8");
    try {
        //得到字符输出流
        PrintWriter out = response.getWriter();
        //通过fastjson方法，将ResultInfo转成JSON格式
        String json = JSON.toJSONString(result);
        //输出流输出JSON字符串
        out.write(json);
        out.close();
    }catch (Exception e){
        e.printStackTrace();
    }
}
}
