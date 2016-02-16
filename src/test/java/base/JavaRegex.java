package base;

import org.junit.Test;

import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Morningsun(515190653@qq.com) on 15-11-5.
 */
public class JavaRegex {

    /**
     * java.util.regex 包主要由三个类所组成：Pattern、Matcher 和 PatternSyntaxException。

     Pattern 对象表示一个已编译的正则表达式。Pattern 类没有提供公共的构造方法。要构建一个模式，首先必须调用公共的静态 compile 方法，它将返回一个 Pattern 对象。这个方法接受正则表达式作为第一个参数。本教程的开始部分将教你必需的语法。
     Matcher 是一个靠着输入的字符串来解析这个模式和完成匹配操作的对象。与 Pattern 相似，Matcher 也没有定义公共的构造方法，需要通过调用 Pattern 对象的 matcher 方法来获得一个 Matcher 对象。
     PatternSyntaxException 对象是一个未检查异常，指示了正则表达式中的一个语法错误。

     */

    @Test
    public void simple(){
//        String regex = "d";
        String regex = "(旗舰店|官方|授权)";
        String source = "半岛铁盒汽车用品旗舰店";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        System.out.println(matcher.find());
        System.out.println(matcher.group());
        System.out.println(matcher.replaceAll(""));
    }

    @Test
    public void test1(){
        //String str = "商品-分类cat10  00001分,类\\以cat开头_并不是1.";
        String str = "4g-iphone,手机";

        StringBuilder buder = new StringBuilder();
        String regEx = "[A-Za-z0-9 \\u4e00-\\u9fa5]";  //过滤除汉字/字母/数字/空格以外的字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            buder.append(m.group(0));
        }
        System.out.println(buder.toString());
    }
}
