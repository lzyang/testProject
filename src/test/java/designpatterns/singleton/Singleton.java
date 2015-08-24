package designpatterns.singleton;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-24.
 */
public class Singleton {
    private static Singleton instance = null;

    private Singleton() {
    }

    /**
     * 为了多线程,以及java new操作执行原理的一致性,独立摘出方法
     */
    private static synchronized void init() {
        if (instance == null) {
            instance = new Singleton();
        }
    }

    public static Singleton getInstance() {
        if (instance == null) init();
        return instance;
    }
}
