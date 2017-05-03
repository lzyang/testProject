package base.innerclass;

/**
 * Created by root on 17-5-3.
 */
abstract class Parent {

    protected abstract void doExecute();

    public Parent() {
        RunEnter.cMap.put("parentInner",new ParentInner());
    }

    class ParentInner{

        public void executor(){
            System.out.println("parentInner executor!!");
        }

        public void messageReceive(){
            System.out.println("parentInner messageReceive()");
            doExecute();
        }
    }
}
