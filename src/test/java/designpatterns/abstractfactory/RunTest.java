package designpatterns.abstractfactory;

import org.junit.Test;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-24.
 */
public class RunTest {

    @Test
    public void test1(){
        Provider provider = new EmailFactory();
        Sender email = provider.produce();
        email.send();
    }
}
