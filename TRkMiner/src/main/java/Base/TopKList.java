package Base;

import Utils.GlobalVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TopKList {

    private SkipList topKItr;

    private int nums;

//    private HashMap<Integer, ArrayList<Pattern>> topKPatternList;
    private HashMap<Float, ArrayList<Pattern>> topKPatternList;


    public TopKList() {
        this.topKItr = new SkipList();
        this.topKPatternList = new HashMap<>();
        this.nums = 0;
    }

    public void insert(Pattern pattern) {
//        int itr = pattern.getItr();
        float itr = pattern.getItr();
//        System.out.println("itr: " + itr);
        if (topKPatternList.containsKey(itr)) {
            topKPatternList.get(itr).add(pattern);
            return;
        }

        if (nums >= GlobalVar.K) {
            if(itr < topKItr.getMinimum()) {return;}
            topKPatternList.remove(topKItr.getMinimum());
            topKItr.delete(topKItr.getMinimum());
            nums--;
        }
        topKItr.insert((float) itr);
        topKPatternList.put(itr, new ArrayList<>(List.of(pattern)));
        nums++;
    }

    public float getMinimumItr() {
        if (nums == GlobalVar.K) {
            return topKItr.getMinimum();
        }
        return 0;
    }

    public int getNums() {
        return nums;
    }

    public SkipList getTopKItrList() {
        return topKItr;
    }

    public HashMap<Float, ArrayList<Pattern>> getTopKPatternList() {
        return topKPatternList;
    }

}
