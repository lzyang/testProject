package base;

/**
 * Created by root on 16-2-16.
 */
public class ObjectSort {

    private class Dog implements Comparable{

        private int id;
        private String nickName;

        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }

}
