package Results;

import Base.Pattern;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author HappyCower
 * @creed: class 封装frquentPattern返回的结果：
 *          return:List(pattern), dist{inLabel:{outLabel:[set(inVid),set(outvid)]}}
 * @date 2022/3/19 13:37
 */

public class FrequentEdgePatternResult {
    ArrayList<Pattern> frequentEdgePattern;
    HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex;
    HashMap<String, Integer> label;


    public FrequentEdgePatternResult(ArrayList<Pattern> frequentEdgePattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex, HashMap<String, Integer> label) {
        this.frequentEdgePattern = frequentEdgePattern;
        this.frequentEdgePatternIndex = frequentEdgePatternIndex;
        this.label = label;
    }

    public FrequentEdgePatternResult(ArrayList<Pattern> frequentEdgePattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        this.frequentEdgePattern = frequentEdgePattern;
        this.frequentEdgePatternIndex = frequentEdgePatternIndex;
        this.label = new HashMap<>();
    }


    public void setFrequentEdgePattersn(ArrayList<Pattern> frequentEdgePattern) {
        this.frequentEdgePattern = frequentEdgePattern;
    }

    public void setFrequentEdgePatternIndex(HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        this.frequentEdgePatternIndex = frequentEdgePatternIndex;
    }

    public ArrayList<Pattern> getFrequentEdgePattern() {
        return frequentEdgePattern;
    }

    public HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> getFrequentEdgePatternIndex() {
        return frequentEdgePatternIndex;
    }

    public HashMap<String, Integer> getLabel() {
        return label;
    }

    public void setLabel(HashMap<String, Integer> label) {
        this.label = label;
    }
}
