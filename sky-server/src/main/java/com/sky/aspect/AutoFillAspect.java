package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类
 *
 * 自动填充 切面类
 */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     *  使用 @Aspect 注解来标记这个类，
     *  然后在这个自定义切面类中
     *  定义各种通知（advice）和切点（pointcut
     */


    /**
     * 切入点  && 并且方法上带有自定义注解
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    private void autoFillPointCut(){}

    /**
     * 前置通知  在通知中进行 公共字段自动填充
     *
     * JoinPoint 是 Spring AOP 提供的一个接口，它包含了当前连接点的信息。
     * 在 @Before 注解标记的方法中，你可以使用 JoinPoint 参数来获取关于当前连接点的一些信息，比如方法的签名、方法的参数
     * JoinPoint 的一些常用方法包括：
     * getSignature()：获取连接点的方法签名。
     * getArgs()：获取连接点的方法参数。
     * getTarget()：获取目标对象。
     * getThis()：获取代理对象。
     */
    @Before("autoFillPointCut()")
    public void beforeAutoFill(JoinPoint joinPoint){
        log.info(" 开始公共字段自动填充 ：{}",joinPoint);

        // 获取到当前被 拦截方法的 上自定义注解的 属性参数 来 确定数据库操作类型
                 // ctrl + alt + b 获取 当前接口的实现类
        MethodSignature signature =(MethodSignature)joinPoint.getSignature();
                /**
                 * 使用 MethodSignature 类型的对象，你可以获取到方法的详细信息，比如方法名、参数类型等。下面是一些常用的方法：
                 * getMethod()：获取连接点所在的方法。
                 * getName()：获取方法的名称。
                 * getReturnType()：获取方法的返回类型。
                 * getParameterNames()：获取方法的参数名数组。
                 * getParameterTypes()：获取方法的参数类型数组。
                 */
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 获取到当被兰截的方法的所有  参数-实体对象
        Object[] args = joinPoint.getArgs();  // 约定 要获取的方法参数在第一个位置 约定实体类对象在第一个
        if(args == null || args.length == 0)return;

        Object entity = args[0];    // 接收到的是约定在第一个位置的实体对象
        /**
         * 迷思：
         * 为了使接受范围更大 使用Object接受
         * 弊端是不能调用子类方法， 所以要通过反射调用子类方法， 修改参数
         */

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId(); // ThreadLocal

        //根据当前不同的操作类型，为对应的属性值

        try {
            if(operationType == OperationType.INSERT){
                // 为4个公共字段赋值
                /**
                 * 常量方法类
                 */
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateUser.invoke(entity,currentId);
                setCreateTime.invoke(entity,LocalDateTime.now());
                setUpdateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,LocalDateTime.now());
            }else if (operationType == OperationType.UPDATE){
                // 为两个公共字段赋值
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,LocalDateTime.now());
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

}
