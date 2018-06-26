package ansj_seg;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
