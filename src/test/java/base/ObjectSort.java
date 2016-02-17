package base;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 16-2-16.
 */
public class ObjectSort {

    /*
    =============================================方法1==============================================
     */

    private class Dog implements Comparable {

        private int id;
        private String nickName;

        public Dog(int id, String nickName) {
            this.id = id;
            this.nickName = nickName;
        }

     /*
     * 这里表示按id从小到大排序，如果该对象小于、等于或大于指定对象Object o，则分别返回负整数、零或正整数
     * 如果需要从大到小排序，则如果该对象小于、等于或大于指定对象Object o，则分别返回正整数、零或负整数
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
        @Override
        public int compareTo(Object o) {
            Dog dog = (Dog)o;
            if(id<dog.id){
                return -1;
            }else if(id>dog.id){
                return 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "Dog{" +
                    "id=" + id +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }
    }

    @Test
    public void testComparable(){
        List DogList = new ArrayList();
        DogList.add(new Dog(2, "张三"));
        DogList.add(new Dog(1, "李四"));
        DogList.add(new Dog(0, "王五"));
        DogList.add(new Dog(7, "赵六"));
        System.out.println("排序前");
        for (int i = 0; i < DogList.size(); i++) {
            Dog dog = (Dog) DogList.get(i);
            System.out.println(dog.id + " " + dog.nickName);
        }
        Collections.sort(DogList);
        System.out.println("排序后");
        for (int i = 0; i < DogList.size(); i++) {
            Dog dog = (Dog) DogList.get(i);
            System.out.println(dog.id + " " + dog.nickName);
        }
    }


    /*
     *================================================方法2========================================================
     */

    public class CompareCat implements Comparator<Cat>{

 /*
 * 这里表示按id从小到大排序，如果该对象o1小于、等于或大于指定对象o2，则分别返回负整数、零或正整数
 * 如果需要从大到小排序，则如果对象o1小于、等于或大于指定对象o2，则分别返回正整数、零或负整数
 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
 */
        @Override
        public int compare(Cat o1, Cat o2) {
            if(o1.id<o2.id){
                return -1;
            }else if(o1.id>o2.id){
                return 1;
            }
            return 0;
        }
    }

    private class Cat{
        private int id;
        private String nickName;

        public Cat(int id, String nickName) {
            this.id = id;
            this.nickName = nickName;
        }
    }

    @Test
    public void testComparator(){
        List<Cat> catList = new ArrayList<Cat>();
        Cat s1 = new Cat(1, "a");
        Cat s4 = new Cat(5, "d");
        Cat s2 = new Cat(2, "b");
        Cat s3 = new Cat(3, "c");

        catList.add(s3);
        catList.add(s2);
        catList.add(s1);
        catList.add(s4);

        System.out.println("排序前");
        for (int i = 0; i < catList.size(); i++) {
            Cat s = catList.get(i);
            System.out.println(s.id + " " + s.nickName);
        }
        Collections.sort(catList, new CompareCat());
        System.out.println("排序后");
        for (int i = 0; i < catList.size(); i++) {
            Cat s = catList.get(i);
            System.out.println(s.id + " " + s.nickName);
        }
    }
}
