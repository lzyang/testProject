package com.sysnote.utils;

import java.io.*;

/**
 * Created by root on 15-2-6.
 */
public class MFileUtil {

    public static final String[] TXT_COMMON_DELSTR = {"\r", "\n", "\t"};
    public static final String[] TXT_COMMON_SPACING_DELSTR = {"\r", "\n", "\t", " "};
    public static final String CHARSET_GBK = "GBK";
    public static final String CHARSET_UTF_8 = "UTF-8";

    /**
     * 写文件
     *
     * @param filepath
     * @param content
     * @param append
     */
    public static void writeFile(String filepath, String content, boolean append) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filepath, append);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读文件
     *
     * @param filePath
     * @return
     */
    public static String readTxt(String filePath, String[] delStrs,String charset) {
        String content = "";
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(filePath), charset);
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            while ((line = br.readLine()) != null) {
                for (String delStr : delStrs) {
                    line = line.replace(delStr, "");
                }
                content = content + line;
            }
            br.close();
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void checkDir(String dirname) {
        try {
            File f = new File(dirname);
            if (!f.exists()) {
                f.mkdir();
            }
        } catch (Exception e) {
        }
    }
}
