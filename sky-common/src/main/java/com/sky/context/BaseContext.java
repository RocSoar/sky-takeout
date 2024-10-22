package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id); //设置当前线程的线程局部变量的值
    }

    public static Long getCurrentId() {
        return threadLocal.get(); //获取当前线程的线程局部变量的值
    }

    public static void removeCurrentId() {
        threadLocal.remove(); //移除当前线程的线程局部变量的值
    }

}
