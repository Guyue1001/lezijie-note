package com.lezijie.note;

import com.lezijie.note.dao.Basedao;
import com.lezijie.note.dao.NoteTypeDao;
import com.lezijie.note.po.NoteType;
import com.lezijie.note.service.NoteTypeService;
import org.junit.Test;

import java.util.List;

public class TestType {
    NoteTypeDao noteTypeDao = new NoteTypeDao();

    //查用类型类别
    @Test
    public void test1(){
        NoteTypeService service = new NoteTypeService();
        List<NoteType> noteType = service.findTypeList(1);
        System.out.println(noteType);
    }
    //通过ID查询类型的子记录的数量
    @Test
    public void test2(){
        long row = noteTypeDao.findNoteCountByTypeId("1");
        System.out.println(row);
    }

    //通过ID删除类型
    @Test
    public void test3(){
        long row = noteTypeDao.deleteTypeById("1");
        System.out.println(row);
    }


}
