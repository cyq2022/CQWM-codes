package com.sky.mapper;


import com.sky.entity.DishFlavor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishFlavorsMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    public void insertBatch(List<DishFlavor> flavors);


    /**
     * 根据菜品id删除对应的口味
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id  = #{dishId}")
    public void deleteByDishId(Long dishId);


    /**
     * 根据菜品id批量删除对应的口味
     * @param dishIds
     */
    public void deleteByDishIds(List<Long> dishIds);


    /**
     * 根据菜品id 返回口味集合
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

}
