package ansj_seg;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;

import java.util.List;

/**
 * Created by root on 15-1-29.
 */

public class AnsjTest {

    @Test
    public void base(){
        String str = "索尼（SONY）Xperia Z1 L39u 4G手机（黑色）联通版";
        List<Term> parse = BaseAnalysis.parse(str);
        for(int i = 0;i<parse.size();i++){
            Term t = parse.get(i);
            System.out.print(t.getName() + " ");
        }

        //System.out.println(parse);
    }

    @Test
    public void toAnsj(){
        String str = "索尼（SONY）Xperia Z1 L39u 4G手机（黑色）联通版";
        List<Term> parse = ToAnalysis.parse(str);
        for(int i = 0;i<parse.size();i++){
            Term t = parse.get(i);
            System.out.print(t.getName() + " ");
        }
        //System.out.println(parse);
    }

    @Test
    public void indexAnsj(){
        String str = "容声(Ronshen) BCD-202M/TX6-GF61-C202升L三门冰箱(银色)健康节能";
        List<Term> parse = IndexAnalysis.parse(str);
        for(int i = 0;i<parse.size();i++){
            Term t = parse.get(i);
            //System.out.print(t.getName() + " ");
        }
        System.out.println(parse);
    }
}
