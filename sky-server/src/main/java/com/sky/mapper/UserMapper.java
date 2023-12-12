package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid从user中获取user对象
     * @param openId
     * @return
     */
    @Select("select *  from user where openid = #{openId}")
    User getByOpenid(String openId);


    /**
     *  插入user对象
     * @param user
     */
    void insert(User user);
}
