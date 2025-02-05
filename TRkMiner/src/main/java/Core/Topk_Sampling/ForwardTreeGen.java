package Core.Topk_Sampling;

import Base.*;
import Results.FrequentEdgePatternResult;
import Results.NewPatternWithANewPNodeResult;
import Utils.Utils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;


public class ForwardTreeGen {
    static Logger logger = Logger.getLogger(ForwardTreeGen.class.getName());
    static int flag = 1;
    static PatternPriorityQueue topKPatternList;
    //    static HashMap<Integer, ArrayList<Pattern>> patternTree;
    static ConcurrentHashMap<Integer, List<Pattern>> patternTree;
    //    static PriorityQueue<Pattern> priorityQueue;
    static PriorityBlockingQueue<Pattern> priorityQueue;

    static ExecutorService inThreadPool = Executors.newCachedThreadPool();


    //**************************************************[Run In PriorityQueue]****************************************************************
    public static ArrayList<ArrayList<Pattern>> runInPriorityQueue(Graph G, FrequentEdgePatternResult edgePatternResult) throws Exception {
        logger.error("***********************[Run In PriorityQueue]**********************");
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex = edgePatternResult.getFrequentEdgePatternIndex();
        priorityQueue = Sampling.priorityQueue;
        topKPatternList = Sampling.getPatternPriorityQueue();
        patternTree = new ConcurrentHashMap<>();

        int i = 1;
        while (!priorityQueue.isEmpty()) {
            Pattern expandPattern = priorityQueue.poll();
            getNewPatternsFromOnePattern(expandPattern, frequentEdgePatternIndex);
//            if (i % 40 == 0) {
//                Utils.cutForwardPatternTree(patternTree);
//                if (i % 80 == 0) Utils.updataCoreNode();
//                System.out.println("priorityQueue size:" + priorityQueue.size());
//                topKPatternList.printTopKItr();
//            }

            if (priorityQueue.size() == 0) break;
            if (priorityQueue.element().getMaxItrInForward() < topKPatternList.getMinimumItr()) {
                System.out.println("break:" + priorityQueue.element().getMaxItrInForward() + "," + topKPatternList.getMinimumItr());
                break;
            }
//            if (i % 200 == 0) {
////                System.out.println("Remove");
//                System.out.println("Remove Start:"+priorityQueue.size());
//                priorityQueue.removeIf(pattern -> pattern.getMaxItrInForward() < topKPatternList.getMinimumItr());
//                System.out.println("Remove End:"+priorityQueue.size() +","+ expandPattern.getItr()+","+expandPattern.getMNI()+","+expandPattern.getQ());
//                topKPatternList.printTopKItr();
//            }
//            System.out.println("i:"+i);
            i++;
        }
//        inThreadPool.awaitTermination(1000,TimeUnit.SECONDS);
        inThreadPool.shutdown();
        System.out.println("i:" + i);
        //treeList
//        ArrayList<ArrayList<Pattern>> treeList = new ArrayList<>(new ArrayList<>(patternTree.values()));
        ArrayList<ArrayList<Pattern>> treeList = new ArrayList<>();
        patternTree.forEach((key, value) -> treeList.add(new ArrayList<>(value)));
        treeList.forEach(list -> list.sort((o1, o2) -> Float.compare(o2.getItr(), o1.getItr())));
        treeList.forEach(list -> list.removeIf(pattern -> pattern.getMaxItr() < topKPatternList.getMinimumItr()));
        treeList.removeIf(list -> list.size() == 0);
        treeList.sort((l1, l2) -> Float.compare(l2.get(0).getItr(), l1.get(0).getItr()));
        return treeList;
    }

    private static void getNewPatternsFromOnePattern(Pattern expandPattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) throws InterruptedException {
        expandPattern.vertexSet().forEach(inNode -> {   //遍历点
            String inLabel = inNode.getLabel();
            HashSet<Integer> inInstanceIDSet = inNode.getInstanceIDSet();
            HashSet<Integer> tempSet = new HashSet<>();
            // 遍历所有包含label作为起点的频繁边
//            if (Utils.getMaxItrByInstanceIDSet(inNode.getInstanceIDSet().size()) < topKPatternList.getMinimumItr())
//                return;


            frequentEdgePatternIndex.get(inLabel).forEach((outLabel, outInstanceIDSet) -> {
                //                if (Utils.getMaxItrByInstanceIDSet(outInstanceIDSet.get(1)) < topKPatternList.getMinimumItr()) return;
                if (expandPattern.isExistLabel(outLabel)) return;    //防止有重复标签
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
                    //                if (Utils.isFrequent(newPNode.getInstanceIDSet()) && Utils.getMaxItrByInstanceIDSet(newPNode.getInstanceIDSet().size()) > topKPatternList.getMinimumItr()) {
                    NewPatternWithANewPNodeResult result = Utils.CopyPatternAndANode(expandPattern, inNode);
                    PNode newInNode = result.getpNode();
                    Pattern newPattern = result.getNewPattern();
                    newPattern.addVertex(newPNode);

                    newPattern.addEdge(newInNode, newPNode, new PEdge(newInNode, newPNode));
                    // 验证同构
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    if (!patternTree.containsKey(newPattern.edgeSet().size())) {
//                            patternTree.put(newPattern.edgeSet().size(), new ArrayList<>());
                        patternTree.put(newPattern.edgeSet().size(), Collections.synchronizedList(new ArrayList<>()));
                    }
                    if (Utils.isIsomorphismInCurrentNewPatternList(patternTree.get(newPattern.edgeSet().size()), newPattern)) {
                        return;
                    }
                    patternTree.get(newPattern.edgeSet().size()).add(newPattern);
                    //                        CheckInstance.checkInstanceByGraph(G, newPattern, newPNode, tempSet);
                    inThreadPool.execute(() -> {
//                            stopWatch.stop();
//                            GlobalInfo.isoTime += stopWatch.getTime();
//                            stopWatch = new StopWatch();
//                            stopWatch.start();
                        if (!CheckInstance.checkInstanceByIndex(newPattern, newPNode, frequentEdgePatternIndex))
                            return;
//                            stopWatch.stop();
//                            GlobalInfo.checkTime += stopWatch.getTime();
                        if (Utils.isFrquentPattern(newPattern) && newPattern.getMaxItrInForward() > topKPatternList.getMinimumItr()) {
                            topKPatternList.insert(newPattern);
                            priorityQueue.add(newPattern);
                        }
                    });

                }
            });


        });

//        inCounts.await();


    }


}



