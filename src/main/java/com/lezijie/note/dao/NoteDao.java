package com.lezijie.note.dao;

import cn.hutool.core.util.StrUtil;
import com.lezijie.note.po.Note;
import com.lezijie.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {
//    添加或修改云记，返回受影响的行数

    public int addOrUpdate(Note note){
        //定义SQL语句
        String sql = "";

        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());

        //判断是否传递了主键
        if(note.getNoteId() == null){
            //添加操作
            sql = "insert into tb_note (typeId,title, content, pubTime) values (?,?,?,now())";
        }else {
            //修改操作
            sql = "update tb_note set typeId = ?, title = ?, content = ? where noteId =?";
            //只有修改操作需要这个参数
            params.add(note.getNoteId());
        }
        //设置参数BaseDao 的更新方法
        int row = Basedao.executeUpdate(sql,params);

        return  row;
    }

    public long findNoteCount(Integer userId,String title,String date,String typeId){
        String sql = "SELECT count(1) FROM tb_note n INNER JOIN " +
                " tb_note_type t on n.typeId = t.typeId " +
                " WHERE userId = ? ";

        //设置参数
        List<Object> params = new ArrayList<>();
         params.add(userId);
        //判断条件查询的参数是否为空,如果条件
        if(!StrUtil.isBlank(title)){
            //如果查询的参数为空(如果查询的参数不为空，则拼接查询所需要的参数)
            sql += " and title like concat('%',?,'%') ";
            //设置sql语句所需要的参数
            params.add(title);

        }  else if(!StrUtil.isBlank(date)){
            //如果查询的参数为空(如果查询的参数不为空，则拼接查询所需要的参数)
            sql += " and date_format(pubTime, '%Y年%m月') = ? ";
            //设置sql语句所需要的参数
            params.add(date);

        }else if(!StrUtil.isBlank(typeId)){
            sql += " and n.typeId = ? ";
            params.add(typeId);
        }

        //调用BaseDao的查询方法
        long count = (long) Basedao.findSingValue(sql, params);

        return  count;
    }

    /**
     * 分页查询当前登录用户下当前页的数据列表，返回note集合
     * @param userId
     * @param index
     * @param pageSize
     * @return
     */
    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize,String title,String date,String typeId){
        //userId, index, pageSize, title,date,typeId
        //定义sql语句
        String sql = "SELECT noteId,title,pubTime FROM tb_note n INNER JOIN " + " tb_note_type t on n.typeId = t.typeId WHERE userId = ? ";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if(!StrUtil.isBlank(title)){
            //如果查询的参数为空(如果查询的参数不为空，则拼接查询所需要的参数)
            sql += " and title like concat('%',?,'%') ";
            //设置sql语句所需要的参数
            params.add(title);

        } else if(!StrUtil.isBlank(date)){
            //如果查询的参数为空(如果查询的参数不为空，则拼接查询所需要的参数)
            sql += " and date_format(pubTime, '%Y年%m月') = ? ";
            //设置sql语句所需要的参数
            params.add(date);

        }else if(!StrUtil.isBlank(typeId)){
            sql += " and t.typeId = ? ";
            params.add(typeId);
        }

        params.add(index);
        params.add(pageSize);
        //拼接分页的sql语句(limit语句需要写在sql语句后面)
        sql += " limit ?,? ";

        //调用basedao方法
        List<Note> noteList = Basedao.queryRows(sql,params,Note.class);
        return  noteList;
    }

    //通过id查询云日记对象
    public Note findNoteById(String noteId){
        //定义sql
        String sql =  "select noteId,title,content,pubTime,typeName,n.typeId from tb_note n inner join tb_note_type t on n.typeId=t.typeId where noteId =?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        //调用BaseDao的查询方法
        Note note = (Note) Basedao.queryRow(sql,params,Note.class);
        return note;
    }

    public List<NoteVo> findNoteCountByType(Integer userId){
        String sql = "SELECT COUNT(noteId) noteCount, t.typeId,typeName groupName from tb_note n " +
                " right join tb_note_type t on n.typeId = t.typeId where userId = ? " +
                " group by t.typeId order by count(noteId) desc ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteVo> list = Basedao.queryRows(sql,params,NoteVo.class);
        return list;
    }

    //通过noteid删除云记记录
    public int deleteNoteById(String noteId){
        //准备sql
        String sql = "delete from tb_note where noteId = ? ";

        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        //调用BaseDao
        int row = Basedao.executeUpdate(sql,params);
        return  row;
    }

    //通过日期分组查询当前登录用户下的云记录数量
    public List<NoteVo> findNoteCountByDate(Integer userId){
        //定义Sql语句
        String sql = "SELECT count(1) noteCount,DATE_FORMAT(pubTime,'%Y年%m月') groupName FROM tb_note n " + " INNER JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " + " GROUP BY DATE_FORMAT(pubTime,'%Y年%m月')" +   " ORDER BY DATE_FORMAT(pubTime,'%Y年%m月') DESC ";

        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        //调用BaseDao层的方法
        List<NoteVo> list = Basedao.queryRows(sql,params,NoteVo.class);
        return  list;
    }

    //通过用户Id查询云记列表
    public List<Note> queryNoteList(Integer userId){
        //定义Sql语句
        String sql = "select lon, lat from tb_note n inner join tb_note_type t on n.typeId = t.typeId where userId = ?";

        //设置参数
        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);
        // 调用BaseDao
        List<Note> list = Basedao.queryRows(sql, params, Note.class);
        return list;
    }
}
