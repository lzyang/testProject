package base.innerclass;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 17-5-3.
 */
public class RunEnter {

    public static Map cMap = new HashMap();

    public static void main(String[] args) {
        Children c = new Children();

        Parent.ParentInner pi = (Parent.ParentInner)cMap.get("parentInner");
        pi.executor();
        pi.messageReceive();
    }
}
