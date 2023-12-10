package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorsMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorsMapper dishFlavorsMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品  和 对应的口味数据
     * @param dishDTO
     */
    @Transactional // 对多表修改 开始事务
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        // 向菜品表插入一条数据
        dishMapper.insert(dish);
        log.info("向菜品表插入了一条数据");

        Long dishId = dish.getId();

        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorsMapper.insertBatch(flavors);
            log.info("向口味表插入了{}条数据",flavors.size());
        }
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO>  page =  dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     *  批量删除菜品
     */
    @Override
    @Transactional // 开启事务
    public void deleteBatch(List<Long> ids) {
        // 在售卖中的菜品不能删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
             throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 关联某个套餐时不能
        List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIdsByDishIds != null && setmealIdsByDishIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

         //开始删除菜品和口味
       // for (Long  id  : ids){
            dishFlavorsMapper.deleteByDishIds(ids);
            dishMapper.deleteByIds(ids);
       // }


    }

    /**
     * 根据菜品id 设置回显页面
     * @param dishId
     * @return
     */
    @Override
    public DishVO getById(Long dishId) {
        // private List<DishFlavor> flavors = new ArrayList<>();
        DishVO dishVO = new DishVO();

        //查询dish表 返回dish
        Dish dish = dishMapper.getById(dishId);
        BeanUtils.copyProperties(dish,dishVO);
        //查询flavor 返回口味集合
        List<DishFlavor> list =  dishFlavorsMapper.getByDishId(dishId);
        dishVO.setFlavors(list);
        return dishVO;
    }

    /**
     * 启售禁售菜品
     * @param status
     */
    @Override
    public void startOrStop(Integer status,Long dishId) {

        Dish dish = Dish.builder()
                .status(status)
                .id(dishId)
                .build();
        dishMapper.update(dish);
    }


    /**
     * 修改菜品信息
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        // 更新 Dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        // 更新 private List<DishFlavor> flavors = new ArrayList<>();
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        // 为口味赋值菜品id
        // 删除原先的再赋新值
        for (DishFlavor flavor : dishFlavors) {
            flavor.setDishId(dishDTO.getId());
            dishFlavorsMapper.deleteByDishId(flavor.getDishId()); // 删除
        }
        dishFlavorsMapper.insertBatch(dishFlavors);
    }
}
