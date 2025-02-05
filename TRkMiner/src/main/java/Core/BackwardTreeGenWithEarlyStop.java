package Core;

import Base.*;
import Results.FrequentEdgePatternResult;
import Results.NewPatternWithTwoNewPNodeResult;
import Utils.Utils;
import Utils.GlobalVar;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class BackwardTreeGenWithEarlyStop {
    static Logger logger = Logger.getLogger(BackwardTreeGen.class.getName());

    static StopWatch stopWatch;

    static TopKList topKPattern = TopRankK.topKPatternList;

    public static ArrayList<ArrayList<Pattern>> run(Graph G, ArrayList<ArrayList<Pattern>> patternTree, FrequentEdgePatternResult frequentEdgePatternResult) throws InterruptedException {
        logger.debug("startBackwardWithEarlyStop...");
        ArrayList<Pattern> frequentEdgePattern = frequentEdgePatternResult.getFrequentEdgePattern();
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex = frequentEdgePatternResult.getFrequentEdgePatternIndex();
        TopKList topKPattern = new TopKList();
        ArrayList<ArrayList<Pattern>> backwardTree = new ArrayList<>();
        for (ArrayList<Pattern> toExpandPattern : patternTree) {
            if (toExpandPattern.size() == 0 || toExpandPattern.get(0).edgeSet().size() == 1) {
                continue;
            }
            ArrayList<Pattern> patterns = toExpandPattern;
            stopWatch = new StopWatch();
            stopWatch.start();
            while (true) {
//                if(patterns.size() > GlobalVar.K) {
//                    patterns.sort(Comparator.comparingInt(Pattern::getItr));
//                    patterns = new ArrayList<>(patterns.subList(0, GlobalVar.K));
//                }
                logger.error("当前patterns size: " + patterns.size());
                patterns = toExpand(G, patterns, frequentEdgePatternIndex);

                if (patterns.size() == 0) break;
//                else backwardTree.add(patterns);
            }
            stopWatch.stop();
            logger.error("此轮检测完毕，耗时：" + stopWatch.toString());
//            logger.error("此轮检测topK sup" + topKPattern.getTopKSupList().toString());
//            System.out.println(GlobalVar.cutNums);
//            logger.error("已经检测的pattern size: " + backwardTree.stream().mapToInt(ArrayList::size).sum());
        }
        return backwardTree;
    }


    private static ArrayList<Pattern> toExpand(Graph G, ArrayList<Pattern> toExpandPattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        ArrayList<Pattern> patterns = new ArrayList<>();
//        toExpandPattern.forEach(pattern -> {
        toExpandPattern.stream().anyMatch(pattern -> {
            if(pattern.getMaxItr() < topKPattern.getMinimumItr()) return true;
//            else topKPattern.insert(pattern);

            pattern.vertexSet().forEach(inNode -> {
                String inLabel = inNode.getLabel();
                if (!frequentEdgePatternIndex.containsKey(inLabel)) {
                    return;
                }
                frequentEdgePatternIndex.get(inLabel).forEach((outLabel, instanceIDList) -> {
                    if(Utils.getMaxItrByInstanceIDSet(instanceIDList.get(1)) < topKPattern.getMinimumItr()) return;
                    if (!pattern.getPatternInfo().containsKey(outLabel)) return;
                    pattern.getPatternInfo().get(outLabel).forEach((outNode -> {
                        if (pattern.containsEdge(inNode, outNode) || outNode == inNode) return;
                        HashSet<Integer> inNodeInstanceIDSet = (HashSet<Integer>) inNode.getInstanceIDSet().clone();
                        inNodeInstanceIDSet.retainAll(new HashSet<>(instanceIDList.get(0)));
                        HashSet<Integer> outNodeInstanceIDSet = (HashSet<Integer>) outNode.getInstanceIDSet().clone();
                        outNodeInstanceIDSet.retainAll(new HashSet<>(instanceIDList.get(1)));
                        if (Utils.isFrequent(inNodeInstanceIDSet, outNodeInstanceIDSet)) {
                            NewPatternWithTwoNewPNodeResult result = Utils.CopyPatternAndTwoNode(pattern, inNode, outNode);
                            Pattern newPattern = result.getNewPattern();
                            PNode newInNode = result.getInNode();
                            PNode newOutNode = result.getOutNode();
                            newInNode.setInstanceIDSet(inNodeInstanceIDSet);
                            newOutNode.setInstanceIDSet(outNodeInstanceIDSet);
//                            System.out.println("oldPattern:"+pattern.getPatternInfo());
//                            System.out.println("newPattern:"+newPattern.getPatternInfo());
                            if (!CheckInstance.checkInstanceByIndex(G, newPattern, newInNode, newOutNode, frequentEdgePatternIndex)) {
                                return;
                            }
                            newPattern.addEdge(newInNode, newOutNode, new PEdge(newInNode, newOutNode));
                            if (Utils.isIsomorphismInCurrentNewPatternList(patterns, newPattern)) {
                                return;
                            }
                            if (newPattern.getItr() >= topKPattern.getMinimumItr()) {
                                topKPattern.insert(newPattern);
                            }
                            if (newPattern.getMaxItr() >= topKPattern.getMinimumItr()) {
                                patterns.add(newPattern);
                            }else{
                                GlobalInfo.cutNums++;
                            }
                        }
                    }));
                });
            });
        return false;
        });
//        patterns.sort((o1, o2) -> o2.getItr() - o1.getItr());
        patterns.sort((o1, o2) -> Float.compare(o2.getItr(), o1.getItr()));
        return patterns;
    }
}
