package com.lezijie.note;

import com.lezijie.note.dao.Basedao;
import com.lezijie.note.po.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestBase {
    //测试添加语句
    @Test
    public void testBaseAdd(){
        Basedao basedao = new Basedao();
        String sql = "insert into tb_note_type values(?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(5);
        params.add("游戏");
        params.add(1);
        basedao.executeUpdate(sql,params);
    }

    //测试查询字段语句
    @Test
    public void testBaseQuery(){
        Basedao basedao = new Basedao();
        String sql = "select count(?) from tb_note_type";
        List<Object> params = new ArrayList<>();
        params.add("typeId");
        Long count =(Long) basedao.findSingValue(sql,params);
        System.out.println(count);

    }

    //查询集合
    @Test
    public void testBaseAll(){
        Basedao basedao = new Basedao();
        String sql = "select * from tb_user where uname =? ";
        List<Object> params = new ArrayList<>();
        params.add("admin");
        List list =  basedao.queryRows(sql,params,User.class);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
           User user = (User)  iterator.next();
            System.out.println("用户姓名："+user.getUname());
            System.out.println("用户密码："+user.getUpwd());
        }
    }

    //查询对象--
    @Test
    public void testBaseFisrt(){
        Basedao basedao = new Basedao();
        String sql = "select * from tb_user where uname =? ";
        List<Object> params = new ArrayList<>();
        params.add("admin");
        User user = (User) basedao.queryRow(sql,params,User.class);
        System.out.println("用户姓名："+user.getUname());
        System.out.println("用户密码："+user.getUpwd());
    }
}
