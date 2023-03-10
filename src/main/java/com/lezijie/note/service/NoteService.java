package com.lezijie.note.service;

import cn.hutool.core.util.StrUtil;
import com.lezijie.note.dao.Basedao;
import com.lezijie.note.dao.NoteDao;
import com.lezijie.note.po.Note;
import com.lezijie.note.util.Page;
import com.lezijie.note.vo.NoteVo;
import com.lezijie.note.vo.ResultInfo;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteService {
    private NoteDao noteDao = new NoteDao();

    /*
    * 修改或添加云日记
    * */
    public ResultInfo<Note> addOrUpdate(String typeId, String title, String content,String noteId){
        ResultInfo<Note> resultInfo = new ResultInfo<>();
        //参数的非空判断
        if(StrUtil.isBlank(typeId)){
            resultInfo.setCode(0);
            resultInfo.setMsg("请选择云记类型！");
            return resultInfo;
        }
        if(StrUtil.isBlank(title)){
            resultInfo.setCode(0);
            resultInfo.setMsg("请选择云日记标题");
            return resultInfo;
        }
        if(StrUtil.isBlank(content)){
            resultInfo.setCode(0);
            resultInfo.setMsg("云记内容不能为空");
            return resultInfo;
        }

        //设置回显对象
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTypeId(Integer.parseInt(typeId));

        //判断云记ID是否为空
        if(!StrUtil.isBlank(noteId)){
            note.setNoteId(Integer.parseInt(noteId));
        }

        resultInfo.setResult(note);
        //调用dao层，添加云记录，返回受影响的行数
        int row = noteDao.addOrUpdate(note);
        //判断受影响的行数
        if(row > 0){
            resultInfo.setCode(1);
        }else {
            resultInfo.setCode(0);
            resultInfo.setResult(note);
            resultInfo.setMsg("更新失败！");
        }
        return resultInfo;
    }

    //查询云日记数量
    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId,String title,String date,String typeId){

        //设置分页参数的默认值
        Integer pageNum = 1;
        Integer pageSize = 5;//默认每页显示

        //参数的非空判断
        if(!StrUtil.isBlank(pageNumStr)){
            //设置当前的页数
            pageNum = Integer.parseInt(pageNumStr);
        }
        if(!StrUtil.isBlank(pageSizeStr)){
            //查询每页显示的数量
            pageSize = Integer.parseInt(pageSizeStr);
        }

        //判断当前用户登录的云记数量，返回总记录数(Long型)

        long count = noteDao.findNoteCount(userId,title,date,typeId);

        //判断总记录数是否大于0
        if(count < 1){
            return  null;
        }

        //如果总记录数量 > 0 调用page类的带参构造，得到其他分页参数的值，返回Page对象
        Page<Note> page = new Page<>(pageNum,pageSize,count);

        //得到数据库中分页查询的开始下标
        Integer index = (pageNum - 1) * pageSize;

        //查询当前登录用户下当前页的数据列表，返回note集合
        List<Note> noteList = noteDao.findNoteListByPage(userId, index, pageSize, title, date, typeId);

        //将note集合设置到page对象中
        page.setDataList(noteList);

        // 返回page对象
        return page;
    }

    //查询云日记详情
    public Note findNoteById(String noteId){
        //参数的非空判断
        if(StrUtil.isBlank(noteId)){
            return  null;
        }

        //调用Dao层的查询，通过noteId查询note对象
        Note note = noteDao.findNoteById(noteId);
        //返回note对象
        return note;
    }

    //通过日期分组查询当前登录用户下的云记数量
    public List<NoteVo> findNoteCountByDate(Integer userId){
        String sql = "select count(1) noteCount,DATE_FORMAT(pubTime,'%Y年%m月') groupName from tb_note n " +
                " inner join tb_note_type t " +
                " on n.typeId = t.typeId " +
                " where userId = ? " +
                " group by DATE_FORMAT(pubTime,'%Y年%m月') " +
                " order by DATE_FORMAT(pubTime,'%Y年%m月') desc";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        //调用BaseDao的查询方法
        List<NoteVo> list = Basedao.queryRows(sql,params,NoteVo.class);
        return list;
    }

    //通过类型查询
    public List<NoteVo> findNoteCountByType(Integer userId){
        return noteDao.findNoteCountByType(userId);
    }

    //删除云记
    public Integer deleteNote(String noteId){
        //判断参数
        if(StrUtil.isBlank(noteId)){
            return  0;
        }

        //调用Dao层的更新方法，返回受影响的行数
        int row = noteDao.deleteNoteById(noteId);

        //判断受影响的行数是否大于0
        if(row > 0){
            return  1;
        }
        return 0;
    }

    //通过月份查询对应云记的数量
    public ResultInfo<Map<String,Object>> queryNoteCountByMonth(Integer userId){
        ResultInfo<Map<String, Object>> resultInfo =new ResultInfo<>();

        //通过月份分类查询云记的数量
        List<NoteVo> noteVos = noteDao.findNoteCountByDate(userId);

        //判断集合是否存在
        if(noteVos != null && noteVos.size() > 0) {
            //得到月份
            List<String> monthList = new ArrayList<>();

            //得到云记集合
            List<Integer> noteCountList = new ArrayList<>();

            //遍历月份分组集合
            for (NoteVo noteVo : noteVos) {
                monthList.add(noteVo.getGroupName());
                noteCountList.add((int) noteVo.getNoteCount());
            }
            //准备Map对象 封装对应的月份与云记数量
            Map<String, Object> map = new HashMap<>();
            map.put("monthArray", monthList);
            map.put("dataArray", noteCountList);

            //将map对象设置到ResultInfo对象中
            resultInfo.setCode(1);
            resultInfo.setResult(map);

        }
        return resultInfo;
    }

    //查询用户发布云记时的坐标
    public ResultInfo<List<Note>> queryNoteLonAndLat(Integer userId){
        ResultInfo<List<Note>> resultInfo = new ResultInfo<>();

        //通过用户ID查询云记列表
        List<Note> noteList = noteDao.queryNoteList(userId);

        //判断是否为空
        if(noteList != null && noteList.size() > 0){
            resultInfo.setCode(1);
            resultInfo.setResult(noteList);
        }
        return resultInfo;
    }

}
