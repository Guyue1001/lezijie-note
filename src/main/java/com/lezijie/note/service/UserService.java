package com.lezijie.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.lezijie.note.dao.UserDao;
import com.lezijie.note.po.User;
import com.lezijie.note.vo.ResultInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class UserService {
    private UserDao userDao = new UserDao();

    //用户登录
    public ResultInfo<User> userLogin(String userName, String userPwd){
        //创建返回的结果类型
        ResultInfo<User> resultInfo = new ResultInfo<>();

        //将登录信息返沪给页面先显示
        User u = new User();
        u.setUname(userName);
        u.setUpwd(userPwd);
        resultInfo.setResult(u);

        //判断用户密码是否为空
        if(StrUtil.isBlank(userName) || StrUtil.isBlank(userPwd)){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户姓名或密码不能为空 ！");
            return resultInfo;
        }

        //查询用户对象
        User user = userDao. queryUserByName(userName);

        //判断用户是否为空
        if(user == null){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户名不存在！");
            return resultInfo;
        }

        //用户名存在,判断密码
        userPwd = DigestUtil.md5Hex(userPwd);
        if(!userPwd.equals(user.getUpwd())){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户名密码不正确");
            return resultInfo;
        }

        //密码正确
        resultInfo.setCode(1);
        resultInfo.setResult(user);

        return  resultInfo;
    }

    //验证昵称的唯一性
   /*
        1、判断昵称是否为空
            为空返回 “0”
        2、掉用dao层，通过用户ID和昵称查询用户对象
        3、判断用户对象存在
            存在  返回 “0”
            不存在 返回 “1”
    */
    public Integer checkNick(String nick, Integer userId){
        //判定昵称是否为空
        if(StrUtil.isBlank(nick)){
            return 0;
        }
        //调用dao层，通过用户id查询
        User user = userDao.queryUserByNickAndUserId(nick,userId);

        //判读用户对象存在
        if(user != null){
            return 0;
        }
        return 1;
    }

    //修改用户信息
    public ResultInfo<User> updateUser(HttpServletRequest request){
        ResultInfo<User> resultInfo = new ResultInfo<>();
        //获取参数
        String nick = request.getParameter("nick");
        String mood = request.getParameter("mood");

        //参数的非空效验
        if(StrUtil.isBlank(nick)){
            //如果昵称为空，将状态码和错误信息设置resultInfo对象中
            resultInfo.setCode(0);
            resultInfo.setMsg("用户昵称不能为空");
            return  resultInfo;
        }

        //从session作用域中获取用户对象(默认头像)
        User user =(User) request.getSession().getAttribute("user");

        //设置昵称和头像
        user.setNick(nick);
        user.setMood(mood);

        //实现文件上传
        try{
            //获取Part对象
            Part part = request.getPart("img");
            //通过Part对象上传文件的文件名
            String header = part.getHeader("Content-Disposition");
            //获取具体请求头对应的值
            String str = header.substring(header.lastIndexOf("=")+2);
            //获取文件的上传名
            String fileName = str.substring(0,str.length()-1);
            //判定文件名
            if(!StrUtil.isBlank(fileName)){
                //如果用户上传了头像，就更新
                user.setHead(fileName);
                String filePath = request.getServletContext().getRealPath("/WEB-INF/upload/");

                //上传文件到指定目录
                part.write(filePath + "/" + fileName);
            }
            // 获取文件存放的路径
//            上传文件到指定目录
        }catch (Exception e){
            e.printStackTrace();
        }

        //调用dao层1更新方法，返回受影响的行数
        int row = userDao.updateUser(user);

        //判断受影响的行数
        if(row > 0){
            resultInfo.setCode(1);
            request.getSession().setAttribute("user",user);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败");
        }

        return resultInfo;
    }
}
