package com.lezijie.note.dao;

import cn.hutool.db.DbUtil;
import com.lezijie.note.util.DBUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/*
* 基础JDBC类操作类
*   更新操作(增删改)
*   查询操作
*       查询一个字段(返回一条记录且只有一个字段,例如：查询总数量)
*       查询集合
*       查询某个对象
*
* */
public class Basedao {
    /*
     * 更新操作
     *   添加、修改、删除
     *   得到数据库连接
     *   定义sql
     *   预编译
     *   如果有参数，设置参数，下标1开始
     *   执行sql
     * 关闭资源
     *
     * 注意：需要两个参数 sql语句、所需参数的集合
     * */
    public static int executeUpdate(String sql, List<Object> params){
        int row = 0;//受影响的行数
        //数据库连接对象
        Connection connection = null;
        //预编译对象
        PreparedStatement preparedStatement = null;

        try {
            //得到数据库连接
            connection = DBUtil.getConnection();

            //预编译
            preparedStatement = connection.prepareStatement(sql);

            //有参数则设置
            if(params != null && params.size() > 0){
                //循环设置参数类型为Object
                for (int i =0; i < params.size(); i++){
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }

            //执行更新，返回受影响的行数
            row = preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(null,preparedStatement,connection);
        }
        return row;
    }


    /*
    * 查询一个字段(只会返回一条记录且只有一个字段)
    * 1、得到数据库连接
    * 2、定义sql语句
    * 3、预编译
    * 4、有参数就设置参数
    * 5、执行查询，返回结果集
    * 6、判断并分析结果集
    * 7、关闭资源
    * @param sql
    * @param params
    * */
    public static Object findSingValue(String sql, List<Object> params){
        Object object = null;
        //连接数据库对象
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            //获取数据库连接
            connection = DBUtil.getConnection();

            //预编译
            preparedStatement = connection.prepareStatement(sql);

            //有参数就设置参数
            if(params != null && params.size() >0){
                for (int i =0; i < params.size(); i++){
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }

            //执行查询，返回结果集
            resultSet = preparedStatement.executeQuery();
            //判断并分析结果集
            if(resultSet.next()){
                //获得查询的字段
                object = resultSet.getObject(1);
            }
        }catch (Exception e){

        }finally {
            DbUtil.close(resultSet,preparedStatement,connection);
        }

        return  object;
    }


    /*
    * 查询集合(javaBean中的字段与数据库中表的字段对应)
    * 获取数据库连接
    * 定义sql语句
    * 预编译
    * 如果有参数，则设置参数，下标从1开始(数据或集合)
    * 执行查询，得到的结果集
    * 得到结果集中的元数据对象
    * 判断并分析结果集
    *   实例化短袖
    *   遍历查询字段的数量，得到数据库中的每一个列表名
    *   通过反射，使用列名得到对应的field对象
    *   拼接set方法，得到字符串
    *   通过反射将set方法的字符串反射成类中的指定的set方法
    *   通过invoke调用seet方法
    *   将对应的javabean设置到集合中
    * 关闭资源
    * */
    public static List queryRows(String sql, List<Object> params, Class cls){

        List list = new ArrayList();
        //数据库连接对象
        Connection connection = null;
        //预编译对象
        PreparedStatement preparedStatement= null;
        //结果集对象
        ResultSet resultSet = null;

        try {
            //得到数据库连接
            connection = DBUtil.getConnection();

            //预编译
            preparedStatement = connection.prepareStatement(sql);

            //如果有参数，设置结果集
            if(params != null && params.size() >0){
                for (int i=0; i < params.size(); i++){
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }

            //执行查询，返回结果集
            resultSet = preparedStatement.executeQuery();

            //
            //

            /*
            *     得到结果集的元数据对象(查询到的字段数量以及查询了那些字段)
            *     元数据：用于描述数据的数据
            *     ResultSetMetaData 叫元数据，是数据库 列对象，以列为单位封装为对象。
            *     元数据，指的是其包含列名，列值，列类型，列长度等等有用信息。
            * */
            ResultSetMetaData resultSetMetaData=resultSet.getMetaData();

            //得到查询的字段数量
            int fieldNum = resultSetMetaData.getColumnCount();

            //判断并分析结果集
            while (resultSet.next()){
                //实例化对象
                Object object = cls.newInstance();
                //遍历查询的字段数量,得到数据库中查询的每一段列名
                for (int i =1; i <= fieldNum; i++) {
                    //得到对应的查询的列明名
                    // getColumnLable 获取列明或别名
                    //getColumnName() 获取列名
                    String columnName = resultSetMetaData.getColumnLabel(i);

                    //通过反射，使用列名得到对应的field对象
                    Field field = cls.getDeclaredField(columnName);

                    //拼接set方法，得到字符串
                    String setMethod = "set"+columnName.substring(0,1).toUpperCase() + columnName.substring(1);
                    //通过反射将set方法字符串，反射成类中对应的set方法
                    // getDeclaredMethod(方法名，返回的数据类型)
                    Method method = cls.getDeclaredMethod(setMethod,field.getType());

                    //得到每一个字段对应的值
                    Object value = resultSet.getObject(columnName);

                    //通过invoke方法调用set方法
                    method.invoke(object,value);
                }

                //将javaBean设置到集合中
                list.add(object);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            DBUtil.close(resultSet,preparedStatement,connection);
        }

        return list;

    }

    /*'
    * 查询对象:就是查询集合中的第一条数据
    * */
    public static Object queryRow(String sql,List<Object> params, Class cls){
        List list = queryRows(sql,params,cls);
        Object object = null;
        //如果集合不为空，则获取查询的第一条数据
        if(list != null && list.size() >0){
            object = list.get(0);
        }
        return  object;
    }
}


