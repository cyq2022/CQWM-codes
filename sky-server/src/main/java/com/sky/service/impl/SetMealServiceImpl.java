package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired

    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        // dto中包含菜品关系
        // private List<SetmealDish> setmealDishes = new ArrayList<>();
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //存储 setmeal
        setmealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();
        log.info("setmealId:{}",setmealId);
        //储存套餐菜品映射信息
        List<SetmealDish> setmealList = setmealDTO.getSetmealDishes();
        for(SetmealDish dish :  setmealList){
            dish.setSetmealId(setmealId);
        }
        log.info("setmealList: {} ",setmealList);
        setmealDishMapper.insertBatch(setmealList);
    }
}
