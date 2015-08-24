package designpatterns.abstractfactory;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-24.
 */
public class MobileMsgFactory implements Provider{
    @Override
    public Sender produce() {
        return new MobileMsg();
    }
}
