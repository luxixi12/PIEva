package Core;

import Base.*;
import Results.FrequentEdgePatternResult;
import Utils.Utils;
import org.apache.log4j.Logger;

import java.util.*;

public class GetInfoFromGraph {
    static Logger logger = Logger.getLogger(GetInfoFromGraph.class.getName());

    /**
     * @param G [G]* @return Results.FrequentEdgePatternResult
     * @author HappyCower
     * @creed: 获取频繁边模式和频繁边模式索引, 为了方便后期快速检索，所有的模式索引都存储了两次（除了A-A这种模式）
     * return:List(pattern), dist{inLabel:{outLabel:[List(inVid),List(outvid)]}}
     * @date 2022/3/18 19:15
     */
    public static FrequentEdgePatternResult getFrquentEdgePattern(Graph G) {
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> labelEdge = getLabelEdge(G);
        ArrayList<Pattern> frequentEdgePattern = new ArrayList<>();
        HashMap<String, Integer> visitInLabel = new HashMap<>();
        ArrayList<String> removeInList = new ArrayList<>();
        ArrayList<String> removeOutList = new ArrayList<>();
        labelEdge.forEach((inLabel, outLabels) -> {
            visitInLabel.put(inLabel, 1);
            outLabels.forEach((outLabel, v) -> {
                if (visitInLabel.containsKey(outLabel) && !outLabel.equals(inLabel)) {
//                if (visitInLabel.containsKey(outLabel)) {
                    return;
                }
//                GlobalVar.globalNum.add(Math.max(v.get(0).size(), v.get(1).size()));
                if (Utils.isFrequent(v)) {
                    // v1.0：inLabel与outLabel相等时，无向图的模式实例需要存储in out两种。
                    // v2.0:将inLabel和outLabel相等时的频繁边忽略
                    if (inLabel.equals(outLabel)) {
//                        ArrayList<Integer> temp = (ArrayList<Integer>) v.get(0).clone();
//                        v.get(0).addAll(v.get(1));
//                        v.get(1).addAll(temp);
                        return;
                    }
                    Pattern pattern = new Pattern(PEdge.class);
                    PNode inNode = new PNode(inLabel, new HashSet<>(v.get(0)));
                    pattern.addVertex(inNode);
                    PNode outNode = new PNode(outLabel, new HashSet<>(v.get(1)));
                    pattern.addVertex(outNode);
                    pattern.addEdge(inNode, outNode, new PEdge(inNode, outNode));
                    //*************[维护TopK]************
//                    TopRankK.topKPatternList.insert(pattern);
                    if(pattern.getMaxItrInForward() >= TopRankK.topKPatternList.getMinimumItr()) {
                        frequentEdgePattern.add(pattern);
                    }else{
                        System.out.println("pattern:" + pattern.toString() + " is not frequent");
                    }
                } else {
                    removeInList.add(inLabel);
                    removeOutList.add(outLabel);
                }
            });
        });

        for (int i = 0; i < removeInList.size(); i++) {
            labelEdge.get(removeInList.get(i)).remove(removeOutList.get(i));
            labelEdge.get(removeOutList.get(i)).remove(removeInList.get(i));
        }

        logger.info("edgePattern sorted!");
        frequentEdgePattern.sort((o1, o2) -> {
//            int m1 = 0;
//            int m2 = 0;
//            for (PNode node : o1.vertexSet()) m1 = Math.max(node.getInstanceIDSet().size(), m1);
//            for (PNode node : o2.vertexSet()) m2 = Math.max(node.getInstanceIDSet().size(), m2);
            int m1 = Integer.MAX_VALUE;
            int m2 = Integer.MAX_VALUE;
            for (PNode node : o1.vertexSet()) m1 = Math.min(node.getInstanceIDSet().size(), m1);
            for (PNode node : o2.vertexSet()) m2 = Math.min(node.getInstanceIDSet().size(), m2);
            return m2 - m1;
        });
        logger.info("one edge pattern size:" + frequentEdgePattern.size());
        return new FrequentEdgePatternResult(frequentEdgePattern, labelEdge);
    }


    public static FrequentEdgePatternResult getAllEdgePattern(Graph G) {
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
                frequentEdgePattern.add(pattern);
            });
        });
        logger.info("getAllEdgePattern and edgePattern sorted!");
        frequentEdgePattern.sort((o1, o2) -> o2.getMNI() - o1.getMNI());
        GlobalInfo.maxMNIInOneEdge = frequentEdgePattern.get(0).getMNI();
        GlobalInfo.minMNIInOneEdge = frequentEdgePattern.get(frequentEdgePattern.size() - 1).getMNI();
        frequentEdgePattern.forEach(pattern -> GlobalInfo.meanMNIInOneEdge +=pattern.getMNI());
        GlobalInfo.meanMNIInOneEdge /= frequentEdgePattern.size();
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
