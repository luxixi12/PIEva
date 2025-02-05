package Core.Topk_Sampling;

import Base.*;
import Results.FrequentEdgePatternResult;
import Results.NewPatternWithANewPNodeResult;
import Results.NewPatternWithTwoNewPNodeResult;
import Utils.GlobalVar;
import Utils.Utils;
import org.apache.log4j.Logger;
// 用的是threadPools对于多线程优化的checkInstance
import Core.TopRankK_PriorityQueue_ThreadPools_Sampling.CheckInstance;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SamplingMining {
    static Logger logger = Logger.getLogger(ForwardTreeGen.class.getName());

    static ConcurrentHashMap<Integer, List<Pattern>> patternTree;

    static PriorityBlockingQueue<Pattern> priorityQueue;

    public static PatternPriorityQueue getPatternPriorityQueue() {
        return patternPriorityQueue;
    }
    static NewTopKList topKPatternList;
    static PatternPriorityQueue patternPriorityQueue;

    static ExecutorService forwardThreadPool = Executors.newCachedThreadPool();

    static Lock lock = new ReentrantLock(false);
    static CountDownLatch countDownLatch;

    static void run(Graph G, FrequentEdgePatternResult edgePatternResult) throws Exception {
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex = edgePatternResult.getFrequentEdgePatternIndex();
        System.out.println("frequentEdgePatternIndex.size() = " + frequentEdgePatternIndex.size());
        AtomicInteger nums = new AtomicInteger();
        frequentEdgePatternIndex.forEach((key, value) -> {
           nums.addAndGet(value.size());
        });
//        System.out.println("nums = " + nums);
//        System.out.println("B = "+nums.intValue()/frequentEdgePatternIndex.size());

//        priorityQueue = new PriorityBlockingQueue<>(100, (p1, p2) -> Float.compare(p2.getMNI(), p1.getMNI()));
//        patternPriorityQueue = new patternPriorityQueue();
        priorityQueue = Sampling.priorityQueue;
        topKPatternList=new NewTopKList();
//        patternPriorityQueue = Sampling.getPatternPriorityQueue();
        patternTree = new ConcurrentHashMap<>();
        edgePatternResult.getFrequentEdgePattern().forEach(pattern -> {
            topKPatternList.insert(pattern);
            priorityQueue.add(pattern);
        });
        ArrayList<ArrayList<Pattern>> patternTree = forwardMining(G, frequentEdgePatternIndex);
//        patternPriorityQueue.saveMNI(GlobalVar.out_sampling);
//        patternPriorityQueue.printTopKMNI();
        ArrayList<ArrayList<Pattern>> backwardPattern = backwardMining(G, patternTree, frequentEdgePatternIndex);
//        ArrayList<ArrayList<Pattern>> patternTree = ForwardTreeGen.runInPriorityQueue(G, edgePatternResult);
//        ArrayList<ArrayList<Pattern>> backwardPattern = BackwardTreeGenWithEarlyStop.run(G, patternTree, edgePatternResult);
//        System.out.println(backwardPattern.size());
        int sum=0;
        int edge=0;
        int a=100000000;
        for(float itr:topKPatternList.getTopKItrList()){
            sum+= topKPatternList.getTopKPatternList().get(itr).size();
            for (Pattern pattern:topKPatternList.getTopKPatternList().get(itr)){
                if (pattern.edgeSet().size()>edge) edge=pattern.getEdgeSum();
                if(pattern.getMNI()<a) a= pattern.getMNI();
            }
        }

    }


    static ArrayList<ArrayList<Pattern>> forwardMining(Graph G, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) throws InterruptedException, IOException {
        int i = 1;
//        System.out.println(priorityQueue.size());
        while (!priorityQueue.isEmpty()) {
//            System.out.println(priorityQueue.element().getMNI()+"-"+priorityQueue.size()+"-"+GlobalVar.THRESHOLD);
//            if (priorityQueue.element().getMNI() <= GlobalVar.THRESHOLD) {
//                break;
//            }
            Pattern expandPattern = priorityQueue.poll();
//            GlobalVar.MAX_NUM--;
            if (expandPattern.getMaxItrInForward() < topKPatternList.getMinimumItr()) {
                System.out.println("break:" + expandPattern.getMaxItrInForward() + "," + topKPatternList.getMinimumItr());
                break;
            }
            getNewPatternsFromOnePattern(expandPattern, frequentEdgePatternIndex);
//            priorityQueue.forEach(pattern -> {
//                if (pattern.getMNI() <= GlobalVar.THRESHOLD) {
//                    priorityQueue.remove(pattern);
//                }
//            });
//            System.out.println("start  " + priorityQueue.size());
//            for (Pattern pattern : priorityQueue) {
//                if (pattern.getMNI() < topKPatternList.getMinimumItr()) {
//                    if(priorityQueue.contains(pattern)) {
//                        priorityQueue.remove(pattern);
//                    }
//                }
//            }
//            System.out.println("end  " + priorityQueue.size());
//            patternPriorityQueue.saveMNI(GlobalVar.out_sampling);
            System.out.println("start  " + priorityQueue.size());
            for (Pattern pattern : priorityQueue) {
                if (pattern.getMNI() < GlobalVar.THRESHOLD) {
                    if(priorityQueue.contains(pattern)) {
                        priorityQueue.remove(pattern);
                    }
                }
            }
            System.out.println("end  " + priorityQueue.size());
            i++;
        }
//        inThreadPool.awaitTermination(1000,TimeUnit.SECONDS);
//        forwardThreadPool.shutdown();
        //treeList
        ArrayList<ArrayList<Pattern>> treeList = new ArrayList<>();
        patternTree.forEach((key, value) -> treeList.add(new ArrayList<>(value)));
        treeList.forEach(list -> list.removeIf(pattern -> pattern.getMaxItr() < topKPatternList.getMinimumItr()));
        treeList.forEach(list -> list.sort((o1, o2) -> Float.compare(o2.getItr(), o1.getItr())));
        treeList.removeIf(list -> list.size() == 0);
        treeList.sort((l1, l2) -> Float.compare(l2.get(0).getItr(), l1.get(0).getItr()));
        System.out.println("forwardMining--treesize:" + treeList.stream().mapToInt(ArrayList::size).sum() + ",Now Thr:" + GlobalVar.THRESHOLD);
        return treeList;
    }


    //    static ArrayList<ArrayList<Pattern>> backwardMining(Graph G, ArrayList<ArrayList<Pattern>> patternTree, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
//        ArrayList<ArrayList<Pattern>> backwardTree = new ArrayList<>();
//        for (ArrayList<Pattern> toExpandPattern : patternTree) {
//            if (toExpandPattern.size() == 0 || toExpandPattern.get(0).edgeSet().size() == 1) {
//                continue;
//            }
//            ArrayList<Pattern> patterns = toExpandPattern;
//            do {
//                patterns = toExpand(G, patterns, frequentEdgePatternIndex);
//                backwardTree.add(patterns);
//            } while (patterns.size() != 0);
//        }
//        System.out.println("backwardMining: " + backwardTree.stream().mapToInt(ArrayList::size).sum()+"Thr"+GlobalVar.THRESHOLD);
//
//        return backwardTree;
//    }
    static ArrayList<ArrayList<Pattern>> backwardMining(Graph G, ArrayList<ArrayList<Pattern>> patternTree, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) throws InterruptedException {
        ArrayList<ArrayList<Pattern>> backwardTree = new ArrayList<>();
        countDownLatch = new CountDownLatch(patternTree.size());
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (ArrayList<Pattern> toExpandPattern : patternTree) {
            if (toExpandPattern.size() == 0 || toExpandPattern.get(0).edgeSet().size() == 1) continue;
            cachedThreadPool.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " start!");
                ArrayList<Pattern> patterns = toExpandPattern;
                do {
                    patterns = toExpand(G, patterns, frequentEdgePatternIndex);
                    backwardTree.add(patterns);
                } while (patterns.size() != 0);
//                System.out.println("个数"+patterns.size());
                countDownLatch.countDown();
                System.out.println(threadName + " end!");
            });
        }
