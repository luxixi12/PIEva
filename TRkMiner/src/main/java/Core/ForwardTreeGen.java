package Core;

import Base.*;
import Results.FrequentEdgePatternResult;
import Results.NewPatternWithANewPNodeResult;
import Utils.Utils;
import Utils.GlobalVar;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class ForwardTreeGen {
    static Logger logger = Logger.getLogger(ForwardTreeGen.class.getName());
    static int flag = 1;
    static TopKList topKPatternList = TopRankK.topKPatternList;


    //********************************************[Frequent Pattern Mining]********************************************

    /**
     * @param G                 图
     * @param edgePatternResult 频繁边模式
     *                          [G, edgePatternResult]* @return java.util.ArrayList<java.util.ArrayList<Base.Pattern>>
     * @author HappyCower
     * @creed: 前向拓展的过程
     * @date 2022/3/19 20:25
     */
    public static ArrayList<ArrayList<Pattern>> run(Graph G, FrequentEdgePatternResult edgePatternResult) {
        System.out.println("startForward...");
        ArrayList<Pattern> frequentEdgePattern = edgePatternResult.getFrequentEdgePattern();
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex = edgePatternResult.getFrequentEdgePatternIndex();
        ArrayList<ArrayList<Pattern>> patternTree = new ArrayList<>();
        patternTree.add(frequentEdgePattern);
        int treeHeight = 1;
        while (patternTree.size() == treeHeight) {
            treeHeight += 1;
            getNewPatternFromOneEdge(G, patternTree, frequentEdgePatternIndex);
        }
        return patternTree;
    }

    /**
     * @param G                        图
     * @param patternTree              存放所有模式的树
     * @param frequentEdgePatternIndex 频繁边模式的索引表
     *                                 [G, patternTree, frequentEdgePatternIndex]* @return void
     * @author HappyCower
     * @creed: 向Pattern添加一条边以获得一个满足要求的新模式
     * @date 2022/3/19 20:22
     */
    private static void getNewPatternFromOneEdge(Graph G,
                                                 ArrayList<ArrayList<Pattern>> patternTree,
                                                 HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        ArrayList<Pattern> toExpandPattern = patternTree.get(patternTree.size() - 1);
        ArrayList<Pattern> newPatternList = new ArrayList<>();
        if (GlobalVar.PAINTING) logger.debug("ForwardToExpandPattern:" + toExpandPattern);
        toExpandPattern.forEach(oldPatten -> {
//            oldPatten.edgeSet().forEach(edge -> { //遍历边
            // 遍历当前pattern的所有顶点
            oldPatten.vertexSet().forEach(inNode -> {   //遍历点
//                PNode inNode = edge.getInNode();
                String inLabel = inNode.getLabel();
                HashSet<Integer> inInstanceIDSet = inNode.getInstanceIDSet();
                HashSet<Integer> tempSet = new HashSet<>();
                // 遍历所有包含label作为起点的频繁边
                frequentEdgePatternIndex.get(inLabel).forEach((outLabel, outInstanceIDSet) -> {
                    PNode newPNode = new PNode(outLabel, new HashSet<>());
                    ArrayList<Integer> oldInInstanceIDList = outInstanceIDSet.get(0);
                    ArrayList<Integer> oldOutInstanceIDList = outInstanceIDSet.get(1);
                    tempSet.clear();
                    // 验证所有可能的instance
                    for (int i = 0; i < oldInInstanceIDList.size(); i++) {
                        // index里面的instanceID需要包含在原来的node里面且新加入的instance不在原pattern里面
                        if (inInstanceIDSet.contains(oldInInstanceIDList.get(i))
                                && !Utils.isInPatternByID(oldPatten, oldOutInstanceIDList.get(i))) {
                            newPNode.addInstanceID(oldOutInstanceIDList.get(i));
                            tempSet.add(oldInInstanceIDList.get(i));
                        }
                    }

                    if (Utils.isFrequent(newPNode.getInstanceIDSet())) {
                        NewPatternWithANewPNodeResult result = Utils.CopyPatternAndANode(oldPatten, inNode);
                        PNode newInNode = result.getpNode();
                        Pattern newPattern = result.getNewPattern();
                        newPattern.addVertex(newPNode);
                        newPattern.addEdge(newInNode, newPNode, new PEdge(newInNode, newPNode));
                        // 验证同构
                        if (Utils.isIsomorphismInCurrentNewPatternList(newPatternList, newPattern)) {
                            return;
                        }
                        // 更新instanceID
                        if (flag == 1) {
//                            logger.info("checkInstanceByGraph.");
                            logger.info("checkInstanceByIndex.");
                            flag = 0;
                        }
//                        CheckInstance.checkInstanceByGraph(G, newPattern, newPNode, tempSet);
                        if (!CheckInstance.checkInstanceByIndex(newPattern, newPNode, frequentEdgePatternIndex)) return;
                        if (Utils.isFrquentPattern(newPattern)) {
                            newPatternList.add(newPattern);
                        }
                    }
                });
            });
        });
        if (newPatternList.size() != 0) {
            logger.info("forward:第" + patternTree.size() + "层:" + newPatternList.size());
//            if (patternTree.size() == 2){
//                newPatternList.forEach(Utils::draw);
//            }
//
//            for (Pattern pattern : newPatternList) {
//                try {
//                    Tools.outputPattern(pattern,"level-"+patternTree.size()+".lg");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            newPatternList.sort((o1, o2) -> o2.getMNI() - o1.getMNI());
            patternTree.add(newPatternList);
        }
    }


    //************************************************[Naive Top Rank K]******************************************************************

    public static ArrayList<ArrayList<Pattern>> newRun(Graph G, FrequentEdgePatternResult edgePatternResult) {
        System.out.println("startForward find All...");
        ArrayList<Pattern> frequentEdgePattern = edgePatternResult.getFrequentEdgePattern();
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex = edgePatternResult.getFrequentEdgePatternIndex();
        ArrayList<ArrayList<Pattern>> patternTree = new ArrayList<>();
        patternTree.add(frequentEdgePattern);
        int treeHeight = 1;
        while (patternTree.size() == treeHeight) {
            treeHeight += 1;
            getAllNewPatternFromOneEdge(G, patternTree, frequentEdgePatternIndex);
        }
//        patternTree.forEach(patterns -> {
//            if (patterns.size()> GlobalVar.K){
//                patterns.subList(0, GlobalVar.K);
//            }
//        });
//        Stream.iterate(0, i -> i + 1).limit(patternTree.size()).forEach(i -> {
//            var patterns = patternTree.get(i);
//            if (patterns.size()> GlobalVar.K){
//                patternTree.set(i,new ArrayList<>(patterns.subList(0, GlobalVar.K)));
//            }
//        });
//        patternTree.sort((o1, o2) -> o2.get(0).getItr() - o1.get(0).getItr());
        patternTree.sort((o1, o2) -> Float.compare (o2.get(0).getItr(), o1.get(0).getItr()));
        return patternTree;
    }


    private static void getAllNewPatternFromOneEdge(Graph G,
                                                    ArrayList<ArrayList<Pattern>> patternTree,
                                                    HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        ArrayList<Pattern> toExpandPattern = patternTree.get(patternTree.size() - 1);
        ArrayList<Pattern> newPatternList = new ArrayList<>();
        ArrayList<Pattern> lazyPatternList = new ArrayList<>();
        if (GlobalVar.PAINTING) logger.debug("ForwardToExpandAllPattern:" + toExpandPattern);
//        toExpandPattern.forEach(oldPattern -> {
        toExpandPattern.stream().anyMatch(oldPattern -> {
//            if (oldPattern.getMaxItr() < topKPatternList.getMinimumItr()) return true;
            if (oldPattern.getMaxItrInForward() < topKPatternList.getMinimumItr()) return true;
//            else topKPatternList.insert(oldPattern);
            // 遍历当前pattern的所有顶点
            oldPattern.vertexSet().forEach(inNode -> {   //遍历点
                String inLabel = inNode.getLabel();
                HashSet<Integer> inInstanceIDSet = inNode.getInstanceIDSet();
                HashSet<Integer> tempSet = new HashSet<>();
                // 遍历所有包含label作为起点的频繁边
                frequentEdgePatternIndex.get(inLabel).forEach((outLabel, outInstanceIDSet) -> {
                    if (Utils.getMaxItrByInstanceIDSet(outInstanceIDSet.get(1)) < topKPatternList.getMinimumItr()) return;
                    if (oldPattern.isExistLabel(outLabel)) return;    //防止有重复标签
                    PNode newPNode = new PNode(outLabel, new HashSet<>());
                    ArrayList<Integer> oldInInstanceIDList = outInstanceIDSet.get(0);
                    ArrayList<Integer> oldOutInstanceIDList = outInstanceIDSet.get(1);
                    tempSet.clear();
                    // 验证所有可能的instance
                    for (int i = 0; i < oldInInstanceIDList.size(); i++) {
                        // index里面的instanceID需要包含在原来的node里面且新加入的instance不在原pattern里面
                        if (inInstanceIDSet.contains(oldInInstanceIDList.get(i))) {
                            newPNode.addInstanceID(oldOutInstanceIDList.get(i));
                            tempSet.add(oldInInstanceIDList.get(i));
                        }
                    }
                    if (Utils.isFrequent(newPNode.getInstanceIDSet())) {
                        NewPatternWithANewPNodeResult result = Utils.CopyPatternAndANode(oldPattern, inNode);
                        PNode newInNode = result.getpNode();
                        Pattern newPattern = result.getNewPattern();
                        newPattern.addVertex(newPNode);

                        newPattern.addEdge(newInNode, newPNode, new PEdge(newInNode, newPNode));
                        // 验证同构
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        if (Utils.isIsomorphismInCurrentNewPatternList(newPatternList, newPattern)) {
                            return;
                        }
                        stopWatch.stop();
                        GlobalInfo.isoTime += stopWatch.getTime();
                        stopWatch = new StopWatch();
                        stopWatch.start();
//                        CheckInstance.checkInstanceByGraph(G, newPattern, newPNode, tempSet);
                        if (!CheckInstance.checkInstanceByIndex(newPattern, newPNode, frequentEdgePatternIndex)) return;
                        stopWatch.stop();
                        GlobalInfo.checkTime += stopWatch.getTime();
                        if (Utils.isFrquentPattern(newPattern) && newPattern.getMaxItrInForward() > topKPatternList.getMinimumItr()) {
                            topKPatternList.insert(newPattern);
                            if (newPattern.getMaxItr() > topKPatternList.getMinimumItr()) {
                                newPatternList.add(newPattern);
                            } else {
                                lazyPatternList.add(newPattern);
                            }
//                            newPatternList.add(newPattern);
                        }
                    }
                });
            });
            return false;
        });


        if (newPatternList.size() != 0) {
            logger.error("forward:第" + patternTree.size() + "层:" + newPatternList.size());
            logger.error("此时总已用同构的时间：" + GlobalInfo.isoTime);
            logger.error("此时总已用checkInstance的时间：" + GlobalInfo.checkTime);
//            newPatternList.sort((o1, o2) -> o2.getItr() - o1.getItr());
            newPatternList.sort((o1, o2) -> Float.compare(o2.getItr(), o1.getItr()));
//            newPatternList.forEach(logger::info);
//            TopRankK.topKPatternList.getTopKItrList().printAll();
            patternTree.add(newPatternList);
        }
        if (lazyPatternList.size() != 0) {
            logger.error("LazyPatternList:" + lazyPatternList.size());
            lazyPatternList.sort((o1, o2) -> o2.getMNI() - o1.getMNI());
            TopRankK.lazyPatternTree.add(lazyPatternList);
        }
    }


    //**********************************************[Optimize Top RanK K]***************************************************************
    public static ArrayList<ArrayList<Pattern>> fastRun(Graph G, FrequentEdgePatternResult edgePatternResult) throws Exception {
        topKPatternList = NewTopRankK.topKPatternList;
        ArrayList<ArrayList<Pattern>> patternTree = new ArrayList<>();
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex = edgePatternResult.getFrequentEdgePatternIndex();
        ArrayList<Pattern> frequentEdgePattern = edgePatternResult.getFrequentEdgePattern();
        StopWatch stopWatch = new StopWatch();
        var patternList = frequentEdgePattern;
        for (int i = 0; i < GlobalVar.step; i++) {
            stopWatch.reset();
            stopWatch.start();
            patternList = getPatternsWithoutCheck(patternList, frequentEdgePatternIndex);
            stopWatch.stop();
            logger.error("getPatternsWithoutCheck used time:" + stopWatch.getTime());
            if(patternList.size() == 0) break;
            var newPatternList = new ArrayList<Pattern>();
            int min = patternList.get(0).getRealMNI();
            int size = patternList.size();
            for (Pattern pattern : patternList) {
                size--;
                if(pattern.getPredictionMNI() < min) {
                    System.out.println("cut,剩余：" + size);
                    break;
                }
//                System.out.println(size);
                stopWatch.reset();
                stopWatch.start();
                setARealNode(pattern,frequentEdgePatternIndex);
                if(!CheckInstance.checkInstanceByIndex(pattern, pattern.getTempNode(), frequentEdgePatternIndex)) continue;
                stopWatch.stop();
                topKPatternList.insert(pattern);
                newPatternList.add(pattern);
                min = pattern.getRealMNI();
            }
            if (newPatternList.size() != 0) {
                newPatternList.sort((o1, o2) -> o2.getRealMNI() - o1.getRealMNI());
//                if(newPatternList.size() > GlobalVar.K) {
//                    newPatternList= new ArrayList<>(newPatternList.subList(0, GlobalVar.K)) ;
//                }
                patternTree.add(newPatternList);
                patternList = newPatternList;
                System.out.println("第" + i + "层：" + newPatternList.size());
            }else {
                break;
            }
        }
//        patternTree.sort((o1, o2) -> o2.get(0).getItr() - o1.get(0).getItr());

        patternTree.sort((o1, o2) -> Float.compare(o2.get(0).getItr() , o1.get(0).getItr()));
        topKPatternList.getTopKItrList().printAll();
//        System.out.println(topKPatternList.getMinimumItr());
//        patternTree.forEach(patterns -> patterns.forEach(Utils::draw));
        return patternTree;
    }

    private static void setARealNode(Pattern pattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) throws Exception {
        PNode inNode = pattern.getTempNode();
        PEdge edge = pattern.getTempEdge();
        PNode outNode = edge.opposite(inNode);
        var index = frequentEdgePatternIndex.get(inNode.getLabel()).get(outNode.getLabel());
        inNode.getInstanceIDSet().retainAll(new HashSet<>(index.get(0)));
    }


    private static ArrayList<Pattern> getPatternsWithoutCheck(ArrayList<Pattern> patterns, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
//        var index = result.getFrequentEdgePatternIndex();
        var newPatternList = new ArrayList<Pattern>();
//        System.out.println(patterns.size());
        for (Pattern pattern : patterns) {
            for (PNode inNode : pattern.vertexSet()) {
//                System.out.println(pattern+" "+inNode);
                frequentEdgePatternIndex.get(inNode.getLabel()).forEach((outNodeLabel, outNodeInstanceList) -> {
                    if (pattern.isExistLabel(outNodeLabel)) return;
                    var newPatternResult = Utils.CopyPatternAndANode(pattern, inNode);
                    var newOutNode = new PNode(outNodeLabel, new HashSet<>(outNodeInstanceList.get(1)));
                    var newInNode = newPatternResult.getpNode();
//                    System.out.println("start:"+newInNode.getInstanceSize());
//                    newInNode.getInstanceIDSet().retainAll(new HashSet<>(outNodeInstanceList.get(0)));
//                    System.out.println("end:"+newInNode.getInstanceSize());
                    var newPattern = newPatternResult.getNewPattern();
                    newPattern.addVertex(newOutNode);
                    var newEdge = new PEdge(newInNode, newOutNode);
                    newPattern.addEdge(newInNode, newOutNode,newEdge);
//                    newPattern.setPredictionMNI(Math.min(pattern.getRealMNI(), newOutNode.getInstanceSize()));
                    newPattern.setTempNode(newInNode);
                    newPattern.setTempEdge(newEdge);
                    if (Utils.isIsomorphismInCurrentNewPatternList(newPatternList, newPattern)) {
                        return;
                    }
                    newPatternList.add(newPattern);
//                    if (newPattern.getMaxItrInForward() > topKPatternList.getMinimumItr()) {
////                        topKPatternList.insert(newPattern);
//                        newPatternList.add(newPattern);
//                    }
                });
            }
        }
        newPatternList.sort((o1, o2) -> o2.getPredictionMNI() - o1.getPredictionMNI());
        return newPatternList;
    }

}
