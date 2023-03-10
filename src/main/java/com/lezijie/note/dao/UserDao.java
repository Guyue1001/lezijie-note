package com.lezijie.note.dao;

import com.lezijie.note.po.User;
import com.lezijie.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    /*
    * 获取数据库连接
    * 定义sql语句
    * 预编译
    * 设置参数
    * 执行查询，返回结果集
    * 判定并分析结果集
    * 关闭资源
    * */
    public User queryUserByName(String userName){

        User user = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            //获取数据库连接
            connection = DBUtil.getConnection();
            
            //定义sql语句
            String sql = "select  * from tb_user where uname =?";
            
            //预编译
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setString(1,userName);
            //执行查询
            resultSet = preparedStatement.executeQuery();


//            判断分析结果集
            if(resultSet.next()){
                user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setUname(userName);
                user.setHead(resultSet.getString("head"));
                user.setMood(resultSet.getString("mood"));
                user.setNick(resultSet.getString("nick"));
                user.setUpwd(resultSet.getString("upwd"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //关闭资源
             DBUtil.close(resultSet,preparedStatement,connection);
        }
        return user;
    }

    //通过昵称与用于id查询用户对象
    public User queryUserByNickAndUserId(String nick, Integer userid){
        //定义SQL语句
        String sql = "select * from tb_user where nick = ? and userId !=?";
        //设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(nick);
        params.add(userid);
        //调用BaseDao的查询方法
        User user = (User) Basedao.queryRow(sql,params,User.class);
        return  user;
    }

    //修改用户信息
    public int updateUser(User user){
        //定义SQL语句
        String sql = "update tb_user set nick = ?, mood =?, head =? where userId =?";
        //设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());
        //调用Base的更新方法，返回受影响的行数
        int row = Basedao.executeUpdate(sql,params);
        return  row;
    }
}
