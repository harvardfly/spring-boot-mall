package com.zpf.mall.model.dao;

import com.zpf.mall.model.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByName(String userName);

    // 如果Mapper接口中的方法的参数不止一个，需要使用@Param注解
    User selectLogin(@Param("userName") String userName, @Param("password")String password);
}