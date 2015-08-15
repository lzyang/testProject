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
        maxBitCount = storeCellCount*31;
        clear();
    }

    public void clear(){
        for (int i = 0; i < bitMap.size(); i++)
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
        int cell = bitMap.elementAt(cellPos);
        if(bit == true){
            cell |= 1<<PosInCell;
        }else{
            cell &= (1<<PosInCell);  //TODO
        }
    }

    /**
     * 获取bit位
     */
    public void getBit(){

    }
}

