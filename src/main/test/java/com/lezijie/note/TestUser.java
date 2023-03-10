package com.lezijie.note;

import com.lezijie.note.dao.UserDao;
import com.lezijie.note.po.User;
import org.junit.Test;

public class TestUser {
    @Test
    public void testQueryUserByName(){
        UserDao userDao = new UserDao();
        User user = userDao.queryUserByName("admin");
    }
}
