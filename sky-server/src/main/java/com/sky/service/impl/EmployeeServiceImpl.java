package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.beans.beancontext.BeanContext;
import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端 传过来的密码 md5 加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    /**
     *  新增员工
     *  传入到实体类 最好使用dto
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {

        //  测试 获取当前线程 的id

        System.out.println("当前线程的id : " + Thread.currentThread().getId());

        Employee employee = new Employee();

        // 将对象属性拷贝 到对象 beanutils
        BeanUtils.copyProperties(employeeDTO,employee);

        // 常量类， 表示状态StatusConstant
        employee.setStatus(StatusConstant.ENABLE);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        // PasswordConstant.DEFAULT_PASSWORD 密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        /**
         *  如何获取拦截器获取到的token中信息 -->>
         *  客户端每次发起的请求都是一个线程， ThreadLocal
         */
        // 设置创建人
        // 设置创建人 修改人
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }


    /**
     *  分页查询server 业务
     * @param employeePageQueryDTO
     * name  page  pageSize
     *
     * 使用 mybatis 的 pagehelper 插件 简化分页开发
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {

        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());

    }


//    @Override
//    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
//
//        //使用 mybatis 的 pagehelper 插件  动态计算
//        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
//        Page<Employee> page =  employeeMapper.pageQuery(employeePageQueryDTO);  // page 存储分页后数据
//
//        long total = page.getTotal();   // page.getTotal()
//        List<Employee> result = page.getResult();  // 获得数据集合
//        PageResult pageResult = new PageResult(total, result);
//
//        return pageResult;
//    }


    /**
     * 启用禁用员工账号业务
     * 动态更新
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //为了 update 通用性 让update 操作 实体类
        //为了提升update的复用性，根据传入的数据进行动态sql。最坏的情况肯定是修改所有参数



        Employee employee = Employee.builder()  // 风格
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);
    }


    /**
     * 根据id查询员工信息
     * @return
     */
    @Override
    public Employee getById(Long id) {

        Employee employee = employeeMapper.getById(id);
        employee.setPassword("******");
        return employee;
    }


    /**
     * 编辑员工信息业务
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

}
