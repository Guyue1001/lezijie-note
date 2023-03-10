package com.lezijie.note.dao;

import com.lezijie.note.po.NoteType;
import com.lezijie.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NoteTypeDao {

    /*
    * 通过用户Id查询类型集合
    * 1、定义Sql语句
    * 2、设置参数列表
    * 3、调用BaseDao的查询方法，返回集合
    * */
    public List<NoteType> findTypeListByUserId(Integer userId){
        //定义Sql参数
        String sql = "select typeId,typeName,userId from tb_note_type where userId =?";

        //准备参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        //调用BaseDao
        List<NoteType> list = Basedao.queryRows(sql,params,NoteType.class);

        return  list;
    }

    /*
    * 通过用户id查询子记录的数量
    * */
    public long findNoteCountByTypeId(String typeId){
        //定义SQL语句
        String sql = "select count(1) from tb_note where typeId =?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        //执行SQL
        long count = (long) Basedao.findSingValue(sql,params);
        return count;
    }

    /*
    * 通过用户id去删除类型记录
    * */
    public int deleteTypeById(String typeId){
        //定义sql语句
        String sql = "delete from tb_note_type where typeId =? ";
        //设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        //调用BaseDao
        int row = Basedao.executeUpdate(sql,params);
        return row;
    }

    /**
     *
     * 查询当前登录用户下，类型名称是否唯一
     *   返回 1， 成功
     *   返回 2， 失败
     *
     * @param typeName
     * @param userId
     * @param typeId
     *
     */
     public int checkTypeName(String typeName, Integer userId, String typeId){
         //定义Sql语句
         String sql = "select * from tb_note_type where userId = ? and typeName = ?";

         //添加参数
         List<Object> params = new ArrayList<>();
         params.add(userId);
         params.add(typeName);
         //执行BAse
         NoteType noteType = (NoteType) Basedao.queryRow(sql,params,NoteType.class);

         //如果对象为空，表示可用
         if(noteType == null){
             return 1;
         }else {
             //如果是修改操作，需要判断是否是当前记录本身
             String str = ""+noteType.getTypeId()+"";
             if(typeId.equals(str)){
                return  1;
             }
         }
         return  0;
     }

    /*
    *
    * */

    public Integer addType(String typeName, Integer userId){
        Integer key = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            //得到数据库连接
            connection = DBUtil.getConnection();

            //定义Sql语句
            String sql = "insert into tb_note_type (typeName,userId) values(?,?)";
            //预编译
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //设置参数
            preparedStatement.setString(1,typeName);
            preparedStatement.setInt(2,userId);

            //执行更新
            int row = preparedStatement.executeUpdate();

            if(row > 0){
                //获取主键，返回主键的结果集
                resultSet = preparedStatement.getGeneratedKeys();
                //得到主键
                if(resultSet.next()){
                    key = resultSet.getInt(1);
                }
            }
        }catch (Exception e){

        }finally {
            //关闭资源
            DBUtil.close(resultSet,preparedStatement,connection);
        }
        return  key;

    }
    public Integer updateType(String typeName,String typeId){

        //定义Sql语句
        String sql = "update tb_note_type set typeName = ? where typeId = ?";

        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(typeId);

        int row = Basedao.executeUpdate(sql,params);
        return row;
    }
}
