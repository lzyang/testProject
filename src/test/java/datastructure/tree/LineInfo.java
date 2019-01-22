package datastructure.tree;

/**
 * Created by root on 17-6-14.
 */
public class LineInfo implements Comparable<LineInfo> {

    public String line = null;
    public int weight = 0;


    public LineInfo(String line, int weight) {
        this.line = line;
        this.weight = weight;
    }

    @Override
    public int compareTo(LineInfo o) {
        if (this.weight < o.weight) {
            return 1;
        }
        if (this.weight == o.weight) {
            if (this.line.equals(o.line)) {
                return 0;
            }
            return 1;
        }
        return -1;
    }
}
