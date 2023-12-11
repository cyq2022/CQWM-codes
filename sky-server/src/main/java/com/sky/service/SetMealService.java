package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {

    /**
     * 新增套餐
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 套餐批量删除接口
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据主键id显示套餐信息
     * @param setmealId
     * @return
     */
    SetmealVO getByIdWithDish(Long setmealId);

    /**
     * 修改套餐
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 起售停售套餐
     *
     * @param status
     * @param setmealId
     */
    void startOrStop(Integer status, Long setmealId);
}
