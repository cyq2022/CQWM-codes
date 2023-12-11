package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据 菜品的id 来查询菜品套餐关系表中 返回套餐id
     * @param dishIds
     * @return
     */
    public List<Long> getSetmealIdsByDishIds(List<Long> dishIds);


    /**
     *  新增套餐 ， 向setmealdish关系表中插入新增套餐下的菜品集合
     * @param setmealDishs
     */
    void insertBatch(List<SetmealDish> setmealDishs);


    /**
     * 根据套餐id删除对应的菜品-套餐记录
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteById(Long id);
}
