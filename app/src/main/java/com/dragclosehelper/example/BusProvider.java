package com.dragclosehelper.example;

import com.squareup.otto.Bus;

/**
 * @author Jsonshi
 * @date 2020/7/10
 * <p>
 * Description:
 */
public class BusProvider {
    /**
     * 使用volatile来修饰多线程访问变量的情况
     */
    private volatile static Bus bus = null;

    private BusProvider() {
    }

    public static Bus getInstance() {
        if (bus == null) {
            /**
             *  /加上sunchronized来防止并发
             */
            synchronized (BusProvider.class) {
                bus = new Bus();
            }
        }
        return bus;
    }
}
