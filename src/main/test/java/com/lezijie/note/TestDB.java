package com.lezijie.note;

import com.lezijie.note.util.DBUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDB {
    private Logger logger = LoggerFactory.getLogger(TestDB.class);

    //测试数据库的连接
    /*
    * 单元测试方法
    * 方法的返回值，建议void，一般没有返回值
    * 参数列表，建议空参，一般没有参数
    * 方法设置@Test注解
    * 每一个方法都能独立允许
    * */
    @Test
    public void testConnection(){
        System.out.println(DBUtil.getConnection());
        //使用日志
        logger.info("在{}时，获取数据库连接", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
