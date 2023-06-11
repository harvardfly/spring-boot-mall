package com.zpf.mall.service.impl;

import com.zpf.mall.exception.ImoocMallException;
import com.zpf.mall.exception.ImoocMallExceptionEnum;
import com.zpf.mall.model.dao.UserMapper;
import com.zpf.mall.model.pojo.User;
import com.zpf.mall.model.vo.UserVO;
import com.zpf.mall.service.UserService;

import java.util.HashMap;

import com.zpf.mall.util.JwtOperatorUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：     UserService实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String userName, String password) throws ImoocMallException {
        //查询用户名是否存在，不允许重名
        User result = userMapper.selectByName(userName);
        if (result != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }

        //写到数据库
        User user = new User();
        user.setUsername(userName);
//        try {
//            user.setPassword(MD5Utils.getMD5Str(password, Constant.ICODE));
        user.setPassword(password);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public UserVO login(String userName, String password) throws ImoocMallException {
//        String md5Password = null;
//        try {
//            md5Password = MD5Utils.getMD5Str(password, Constant.ICODE);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
        User user = userMapper.selectLogin(userName, password);
        if (user == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 1.初始化
        JwtOperatorUtil jwtOperator = new JwtOperatorUtil();
        jwtOperator.setExpirationTimeInSecond(1209600L);
        jwtOperator.setSecret("aaabbbcccdddeeefffggghhhiiijjjkkklllmmmnnnooopppqqqrrrsssttt");

        // 2.设置用户信息
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("userId", user.getId());

        // 3.生成token
        String token = jwtOperator.generateToken(objectObjectHashMap);

        userVO.setToken(token);
        return userVO;
    }

    @Override
    public void updateInformation(User user) throws ImoocMallException {
        //更新个性签名
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user) {
        //1是普通用户，2是管理员
        return user.getRole().equals(2);
    }

    @Override
    public boolean checkEmailRegistered(String emailAddress) {
        User user = userMapper.selectOneByEmailAddress(emailAddress);
        if (user != null) {
            return false;
        }
        return true;
    }
}
