package designpatterns.abstractfactory;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-24.
 */
public class MobileMsg implements Sender {
    @Override
    public void send() {
        System.out.println("Mobile Message Send!");
    }
}
