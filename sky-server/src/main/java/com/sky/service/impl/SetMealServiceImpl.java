package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
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

    @Autowired
    private DishMapper dishMapper;

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

    /**
     * 套餐批量删除接口
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        // 在售卖中不可删除
        for (Long id : ids){
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        // 批量删除套餐
        setmealMapper.deleteBySetmealIds(ids);
        // 套餐删除了， 对应的套餐菜品关系也要删除
        for(Long id : ids) {
            setmealDishMapper.deleteById(id);
        }
    }

    /**
     * 根据主键id显示套餐信息
     * @param setmealId
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long setmealId) {
        //返回vo

        Setmeal setmeal = setmealMapper.getById(setmealId);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        // 根据套餐查询菜品list
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(setmealId);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        List<SetmealDish> setmealDishs = setmealDTO.getSetmealDishes();


        log.info(",,,,setmealDTO:{}" , setmealDTO);
        //修改套餐
        setmealMapper.update(setmeal);
        //修改菜品-套餐表  删除再插入
        Long setmealDTOId = setmealDTO.getId();
        setmealDishMapper.deleteById(setmealDTOId);

        for (SetmealDish setmealDish : setmealDishs){
            setmealDish.setSetmealId(setmealDTOId);
        }
        log.info(",,,,setmealDishs:{}" , setmealDishs);
        setmealDishMapper.insertBatch(setmealDishs);

    }

    @Override
    public void startOrStop(Integer status, Long setmealId) {
//              * - 可以对状态为起售的套餐进行停售操作，可以对状态为停售的套餐进行起售操作
//              * - 起售套餐时，如果套餐内包含停售的菜品，则不能起售
//            TODO  * - 起售的套餐可以展示在用户端，停售的套餐不能展示在用户端
        // 如果要起售：：：
        if (status == StatusConstant.ENABLE) {
            // 起售时若有停售的菜品则不能起售
            // 用setmeal_id 获取 setmeal-dish list集合
            List<SetmealDish> setmealDishList = setmealDishMapper.getBySetmealId(setmealId);
            // 对集合遍历取dishId查询是否停售
            for (SetmealDish setmealDish : setmealDishList) {
                Long dishId = setmealDish.getDishId();
                Dish dish = dishMapper.getById(dishId);
                if (dish.getStatus() == StatusConstant.DISABLE){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        // 停售起售套餐
        Setmeal setmeal = Setmeal.builder().id(setmealId).status(status).build();
        log.info("..setmeal:{}",setmeal);
        setmealMapper.update(setmeal);

    }


    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }


    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