//        System.out.println("wait for all threads end!" + countDownLatch.getCount());

            countDownLatch.await();

//        System.out.println("线程池结束" + countDownLatch.getCount());
        topKPatternList.printTopKItr();
        topKPatternList.printTopKPatternList();
        System.out.println(topKPatternList.getPatternSize());
//        System.out.println("backwardMining: " + backwardTree.stream().mapToInt(ArrayList::size).sum() + "\nThr" + GlobalVar.THRESHOLD);
//        System.out.println("\nglobalThreshold: " + GlobalVar.THRESHOLD);
//        System.out.println("backwardMining--treesize:" + backwardTree.stream().mapToInt(ArrayList::size).sum() + ",Now Thr:" + GlobalVar.THRESHOLD);
        GlobalVar.M=topKPatternList.getPatternSize();
        cachedThreadPool.shutdown();
        return backwardTree;
    }

    private static void getNewPatternsFromOnePattern(Pattern expandPattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) throws InterruptedException {
        expandPattern.vertexSet().forEach(inNode -> {   //遍历点
            String inLabel = inNode.getLabel();
            HashSet<Integer> inInstanceIDSet = inNode.getInstanceIDSet();
            HashSet<Integer> tempSet = new HashSet<>();
            frequentEdgePatternIndex.get(inLabel).forEach((outLabel, outInstanceIDSet) -> {
                //TODO
                if(expandPattern.getMNI() > Math.min(outInstanceIDSet.get(0).size(), outInstanceIDSet.get(1).size())){
//                    System.out.println("ufgweuyfgewifgewiufegwiuegfiueg");
                    return;
                }
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
                    NewPatternWithANewPNodeResult result = Utils.CopyPatternAndANode(expandPattern, inNode);
                    PNode newInNode = result.getpNode();
                    Pattern newPattern = result.getNewPattern();
                    GlobalVar.MAX_NUM--;
                    if(GlobalVar.MAX_NUM<=0){
//                    GlobalVar.MAX_NUM = 0;
                        GlobalVar.THRESHOLD = (int)Double.POSITIVE_INFINITY;
                    }
                    newPattern.addVertex(newPNode);

                    newPattern.addEdge(newInNode, newPNode, new PEdge(newInNode, newPNode));
                    if (!patternTree.containsKey(newPattern.edgeSet().size())) {
                        patternTree.put(newPattern.edgeSet().size(), Collections.synchronizedList(new ArrayList<>()));
                    }
                    if (Utils.isIsomorphismInCurrentNewPatternList(patternTree.get(newPattern.edgeSet().size()), newPattern)) {
                        return;
                    }
                    patternTree.get(newPattern.edgeSet().size()).add(newPattern);
//                    forwardThreadPool.execute(() -> {
                    if (!CheckInstance.checkInstanceByIndex(newPattern, newPNode, frequentEdgePatternIndex))
                        return;

                    if (Utils.isFrquentPattern(newPattern)&& newPattern.getMaxItrInForward() >= topKPatternList.getMinimumItr() ) {
                        priorityQueue.add(newPattern);
                        topKPatternList.insert(newPattern);
                    }
//                    System.out.println(patternPriorityQueue.size()+" "+priorityQueue.size());
//                    System.out.println("GlobalVar.THRESHOLD  "+GlobalVar.THRESHOLD);
//                    System.out.println(newPattern.getMNI());

//                    });
                }
            });
        });
    }


    //    private static ArrayList<Pattern> toExpand(Graph G, ArrayList<Pattern> toExpandPattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
