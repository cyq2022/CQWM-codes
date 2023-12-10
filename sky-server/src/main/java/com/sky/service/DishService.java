package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品  和 对应的口味数据
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     *  批量删除菜品
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据菜品id 设置回显页面
     * @param dishId
     * @return
     */
    DishVO getById(Long dishId);

    /**
     * 启售禁售菜品
     * @param status
     */
    void startOrStop(Integer status,Long dishId);

    /**
     * 修改菜品信息
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 根据菜品分类id 查询菜品
     *
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);
}
