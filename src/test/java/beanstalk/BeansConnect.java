package beanstalk;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import beanstalk.BeansConnectFactory.Builder;
import org.x.beanstalk.Client;
import org.x.beanstalk.Job;

public class BeansConnect {

    private Client client;

    public BeansConnect(Builder builder) {

        addDir(System.getProperty("java.class.path"));

        client = new Client();
        client.Init(builder.getServer(), builder.getPort(), builder.getTimeout_secs());
        if (builder.getUse() != null) {
            client.use(builder.getUse());
        } else if (builder.getWatch() != null) {
            client.watch(builder.getWatch());
        } else {
            throw new NullPointerException("use or watch not all null.");
        }
    }

    public boolean ping() {
        return client.ping();
    }

    public boolean use(String tube) {
        return client.use(tube);
    }

    public boolean watch(String tube) {
        return client.watch(tube);
    }

    public boolean ignore(String tube) {
        return client.ignore(tube);
    }

    public long put(String body, long priority, long delay, long ttr) {
        return client.put(body, priority, delay, ttr);
    }

    public boolean del(long id) {
        return client.del(id);
    }

    public boolean del(Job job) {
        return client.del(job);
    }

    public boolean reserve(Job job) {
        return client.reserve(job);
    }

    public boolean reserve(Job job, long timeout) {
        return client.reserve(job, timeout);
    }

    public boolean release(Job job, long priority, long delay) {
        return client.release(job, priority, delay);
    }

    public boolean release(long id, long priority, long delay) {
        return client.release(id, priority, delay);
    }

    public boolean bury(Job job, long priority) {
        return client.bury(job, priority);
    }

    public boolean bury(long id, long priority) {
        return client.bury(id, priority);
    }

    public boolean touch(Job job) {
        return client.touch(job);
    }

    public boolean touch(long id) {
        return client.touch(id);
    }

    public boolean peek(Job job, long id) {
        return client.peek(job, id);
    }

    public boolean peek_ready(Job job) {
        return client.peek_ready(job);
    }

    public boolean peek_delayed(Job job) {
        return client.peek_delayed(job);
    }

    public boolean peek_buried(Job job) {
        return client.peek_buried(job);
    }

    public boolean kick(int bound) {
        return client.kick(bound);
    }

    public void connect(String host, int port, float secs) {
        client.connect(host, port, secs);
    }

    public void reconnect() {
        client.reconnect();
    }

    public boolean disconnect() {
        return client.disconnect();
    }

    public String list_tube_used() {
        return client.list_tube_used();
    }

    public HashMap<String, String> stats() {
        return client.stats();
    }

    public HashMap<String, String> stats_job(long id) {
        return client.stats_job(id);
    }

    public HashMap<String, String> stats_tube(String name) {
        return client.stats_tube(name);
    }

    public ArrayList<String> list_tubes() {
        return client.list_tubes();
    }

    public ArrayList<String> list_tubes_watched() {
        return client.list_tubes_watched();
    }

    public static void addDir(String s) {
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = s;
            field.set(null, tmp);
        } catch (IllegalAccessException e) {
            System.out.println("Failed to get permissions to set library path");
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            System.out.println("Failed to get field handle to set library path");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        BeansConnect build = BeansConnectFactory.builder().server("10.58.22.16").watch("ATP").build();
        while (true) {
            Job job = new Job();
            boolean reserve = build.reserve(job);
            if (reserve) {
                System.out.println(job.getBody());
            }
            break;
        }
    }
}
