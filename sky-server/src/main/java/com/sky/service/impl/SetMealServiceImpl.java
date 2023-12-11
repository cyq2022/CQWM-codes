package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 使用插件pagehelper
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealPageQueryDTO,setmeal);
        //TODO 可以把dto拷贝到setmeal对象
        // 查询数据
        log.info("传入setmealPageQueryDTO：{}",setmealPageQueryDTO);
        Page<SetmealVO> page = setmealMapper.select(setmeal);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }
}
