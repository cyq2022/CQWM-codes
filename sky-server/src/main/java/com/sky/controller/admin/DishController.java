package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@Api(tags = "菜品接口")
@RequestMapping("/admin/dish")
public class DishController {


    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品           redis  优化 ，清空缓存
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "新增菜品接口")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：dishDTO : {}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        // 清空缓存
        String key = "dish_" + dishDTO.getCategoryId();
        cleanRedis(key);

        return Result.success();
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @ApiOperation(value = "分页查询菜品")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询controller： dishPageQueryDTO：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     *  批量删除菜品
     */
    @DeleteMapping
    @ApiOperation(value = "菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品删除列表：{}",ids);
        dishService.deleteBatch(ids);

        // 清空缓存
        cleanRedis("dish_*");

        return Result.success();
    }

    /**
     * 根据菜品id 设置回显页面
     * @param DishId
     * @return
     */
    @ApiOperation(value = "根据菜品id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable(value = "id") Long DishId){
        DishVO dishVO = dishService.getById(DishId);
        return Result.success(dishVO) ;
    }

    /**
     * 启售禁售菜品
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "启售禁售菜品")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable(value = "status") Integer status , Long id){
        log.info("启用禁用员工账号：{},{}",status,id);
        dishService.startOrStop(status,id);

        // 清空缓存
        cleanRedis("dish_*");

        return Result.success();
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "修改菜品信息")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);

        // 清空缓存
        cleanRedis("dish_*");

        return Result.success();
    }

    /**
     * 根据菜品分类id 查询菜品
     * @param categoryId
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> dishList = dishService.list(categoryId);
        return Result.success(dishList);
    }


    private void cleanRedis(String patten){
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);
    }
}
