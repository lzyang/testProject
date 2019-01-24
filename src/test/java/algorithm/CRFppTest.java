package algorithm;

import com.mongodb.BasicDBList;
import com.sysnote.utils.StringUtil;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;

import java.io.*;
import java.util.List;

public class CRFppTest {
      @Test
      public void CRFppData() {
            String inputFilePath = "/Users/morningsun/data/source/莽荒纪_我吃西红柿_精校.txt";
            String outputFilePath = "/Users/morningsun/data/crf_pp/mhj_crf_bigram.txt";

            try {
                  OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFilePath, false), "UTF-8");
                  BufferedWriter wr = new BufferedWriter(writer);
                  InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFilePath), "UTF-8");
                  BufferedReader br = new BufferedReader(reader);

                  String line = "";
                  long lineCount = 0;
                  StopRecognition filter = new StopRecognition();
//                  filter.insertStopNatures("u", "v", "r", "d","p", "null", "e", "c", "a", "nt", "u", "ad", "q", "mq", "b", "y", "m", "f", "s", "j", "t", "z", "ng");//去除助词和动词
                  filter.insertStopNatures("u", "v", "r", "d","p", "null");
                  BasicDBList doc = new BasicDBList();
                  //标注含义：B:开始，E:结束，M/I:中间，S:单
                  String b = "\tB";
                  String m = "\tM";
                  String e = "\tE";
                  String s = "\tS";
                  while ((line = br.readLine()) != null) {
                        List<Term> parse = ToAnalysis.parse(StringUtil.normalizeString(line)).recognition(filter).getTerms();
                        for (int i = 0; i < parse.size(); i++) {
                              Term t = parse.get(i);
                              char[] words = t.getName().toCharArray();
                              if(words.length>1){
                                    for(int j=0;j<words.length;j++){
                                          if(j==0){
                                                wr.write(words[j] + b + "\r\n");
                                          }else if(j==words.length-1){
                                                wr.write(words[j] + e + "\r\n");
                                          }else {
                                                wr.write(words[j] + m + "\r\n");
                                          }
                                    }
                              }else{
                                    wr.write(words[0] + s + "\r\n");
                              }
                        }
                        System.out.println("line\t" + ++lineCount);
//                        if(lineCount>10) break;
                  }
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
}
