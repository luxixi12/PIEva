package Core.Topk_Sampling;

import Base.*;
import Results.FrequentEdgePatternResult;
import Utils.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GetInfoFromGraph {
    static Logger logger = Logger.getLogger(GetInfoFromGraph.class.getName());

    /**
     * @param G [G]* @return Results.FrequentEdgePatternResult
     * @author HappyCower
     * @creed: 获取频繁边模式和频繁边模式索引, 为了方便后期快速检索，所有的模式索引都存储了两次（除了A-A这种模式）
     * return:List(pattern), dist{inLabel:{outLabel:[List(inVid),List(outvid)]}}
     * @date 2022/3/18 19:15
     */


    public static FrequentEdgePatternResult getEdgePattern(Graph G) {
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> labelEdge = getLabelEdge(G);
        ArrayList<Pattern> frequentEdgePattern = new ArrayList<>();
        HashMap<String, Integer> visitInLabel = new HashMap<>();
        HashMap<String, Integer> label = new HashMap<>();
        labelEdge.forEach((inLabel, outLabels) -> {
            visitInLabel.put(inLabel, 1);
            outLabels.forEach((outLabel, v) -> {
                if (visitInLabel.containsKey(outLabel) && !outLabel.equals(inLabel)) {
                    return;
                }
                if(!label.containsKey(inLabel)) label.put(inLabel, 0);
                if(!label.containsKey(outLabel)) label.put(outLabel, 0);
                Pattern pattern = new Pattern(PEdge.class);
                PNode inNode = new PNode(inLabel, new HashSet<>(v.get(0)));
                pattern.addVertex(inNode);
                PNode outNode = new PNode(outLabel, new HashSet<>(v.get(1)));
                pattern.addVertex(outNode);
                pattern.addEdge(inNode, outNode, new PEdge(inNode, outNode));

                //开始维护topK
//                TopRankK.topKPatternList.insert(pattern);
                //TODO
//                if(Utils.isAllPatternNodeInCoreNode(pattern)){
//                    TopRankK.topKPatternList.insert(pattern);
//                    TopRankK.priorityQueue.add(pattern);
//                    frequentEdgePattern.add(pattern);
//                }

                frequentEdgePattern.add(pattern);

            });
        });
        frequentEdgePattern.sort((o1, o2) -> o2.getMNI() - o1.getMNI());
        if(frequentEdgePattern.size()!=0) {
            GlobalInfo.maxMNIInOneEdge = frequentEdgePattern.get(0).getMNI();
            GlobalInfo.minMNIInOneEdge = frequentEdgePattern.get(frequentEdgePattern.size() - 1).getMNI();
            frequentEdgePattern.forEach(pattern -> GlobalInfo.meanMNIInOneEdge += pattern.getMNI());
            GlobalInfo.meanMNIInOneEdge /= frequentEdgePattern.size();
        }
        return new FrequentEdgePatternResult(frequentEdgePattern, labelEdge,label);
    }


    /**
     * @param G [G]* @return java.util.HashMap<java.lang.String,java.util.HashMap<java.lang.String,java.util.ArrayList<java.util.HashSet<java.lang.Integer>>>>
     * @author HappyCower
     * @creed: 获取每个单边模式
     * return:{inLabel:{outLabel:[set(inVids),set(outVids)]}}
     * @date 2022/3/18 19:14
     */
    private static HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> getLabelEdge(Graph G) {
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> labelEdge = new HashMap<>();
        for (Edge edge : G.edgeSet()) {
            String inLabel = edge.getInNode().getLabel();
            int inId = edge.getInNode().getId();
            String outLabel = edge.getOutNode().getLabel();
            int outId = edge.getOutNode().getId();

            // inLabel和outLabel不相等就需要相互建立联系
            if (!inLabel.equals(outLabel)) {
                built(labelEdge, inLabel, inId, outLabel, outId);
                built(labelEdge, outLabel, outId, inLabel, inId);
            }
        }
        return labelEdge;
    }

    /**
     * @param labelEdge HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>>
     * @param inLabel   String
     * @param inId      Int
     * @param outLabel  String
     * @param outId     Int
     *                  [labelEdge, inLabel, inId, outLabel, outId]* @return void
     * @author HappyCower
     * @creed: 用于建立inLabel和outLabel的HashMap联系
     * @date 2022/3/24 14:42
     */
    private static void built(HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> labelEdge, String inLabel, int inId, String outLabel, int outId) {
        if (labelEdge.containsKey(inLabel)) {
            if (labelEdge.get(inLabel).containsKey(outLabel)) {
                labelEdge.get(inLabel).get(outLabel).get(0).add(inId);
                labelEdge.get(inLabel).get(outLabel).get(1).add(outId);
            } else {
                labelEdge.get(inLabel).put(outLabel, newArrInArr(inId, outId));
            }
        } else {
            HashMap<String, ArrayList<ArrayList<Integer>>> hm = new HashMap<>();
            hm.put(outLabel, newArrInArr(inId, outId));
            labelEdge.put(inLabel, hm);
        }
    }


    /**
     * @param inId  int
     * @param outId int
     *              [inId, outId]* @return java.util.ArrayList<java.util.ArrayList<java.lang.Integer>>
     * @author HappyCower
     * @creed: 用于新建双重ArrayList
     * @date 2022/3/24 14:45
     */
    private static ArrayList<ArrayList<Integer>> newArrInArr(int inId, int outId) {
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        list.add(new ArrayList<>());
        list.add(new ArrayList<>());
        list.get(0).add(inId);
        list.get(1).add(outId);
        return list;
    }
}
