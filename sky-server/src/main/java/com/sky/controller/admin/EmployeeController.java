package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理 ddd
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录方法")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工登出方法")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     *  新增员工 save
     * @param employeeDTO
     * @return
     */
    @ApiOperation(value = "新增员工save")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){

        //  测试 获取当前线程 的id


        System.out.println("当前线程的id : " + Thread.currentThread().getId());

        log.info("EmployeeDTO :{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }


    /**
     *  员 工分页查询
     * 前端请求格式是 query请求参数  ， 正常用dto接受即可， 请求体需要用responsebody
     * 分页数据封装到PageResult中
     *
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询参数：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        Result<PageResult> success = Result.success(pageResult);
        success.setMsg("分页查询成功");
        return success;
    }


    /**
     *  启用禁用 员工账号
     * @param status 状态 1 启用 0 禁用
     * @param id 用户id
     * @return
     */
    @ApiOperation(value = "启用禁用 员工账号")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable("status") Integer status , Long id){
        log.info("启用禁用员工账号：{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }


    /**
     *  根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询员工信息")
    public Result<Employee> getById(@PathVariable("id") Long id){
        log.info("根据id查询员工信息 : {}",id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }


    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @ApiOperation("编辑员工信息")
    @PutMapping
    public Result update( @RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息 : {}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