//        ArrayList<Pattern> patterns = new ArrayList<>();
//        toExpandPattern.stream().anyMatch(pattern -> {
//            pattern.vertexSet().forEach(inNode -> {
//                String inLabel = inNode.getLabel();
//                if (!frequentEdgePatternIndex.containsKey(inLabel)) {
//                    return;
//                }
//                frequentEdgePatternIndex.get(inLabel).forEach((outLabel, instanceIDList) -> {
////                    if(Utils.getMaxItrByInstanceIDSet(instanceIDList.get(1)) < topKPattern.getMinimumItr()) return;
//                    if (!pattern.getPatternInfo().containsKey(outLabel)) return;
//                    pattern.getPatternInfo().get(outLabel).forEach((outNode -> {
//                        if (pattern.containsEdge(inNode, outNode) || outNode == inNode) return;
//                        HashSet<Integer> inNodeInstanceIDSet = (HashSet<Integer>) inNode.getInstanceIDSet().clone();
//                        inNodeInstanceIDSet.retainAll(new HashSet<>(instanceIDList.get(0)));
//                        HashSet<Integer> outNodeInstanceIDSet = (HashSet<Integer>) outNode.getInstanceIDSet().clone();
//                        outNodeInstanceIDSet.retainAll(new HashSet<>(instanceIDList.get(1)));
//                        if (Utils.isFrequent(inNodeInstanceIDSet, outNodeInstanceIDSet)) {
//                            NewPatternWithTwoNewPNodeResult result = Utils.CopyPatternAndTwoNode(pattern, inNode, outNode);
//                            Pattern newPattern = result.getNewPattern();
//                            PNode newInNode = result.getInNode();
//                            PNode newOutNode = result.getOutNode();
//                            newInNode.setInstanceIDSet(inNodeInstanceIDSet);
//                            newOutNode.setInstanceIDSet(outNodeInstanceIDSet);
//
//                            var edge = new PEdge(newInNode, newOutNode);
//                            newPattern.addEdge(newInNode, newOutNode, edge);
//
//                            if (Utils.isIsomorphismInCurrentNewPatternList(patterns, newPattern)) return;
////                            System.out.println(newPattern.getMNI()+","+patternPriorityQueue.minMNI());
//                            if (!CheckInstance.checkInstanceByIndex(G, newPattern, newInNode, newOutNode, frequentEdgePatternIndex, edge))
//                                return;
////                            System.out.println(":"+newPattern.getMNI()+","+patternPriorityQueue.minMNI());
//                            patternPriorityQueue.add(newPattern);
//                            if (newPattern.getMNI() >= patternPriorityQueue.minMNI()) {
//                                patterns.add(newPattern);
//                            }
//                        }
//                    }));
//                });
//            });
//            return false;
//        });
////        patterns.sort((o1, o2) -> o2.getItr() - o1.getItr());
//        patterns.sort((o1, o2) -> Float.compare(o2.getItr(), o1.getItr()));
//        return patterns;
//    }
    private static ArrayList<Pattern> toExpand(Graph G, ArrayList<Pattern> toExpandPattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        ArrayList<Pattern> patterns = new ArrayList<>();
        CountDownLatch threadCounts = new CountDownLatch(toExpandPattern.size());
        ExecutorService patternBackwardThreadPool = Executors.newFixedThreadPool(toExpandPattern.size());
        toExpandPattern.stream().anyMatch(pattern -> {
//            if(pattern.getMNI()<topKPatternList.getMinimumItr()) return false;
            patternBackwardThreadPool.execute(() -> {
                pattern.vertexSet().forEach(inNode -> {
                    String inLabel = inNode.getLabel();
                    if (!frequentEdgePatternIndex.containsKey(inLabel)) {
                        return;
                    }
                    frequentEdgePatternIndex.get(inLabel).forEach((outLabel, instanceIDList) -> {
//                    if(Utils.getMaxItrByInstanceIDSet(instanceIDList.get(1)) < topKPattern.getMinimumItr()) return;
                        if (!pattern.getPatternInfo().containsKey(outLabel)) return;

                        //TODO
                        if(pattern.getMNI()>Math.min(instanceIDList.get(0).size(), instanceIDList.get(1).size())) return;


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

                                var edge = new PEdge(newInNode, newOutNode);
                                newPattern.addEdge(newInNode, newOutNode, edge);



                                try {
                                    lock.lock();
                                    if (Utils.isIsomorphismInCurrentNewPatternList(patterns, newPattern)) {
                                        return;
                                    }
                                    patterns.add(newPattern);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    lock.unlock();
                                }

                                if (!CheckInstance.checkInstanceByIndex(G, newPattern, newInNode, newOutNode, frequentEdgePatternIndex, edge))
                                    return;



//                                patternPriorityQueue.add(newPattern);
                                topKPatternList.insert(newPattern);



                            }
                        }));
                    });
                });
                threadCounts.countDown();
            });
            return false;
        });
        try {
            threadCounts.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        patternBackwardThreadPool.shutdown();
        patterns.sort((o1, o2) -> Float.compare(o2.getItr(), o1.getItr()));
        return patterns;
    }
}
