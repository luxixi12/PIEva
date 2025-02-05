package Core.Topk_Sampling;

import Base.*;
import Core.TopRankK_PriorityQueue_ThreadPools_Sampling.CheckInstance;
import Results.FrequentEdgePatternResult;
import Results.NewPatternWithTwoNewPNodeResult;
import Utils.Utils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BackwardTreeGenWithEarlyStop {
    static Logger logger = Logger.getLogger(BackwardTreeGenWithEarlyStop.class.getName());

    static StopWatch stopWatch;

    static PatternPriorityQueue topKPattern;

    static Lock lock = new ReentrantLock(false);
    static CountDownLatch countDownLatch;

    public static ArrayList<ArrayList<Pattern>> run(Graph G, ArrayList<ArrayList<Pattern>> patternTree, FrequentEdgePatternResult frequentEdgePatternResult) throws InterruptedException {
        logger.debug("startBackwardWithEarlyStop...");
        ArrayList<Pattern> frequentEdgePattern = frequentEdgePatternResult.getFrequentEdgePattern();
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex = frequentEdgePatternResult.getFrequentEdgePatternIndex();
        topKPattern = Sampling.patternPriorityQueue;
        ArrayList<ArrayList<Pattern>> backwardTree = new ArrayList<>();

        countDownLatch = new CountDownLatch(patternTree.size());
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        for (ArrayList<Pattern> toExpandPattern : patternTree) {
            if (toExpandPattern.size() == 0 || toExpandPattern.get(0).edgeSet().size() == 1) continue;
            cachedThreadPool.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " start!");
                ArrayList<Pattern> newPatterns = toExpandPattern;
                while (true) {
                    newPatterns = toExpand(G, newPatterns, frequentEdgePatternIndex);
                    if (newPatterns.size() == 0) break;
                }
                System.out.println(threadName + " end!");
                countDownLatch.countDown();
            });
        }
        System.out.println("wait for all threads end!" + countDownLatch.getCount());
        countDownLatch.await();
        System.out.println("线程池结束" + countDownLatch.getCount());
        cachedThreadPool.shutdown();
        return backwardTree;
    }

    private static ArrayList<Pattern> toExpand(Graph G, ArrayList<Pattern> toExpandPattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
//        ArrayList<Pattern> patterns = new ArrayList<>();
        CountDownLatch threadCounts = new CountDownLatch(toExpandPattern.size());
        ExecutorService patternBackwardThreadPool = Executors.newFixedThreadPool(toExpandPattern.size());
        List<Pattern> patterns = Collections.synchronizedList(new ArrayList<>());
        toExpandPattern.stream().anyMatch(pattern -> {
            patternBackwardThreadPool.execute(() -> {
                String threadName = Thread.currentThread().getName();
                pattern.vertexSet().forEach(inNode -> {
                    String inLabel = inNode.getLabel();
                    if (!frequentEdgePatternIndex.containsKey(inLabel)) {
                        return;
                    }
                    frequentEdgePatternIndex.get(inLabel).forEach((outLabel, instanceIDList) -> {
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

                                var edge = new PEdge(newInNode, newOutNode);
                                newPattern.addEdge(newInNode, newOutNode, edge);
                                if (!CheckInstance.checkInstanceByIndex(G, newPattern, newInNode, newOutNode, frequentEdgePatternIndex, edge))
                                    return;

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
                                topKPattern.insert(newPattern);
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
        return new ArrayList<>(patterns);
    }
}
