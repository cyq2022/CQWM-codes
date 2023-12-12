package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品    优化： 添加redis缓存
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        log.info("当前查询的分类id： {}",categoryId);
        //  构造redis中的key， 规则 dish_+categoryId
            String  key = "dish_" + categoryId;
        //  查询redis中是否存在菜品数据
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<DishVO> list = (List<DishVO>) valueOperations.get(key);

        //  若存在， 直接返回无需查询
        if(list != null && list.size() > 0){
            return Result.success(list);
        }

        //  若不存在， 查询并且放入缓存
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        list = dishService.listWithFlavor(dish);
        // 放入缓存中
        valueOperations.set(key,list);

        return Result.success(list);
    }

}
