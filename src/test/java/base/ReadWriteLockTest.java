package base;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 两种互斥锁机制：
 * <p/>
 * 1、synchronized
 * <p/>
 * 2、ReentrantLock
 * <p/>
 * ReentrantLock是jdk5的新特性，采用ReentrantLock可以完全替代替换synchronized传统的锁机制，而且采用ReentrantLock的方式更加面向对象，也更加灵活，网上有很多关于对比两者锁方式的文章，这里就不多口舌了，大家baidu、google一下就水落石出了。在本博客中也写关于这两种锁方式实现的经典例子《生产者消费者》。
 *
 * @author longgangbai
 */
public class ReadWriteLockTest {


    // 缓存都应该是单例的，在这里用单例模式设计：
    private static ReadWriteLockTest cachedData = new ReadWriteLockTest();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();//读写锁
    private Map<String, Object> cache = new HashMap<String, Object>();//缓存

    private ReadWriteLockTest() {
    }

    public static ReadWriteLockTest getInstance() {
        return cachedData;
    }

    // 读取缓存：
    public Object read(String key) {
        lock.readLock().lock();
        Object obj = null;
        try {
            obj = cache.get(key);
            if (obj == null) {
                lock.readLock().unlock();
                // 在这里的时候，其他的线程有可能获取到锁
                lock.writeLock().lock();
                try {
                    if (obj == null) {
                        obj = "查找数据库"; // 实际动作是查找数据库
                        // 把数据更新到缓存中：
                        cache.put(key, obj);
                    }
                } finally {
                    // 当前线程在获取到写锁的过程中，可以获取到读锁，这叫锁的重入，然后导致了写锁的降级，称为降级锁。
                    // 利用重入可以将写锁降级，但只能在当前线程保持的所有写入锁都已经释放后，才允许重入 reader使用
                    // 它们。所以在重入的过程中，其他的线程不会有获取到锁的机会（这样做的好处）。试想，先释放写锁，在
                    // 上读锁，这样做有什么弊端？--如果这样做，那么在释放写锁后，在得到读锁前，有可能被其他线程打断。
                    // 重入————>降级锁的步骤：先获取写入锁，然后获取读取锁，最后释放写入锁（重点）
                    lock.readLock().lock();
                    lock.writeLock().unlock();
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return obj;
    }
}
