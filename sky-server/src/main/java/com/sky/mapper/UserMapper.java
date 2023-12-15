package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

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

    @Select("select * from user where id = #{id}")
    User getById(Long userId);

    /**
     * 用户数据统计
     * @param map
     * @return
     */

    Integer countByMap(Map map);
}
