package com.lezijie.note.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class DBUtil {
    //定义配置对象
    private static Properties properties = new Properties();

    static {
        try {

            //加载配置文件(输入流)
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");

            // // 通过load()方法将输入流加载到配置文件对象中
            properties.load(in);

            // 通过配置对象的getProperty()方法得到驱动名，加载驱动
            Class.forName(properties.getProperty("jdbcName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到数据库连接
     *
     * @return
     */

    public static Connection getConnection(){
        //获取连接
        Connection connection = null;
        try {
            String dbUrl = properties.getProperty("dbUrl");
            String dbName = properties.getProperty("dbName");
            String dbPwd = properties.getProperty("dbPwd");

            //得到数据库连接
            connection = DriverManager.getConnection(dbUrl,dbName,dbPwd);
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 关闭资源
     * 当资源对象不为空时，关闭资源
     *
     * @param resultSet
     * @param preparedStatement
     * @param connection
     */

    public static void close(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection){
        try {
            if(resultSet != null){
                resultSet.close();//关闭资源
            }

            if(preparedStatement != null){
                preparedStatement.close();
            }

            if(connection != null){
                connection.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
