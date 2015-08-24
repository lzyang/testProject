package designpatterns.create.abstractfactory;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-24.
 */
public class Email implements Sender{

    @Override
    public void send() {
        System.out.println("Send Email!!");
    }
}
