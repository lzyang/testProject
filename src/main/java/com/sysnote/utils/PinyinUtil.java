package com.sysnote.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinyinUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PinyinUtil.class);
    private static final char [] ChineseNum ={'零','壹','贰','叁','肆','伍','陆','柒','捌','玖'};
    private static HashMap<String,String> DYDICT = new HashMap<String, String>(){{
        put("长","chang");
    }};

    public static String[] toSpell(String chines) {

        //数字开头品牌名称特殊处理
        if(isSpecialBrand(chines)){
            chines = "十";
        }else if(isNumBrandName(chines)) {
            chines = arabNumToChineseRMB(chines.substring(0, 1));
        }

        StringBuilder pyShort = new StringBuilder();
        StringBuilder pyLong = new StringBuilder();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strs == null)
                        continue;
                    String dy = DYDICT.get(nameChar[i]+"");
                    if(dy!=null){
                        strs[0] = dy;
                    }
                    pyShort.append(strs[0].charAt(0));
                    pyLong.append(strs[0].replaceAll(":", ""));
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                }
            } else {
                pyShort.append(nameChar[i]);
                pyLong.append(nameChar[i]);
            }
        }
        return new String[] { pyShort.toString(), pyLong.toString() };
    }

    /**
     * 返回关于钱的中文式大写数字,支仅持到亿
     * @throws Exception
     * */
    public static String arabNumToChineseRMB(String moneyNum){
        String res="";
        int i=3;
        int len=moneyNum.length();
        if(len>12){
            LOG.error("Number too large!");
        }
        if("0".equals(moneyNum))
            return "零";
        for(len--;len>=0;len--){
            int num=Integer.parseInt(moneyNum.charAt(len)+"");
            res=ChineseNum[num]+res;
        }
        return res;

    }

    /**
     * 判断品牌名称以1开头并且前两位是数字
     * @param brandName
     * @return
     */
    public static boolean isSpecialBrand(String brandName){
        String regEx="^[0-9][0-9][^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(brandName);

        return m.find() && brandName.startsWith("1");
    }

    /**
     * 判断品牌名称以数字开头除１开头之外
     * @param brandName
     * @return
     */
    public static boolean isNumBrandName(String brandName){
        String regEx="^[0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(brandName);
        return m.find();
    }

    public static void main(String[] args) {
        System.out.println(toSpell("长虹")[0] + "\t" + toSpell("长虹")[1]);
    }

}
