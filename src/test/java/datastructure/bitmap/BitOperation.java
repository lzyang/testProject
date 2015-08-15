package datastructure.bitmap;

import org.junit.Test;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-14.
 */
public class BitOperation {

    /**
     * 清零操作
     */
    @Test
    public void clear(){
        int a = 852;
        //对数字a和十六进制0去且操作
        int b = a&0x0;
        System.out.println(b);
        printBinary(65535);
    }

    @Test
    public void testShort(){
        char a = 12323;
//        short b = a&0x128;  //char和short在java中进行位运算会自动转化为int再进行运算
        System.out.println(a);

    }

    /**
     *打印正数二进制
     * @param x
     */
    public void printBinary(int x){
        StringBuffer binaryStr = new StringBuffer("");
        for(int i = 0;i<31;i++){
            if((x & (1 << i))>0){
                binaryStr.append(1);
            }else{
                binaryStr.append(0);
            }
        }
        System.out.println(binaryStr);
    }

    public void printBinary(long x){
        StringBuffer binaryStr = new StringBuffer("");
        for(int i = 0;i<63;i++){
            if((x & (1 << i))>0){
                binaryStr.append(1);
            }else{
                binaryStr.append(0);
            }
        }
        System.out.println(binaryStr);
    }

    public void printBinary(short x){
        StringBuffer binaryStr = new StringBuffer("");
        for(int i = 0;i<15;i++){
            if((x & (1 << i))>0){
                binaryStr.append(1);
            }else{
                binaryStr.append(0);
            }
        }
        System.out.println(binaryStr);
    }

    public void printBinary(char x){
        StringBuffer binaryStr = new StringBuffer("");
        for(int i = 0;i<15;i++){
            if((x & (1 << i))>0){
                binaryStr.append(1);
            }else{
                binaryStr.append(0);
            }
        }
        System.out.println(binaryStr);
    }

    public void printBinary(byte x){
        StringBuffer binaryStr = new StringBuffer("");
        for(int i = 0;i<7;i++){
            if((x & (1 << i))>0){
                binaryStr.append(1);
            }else{
                binaryStr.append(0);
            }
        }
        System.out.println(binaryStr);
    }

}
