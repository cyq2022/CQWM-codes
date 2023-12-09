package com.sky.context;

public class BaseContext {


    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //设置当前线程局部变量的值
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
