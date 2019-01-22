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
        String inputFilePath = "/mdata/code/gitworkspace/pythonLearn/python/data/1.基地三部曲1：基地.txt";
        String outputFilePath = "/mdata/code/gitworkspace/pythonLearn/python/data/jidi.txt";

        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFilePath),"UTF-8");
            BufferedWriter wr = new BufferedWriter(writer);

            InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFilePath),"UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            long lineCount = 0;

            StopRecognition filter = new StopRecognition();
            filter.insertStopNatures("u","v","r","d","p","null");//去除助词和动词
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
        String str = "容声(Ronshen) BCD-202M/TX6-GF61-C202升L三门冰箱(银色)健康节能";
        List<Term> parse = ToAnalysis.parse(str).getTerms();
        for(int i = 0;i<parse.size();i++){
            Term t = parse.get(i);
            System.out.print(t.getName() + "\n");
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
