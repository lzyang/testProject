package base;

import org.junit.Test;

public class AssertTest {

    /**
     * 在Java中，assert关键字是从JAVA SE 1.4 引入的，为了避免和老版本的Java代码中使用了assert关键字导致错误，Java在执行的时候默认是不启动断言检查的（这个时候，所有的断言语句都 将忽略！），如果要开启断言检查，则需要用开关-enableassertions或-ea来开启。
     *
     * assert关键字语法很简单，有两种用法：
     *
     * 1、assert <boolean表达式>
     * 如果<boolean表达式>为true，则程序继续执行。
     * 如果为false，则程序抛出AssertionError，并终止执行。
     *
     * 2、assert <boolean表达式> : <错误信息表达式>
     * 如果<boolean表达式>为true，则程序继续执行。
     * 如果为false，则程序抛出java.lang.AssertionError，并输入<错误信息表达式>。
     */

    @Test
    public void testAssert(){
        //断言1结果为true，则继续往下执行
        assert true;
        System.out.println("断言1没有问题！");

        System.out.println("\n-----------------\n");

        //断言2结果为false,程序终止
        assert false : "断言失败，此表达式的信息将会在抛出异常的时候输出！";
        System.out.println("断言2没有问题！");
    }
}
