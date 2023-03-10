package com.lezijie.note.service;

import cn.hutool.core.util.StrUtil;
import com.lezijie.note.dao.NoteTypeDao;
import com.lezijie.note.po.NoteType;
import com.lezijie.note.vo.ResultInfo;

import java.util.List;

public class NoteTypeService {
    private NoteTypeDao noteTypeDao = new NoteTypeDao();
    public List<NoteType> findTypeList(Integer userId){
        //查询用户列表哦
        List<NoteType> typeList = noteTypeDao.findTypeListByUserId(userId);
        return typeList;
    }

    /*
    * 删除类型
    * 1、判断参数是否为空
    * 2、调用dao层，通过类型ID查询云记录的数量
    * 3、如果云记录数量大于0，说明存在子记录，不可删除
    *   code=0，msg=“该类型存在子记录”
    * 4、如果不存在子记录，调用Dao层的更新方法，通过类型ID删除指定的类型记录
    * 5、判断受影响的行数是否大于0
    *   大于0 code = 1； 删除成功
    *   否则删除失败
    * 6、返回ResultInfo对象
    * */
    public ResultInfo<NoteType> deleteType(String typeId){
        ResultInfo<NoteType> resultInfo = new ResultInfo<>();
        //判断参数是否为空
        if(StrUtil.isBlank(typeId)){
            resultInfo.setCode(0);
            resultInfo.setMsg("系统异常请重试");
        }

        //调用Dao层，通过类型ID查询子记录的数量
        long noteCount = noteTypeDao.findNoteCountByTypeId(typeId);

        //判断子记录的数量
        if(noteCount > 0){
            resultInfo.setCode(0);
            resultInfo.setMsg("该子类存在子记录，不可删除！");
            return resultInfo;
        }

        //如果不存在子记录，调用Dao层的更新方法，通过id删除类型
        int row = noteTypeDao.deleteTypeById(typeId);
        //判断受响应的行数
        if(row > 0){
            resultInfo.setCode(1);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("删除失败");
        }
        return resultInfo;
    }

    /*
    *
    * */
    public ResultInfo<Integer> addOrUpdate(String typeName, Integer userId, String typeId){
        ResultInfo<Integer> resultInfo = new ResultInfo<>();

        //判断参数是否为空
        if(StrUtil.isBlank(typeName)){
            resultInfo.setCode(0);
            resultInfo.setMsg("参数类型名称不能为空");
            return resultInfo;
        }

        //调用Dao层,查询当前用户下，类型名称是否唯一，返回0或1(1 可用，0不可用)
        Integer code = noteTypeDao.checkTypeName(typeName,userId,typeId);

        if(code == 0){
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称已存在，请重新输入！");
            return resultInfo;
        }

        //判断类型ID是否为空
        Integer key = null;
        if(StrUtil.isBlank(typeId)){
            //如果为空，调用Dao层的添加方法，返回主键
            key = noteTypeDao.addType(typeName,userId);
        }else {
            //如果不为空，调用dao层的修改方法，返回受影响的行数
            key = noteTypeDao.updateType(typeName,typeId);
        }

        //判断主键/受影响的行数是否大于0
        if(key > 0){
            resultInfo.setCode(1);
            resultInfo.setResult(key);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败！");
        }
        return  resultInfo;
    }
}
