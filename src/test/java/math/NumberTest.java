package math;

import org.junit.Test;

import java.math.BigInteger;

public class NumberTest {

    /**
     * 十进制转换二进制
     */
    @Test
    public void decimalToBinary(){
        int decimal = 123;
        BigInteger bi = new BigInteger(String.valueOf(decimal));  //转化为bigInt类型，默认为十进制
        System.out.printf(bi.toString(2));   //参数2表示进制
    }

    /**
     * 二进制转十进制
     */
    @Test
    public void binaryToDecimal(){
        String binary = "1111011";
        BigInteger bi = new BigInteger(binary,2);
        System.out.println(bi.toString());
    }


    @Test
    public void testHash(){
        System.out.println("0".hashCode());
    }
}
