package com.zpf.mall.service;

import com.zpf.mall.exception.ImoocMallException;
import com.zpf.mall.model.pojo.User;
import com.zpf.mall.model.vo.UserVO;

/**
 * 描述：     UserService
 */
public interface UserService {

    User getUser();

    void register(String userName, String password) throws ImoocMallException;

    UserVO login(String userName, String password) throws ImoocMallException;

    void updateInformation(User user) throws ImoocMallException;

    boolean checkAdminRole(User user);

    boolean checkEmailRegistered(String emailAddress);
}
