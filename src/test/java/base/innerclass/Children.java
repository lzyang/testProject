package base.innerclass;

/**
 * Created by root on 17-5-3.
 */
public class Children extends Parent{

    public void executor(){
        System.out.println("Children executor!!");
    }

    @Override
    protected void doExecute(){
        System.out.println("Children doExecute!");
    }
}
