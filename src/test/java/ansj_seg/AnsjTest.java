package ansj_seg;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.sysnote.utils.MFileUtil;
import com.sysnote.utils.StringUtil;
import org.ansj.domain.Term;
import org.ansj.library.StopLibrary;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;
import org.nlpcn.commons.lang.tire.library.Library;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 15-1-29.
 */

public class AnsjTest {

    @Test
    public void base(){
        String str = "索尼（SONY）Xperia Z1 L39u 4G手机（黑色）联通版";
        List<Term> parse = BaseAnalysis.parse(str).getTerms();
        for(int i = 0;i<parse.size();i++){
            Term t = parse.get(i);
            System.out.println(t.getName() + " " + t.getNatureStr());
        }

        //System.out.println(parse);
    }

    @Test
    public void algorithmLearn(){
//        String inputFilePath = "/Users/morningsun/data/source/1.基地三部曲1：基地.txt";
//        String inputFilePath = "/Users/morningsun/data/source/2.基地三部曲2：基地与帝国.txt";
//        String inputFilePath = "/Users/morningsun/data/source/3.基地三部曲3：第二基地.txt";
//        String inputFilePath = "/Users/morningsun/data/source/4.基地后传1·基地边缘.txt";
//        String inputFilePath = "/Users/morningsun/data/source/5.基地后传2·基地与地球.txt";
//        String inputFilePath = "/Users/morningsun/data/source/6.基地前传1·基地前奏.txt";
//        String inputFilePath = "/Users/morningsun/data/source/7.基地前传2·迈向基地.txt";
        String inputFilePath = "/Users/morningsun/data/source/tianlongbabu_jinyong.txt";
        String outputFilePath = "/Users/morningsun/data/word2vec/tlbb.txt";

        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFilePath,false),"UTF-8");
            BufferedWriter wr = new BufferedWriter(writer);

            InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFilePath),"UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            long lineCount = 0;

            StopRecognition filter = new StopRecognition();
            filter.insertStopNatures("u","v","r","d",
                      "p","null","e","c","a",
                      "nt","u","ad","q","mq","b"
                      ,"y","m","f","s","j","t","z","ng");//去除助词和动词
            BasicDBList doc = new BasicDBList();
            while ((line = br.readLine()) != null) {
                List<Term> parse = ToAnalysis.parse(StringUtil.normalizeString(line)).recognition(filter).getTerms();
//                BasicDBList sentences = new BasicDBList();
                for(int i = 0;i<parse.size();i++){
                    Term t = parse.get(i);
//                    sentences.add(t.getName());
                    wr.write(t.getName() + " ");
                }
//                doc.add(sentences);

//                if(++lineCount>10) break;
            }
//            wr.write(doc.toString());
            br.close();
            reader.close();

            wr.close();
            writer.close();
            System.out.println(outputFilePath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void toAnsj(){
        String str = "府君 牛 头 鬼神 召 纪宁 书房 书房 墙面 唯一 一幅 纪宁 一幅 女子 图 韵律 面带微笑 地球 寺庙 大佛 人 膜拜 心 纪宁 一幅 一幅 女子 面容 长发 魔力 中年人 眼 墙壁 \n" +
                  "女子 图 悟性 中年人 一声 喝 纪宁 感觉 思想 境界 崔府君 崔府君 书籍 纪宁 面色 角度 崔府君 书籍 封面 三个字 生死 崔府君 生死 一生 崔府君 纪宁 纪宁 一生 一幕 场\n" +
                  "景 父亲 一家 生物 研究所 项目 带头人 薪资 母亲 教师 家庭 患上 绝症 医生 十五六岁 奇迹 同龄人 小时 都会 身体 病痛 折磨 孩童 时期 孤僻 医院 恐惧感 折磨 孤僻 书\n" +
                  "籍 网络 书籍 网络 精神 世界 才让 性格 书籍 网络 知识 世界 眼界 内心 理性 世界 世界 孩子 父母 价值 总 活 一世 父亲 十万元 网络 生活 色彩 成就 时间 巨额 财富 绝症 活 太久 父母 钱 金钱 全国 病 孩子 们 命运 病 孩子 们 命运 纪宁 心底 呐喊 所有 金钱 一次 父母 医院 街道 苦难 崔府君 苦难 力量 巨富 巨富 十八岁 崔府君 舍己救人 陌生人 纪宁 誉 寿命 医生 活 寿命 女孩 寿命 值 崔府君 生死 蕴含着 无尽 纪 一生 万人 功德 六道 人道 人道 纪宁 崔府君 大功 人道 地球 一步 巨富 孩童 功德 天界 意思";
        List<Term> parse = ToAnalysis.parse(str).getTerms();
        for(int i = 0;i<parse.size();i++){
            Term t = parse.get(i);
            System.out.print(t.getName() + t.getNatureStr() + "\n");
        }
        //System.out.println(parse);
    }

    @Test
    public void indexAnsj(){
        String str = "容声(Ronshen) BCD-202M/TX6-GF61-C202升L三门冰箱(银色)健康节能";
        List<Term> parse = IndexAnalysis.parse(str).getTerms();
        for(int i = 0;i<parse.size();i++){
            Term t = parse.get(i);
            System.out.print(t.getName() + " ");
        }
//        System.out.println(parse);
    }

    @Test
    public void haiGou(){

        FileReader fr = null;
        try {
            fr = new FileReader("/mdata/sourcedata/haigou/shortJson");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        try {
            int num = 0;
            while ((line = br.readLine())!=null){
                if(++num == 10) break;
                BasicDBObject o = (BasicDBObject)JSON.parse(line);
                String str = o.getString("prodName","");
                System.out.println("\n\n\n"+str);
                List<Term> parse = BaseAnalysis.parse(str).getTerms();
                for(int i = 0;i<parse.size();i++){
                    Term t = parse.get(i);
                    System.out.print(t.getName() + ":" + t.getNatureStr() + "\t");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
