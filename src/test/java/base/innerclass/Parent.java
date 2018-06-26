package base.innerclass;

/**
 * Created by root on 17-5-3.
 */
abstract class Parent {

    protected void doExecute(){
        System.out.println("parent doExecute!!");
    }

    public Parent() {
        ParentInner a =  new ParentInner();
        RunEnter.cMap.put("parentInner",a);
        RunEnter.cMap.put(this,a);
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
