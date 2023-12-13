package com.sky.controller.admin;


import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "套餐接口")
@RestController
@RequestMapping("/admin/setmeal")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;
    /**
     *  新增套餐
     *  1. 获取套餐分类接口(done)
     *  2. 根据套餐分类id获取菜品接口
     *  3. 上传图片接口
     *  4. 保存接口
     */
    @ApiOperation(value = "新增套餐方法")
    @PostMapping
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")  // 清空缓存
    public Result saveWithDish(@RequestBody SetmealDTO setmealDTO){
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation("套餐分页显示接口")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 套餐批量删除接口
     * @return
     */
    @ApiOperation("套餐批量删除接口")
    @DeleteMapping()
    @CacheEvict(cacheNames = "setmealCache",allEntries = true) // 清空缓存
    public Result delete(@RequestParam List<Long> ids){
        setMealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据主键id显示套餐信息
     * @param setmealId
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable(value = "id") Long setmealId){
        SetmealVO setmealVO =  setMealService.getByIdWithDish(setmealId);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @ApiOperation("修改套餐")
    @PutMapping
    @CacheEvict(cacheNames = "setmealCache",allEntries = true) // 清空缓存
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setMealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 起售停售套餐
     */
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true) // 清空缓存
    @ApiOperation(value     = "起售或者停售")
    public Result startOrStop(@PathVariable(value = "status") Integer status, Long id){
        log.info("ID ::::{}",id);
        setMealService.startOrStop(status,id);
        return Result.success();
    }

}
