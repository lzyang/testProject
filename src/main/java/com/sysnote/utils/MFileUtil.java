package com.sysnote.utils;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by root on 15-2-6.
 */
public class MFileUtil {
    /**
     * 写文件
     * @param filepath
     * @param content
     * @param append
     */
    public static void writeFile(String filepath,String content,boolean append){
        FileWriter writer = null;
        try{
            writer = new FileWriter(filepath, append);
            writer.write(content);
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(writer!=null)
                    writer.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
