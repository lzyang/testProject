package base;

import org.junit.Test;

import java.util.*;

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

    //========================================================================

    private class Category implements Comparable<Category>{

        private String catId = "";
        private String catName = "";
        private int prodCount = 0;

        public Category(String catId, String catName) {
            this.catId = catId;
            this.catName = catName;
        }

        public Category(String catId, String catName, int prodCount) {
            this.catId = catId;
            this.catName = catName;
            this.prodCount = prodCount;
        }

        @Override
        public String toString() {
            return catId + '\t' + catName + '\t' + prodCount;
        }

        @Override
        public int compareTo(Category o) {
            if(this.prodCount-o.prodCount>0){
                return -1;
            }else if(this.prodCount - o.prodCount<0){
                return 1;
            }else {
                return 0;
            }
        }

        @Override
        public int hashCode() {
            String key = catId + catName;
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            Category catB = (Category)obj;
            if(this.catName.equals(catB.catName)&&this.catId.equals(catB.catId)){
                return true;
            }else {
                return false;
            }
        }
    }

    @Test
    public void testTreeSet(){
        Set<Category> cats = new TreeSet<Category>();
        cats.add(new Category("cat10000070","手机",3));
        cats.add(new Category("cat10000072","电视",32));
        cats.add(new Category("cat10000073","电脑",2));
        cats.add(new Category("cat10000071","挂烫机",23));
        cats.add(new Category("cat10000074","茶叶",5));
        cats.add(new Category("cat10000075","杯子",7));
        System.out.println(cats);

        for(Category cat :cats){
            System.out.println(cat.toString());
        }

        System.out.println(new Category("cat2002021","32").equals(new Category("cat2002021","32")));
    }
}
