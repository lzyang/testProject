package datastructure.bitmap;

import org.junit.Test;

import java.util.Vector;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-13.
 */

public class BitMap {

    private int storeCellCount = 0;
    private int maxBitCount = 0;
    private int setBitCount = 0;
    private Vector<Integer> bitMap = null;

    BitMap(int storeSize) {
        storeCellCount = storeSize / 31 + 1;
        bitMap = new Vector<Integer>(storeCellCount);
        bitMap.setSize(storeCellCount);
        maxBitCount = storeCellCount*31;
        clear();

    }

    public void clear(){
        for (int i = 0; i < storeCellCount; i++)
            bitMap.set(i, 0);
    }

    /**
     * 获取设置存储的预定bit位数
     * @return
     */
    int getSetBitCount(){
        return setBitCount;
    }

    /**
     * 获取bitmap允许存储的最大位数
     * @return
     */
    int getMaxBitCount(){
        return maxBitCount;
    }

    /**
     * 设置bit位
     * @param bitPosition
     * @param bit true表示1,false表示0
     */
    public void setBit(int bitPosition,boolean bit){
        if(bitPosition<1||bitPosition>maxBitCount){
            try {
                throw new Exception("parameter must be positive and can't pass MaxBitCount!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        int cellPos = bitPosition/31;
        int PosInCell = bitPosition%31;
        //System.out.println("cellPos:"+cellPos + " PosInCell"+PosInCell + "  bitSize:"+bitMap.size());
        int cell = bitMap.elementAt(cellPos);
        if(bit == true){
            cell |= 1<<PosInCell;
        }else{
            cell &= (1<<PosInCell)^Integer.MAX_VALUE;
        }
        bitMap.set(cellPos,cell);
    }

    /**
     * 获取bit位
     */
    public boolean getBit(int pos){
        if(pos<1||pos>maxBitCount){
            try {
                throw new Exception("parameter must be positive and can't pass MaxBitCount!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        int cellPos = pos/31;
        int PosInCell = pos%31;
        int cell = bitMap.elementAt(cellPos);
        if((cell&(1<<PosInCell))>0){
            return true;
        }else{
            return false;
        }
    }

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

    public static void main(String[] args){
        BitMap bm = new BitMap(3880);
        bm.setBit(3880,true);
        bm.setBit(9,true);
        bm.setBit(8,true);
        bm.setBit(7,true);
        bm.setBit(6,true);
        bm.setBit(1,true);
        System.out.println("3880>"+bm.getBit(3880));
        System.out.println("9>"+bm.getBit(9));
        System.out.println("8>"+bm.getBit(8));
        System.out.println("7>"+bm.getBit(7));
        System.out.println("6>"+bm.getBit(6));
        System.out.println("5>"+bm.getBit(5));
        System.out.println("1>"+bm.getBit(1));

        System.out.println("============================");
        bm.setBit(1,false);
        bm.setBit(7, false);
        bm.setBit(3880,false);
        System.out.println("3880>" + bm.getBit(3880));
        System.out.println("9>" + bm.getBit(9));
        System.out.println("8>"+bm.getBit(8));
        System.out.println("7>"+bm.getBit(7));
        System.out.println("6>"+bm.getBit(6));
        System.out.println("5>"+bm.getBit(5));
        System.out.println("1>"+bm.getBit(1));
    }
}

