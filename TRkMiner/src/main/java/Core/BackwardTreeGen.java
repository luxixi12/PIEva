package Core;

import Base.Graph;
import Base.PEdge;
import Base.PNode;
import Base.Pattern;
import Results.FrequentEdgePatternResult;
import Results.NewPatternWithTwoNewPNodeResult;
import Utils.Utils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackwardTreeGen {
    static CountDownLatch countDownLatch;
    static Logger logger = Logger.getLogger(BackwardTreeGen.class.getName());
    /**
     * @param G                         Graph
     * @param patternTree               ArrayList<ArrayList<Pattern>>
     * @param frequentEdgePatternResult FrequentEdgePatternResult
     *                                  [G, toExpandPattern, frequentEdgePatternResult]* @return java.util.ArrayList<Base.Pattern>
     * @author HappyCower
     * @creed: 后向拓展
     * @date 2022/3/24 14:46
     */

    public static ArrayList<ArrayList<Pattern>> run(Graph G, ArrayList<ArrayList<Pattern>> patternTree, FrequentEdgePatternResult frequentEdgePatternResult) throws InterruptedException {
        logger.debug("startBackward...");
        ArrayList<Pattern> frequentEdgePattern = frequentEdgePatternResult.getFrequentEdgePattern();
        HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex = frequentEdgePatternResult.getFrequentEdgePatternIndex();
        ArrayList<Pattern> topKPattern = new ArrayList<>();
        ArrayList<ArrayList<Pattern>> backwardTree = new ArrayList<>();
//        CopyOnWriteArrayList<ArrayList<Pattern>> backwardTree = new CopyOnWriteArrayList<>();
//        Vector<ArrayList<Pattern>> backwardTree = new Vector<>();

//        ArrayList<Pattern> toExpandPattern = patternTree.get(patternTree.size() - 1);
//        toExpandPattern.forEach(pattern -> {
//            System.out.println(":"+pattern.getPatternInfo());
//        });
//        System.out.println("BackwardToExpand："+toExpandPattern);
        countDownLatch = new CountDownLatch(patternTree.size() - 1);
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
//        logger.info("backward 1-core in fun1");
        logger.info("backward n-core in fun2 add synchronized");
//        logger.info("backward 1-core in fun2");
        int i=0;
        for (ArrayList<Pattern> toExpandPattern : patternTree) {
            if (toExpandPattern.size() == 0 || toExpandPattern.get(0).edgeSet().size() == 1) {
                continue;
            }

//
            cachedThreadPool.execute(() -> {
                ArrayList<Pattern> newPatterns = toExpandPattern;
                ArrayList<ArrayList<Pattern>> tempPatternTree = new ArrayList<>();
                while (true){
                    newPatterns = toExpand(G, newPatterns, frequentEdgePatternIndex);
                    if (newPatterns.size() == 0) break;
                    else tempPatternTree.add(newPatterns);
                }

                logger.info("tempTreeNum:"+tempPatternTree.stream().mapToInt(ArrayList::size).sum());
                if (tempPatternTree.size() > 0) {
                    synchronized (backwardTree) {
                        backwardTree.addAll(tempPatternTree);
                    }
                }
//                backwardTree.addAll(tempPatternTree);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        cachedThreadPool.shutdown();


            //            final ArrayList<Pattern> patterns = toExpand(G, toExpandPattern, frequentEdgePatternIndex);
//            cachedThreadPool.execute(() -> {
//                ArrayList<Pattern> newPatterns = patterns;
//                while (newPatterns.size()!= 0) {
//                    backwardTree.add(newPatterns);
//                    newPatterns = toExpand(G, newPatterns, frequentEdgePatternIndex);
//                }
//                countDownLatch.countDown();
//            });
            //        cachedThreadPool.shutdown();
//        countDownLatch.await();
//    }
//    cachedThreadPool.shutdown();
//        countDownLatch.await();

//
//
//            ArrayList<Pattern> patterns = toExpand(G, toExpandPattern, frequentEdgePatternIndex);
//            while (patterns.size() != 0) {
//                backwardTree.add(patterns);
//                patterns = toExpand(G, patterns, frequentEdgePatternIndex);
//            }
//            logger.info("backwardTree size:"+(backwardTree.stream().mapToInt(ArrayList::size).sum()-i));
//            i = backwardTree.stream().mapToInt(ArrayList::size).sum();
//        }

//
//

//            ArrayList<Pattern> patterns = toExpandPattern;
//            while (true) {
//                    patterns = toExpand(G, patterns, frequentEdgePatternIndex);
//                    if (patterns.size() == 0) break;
//                    else backwardTree.add(patterns);
//                }
//            logger.info("backwardTree size:"+(backwardTree.stream().mapToInt(ArrayList::size).sum()-i));
//            i = backwardTree.stream().mapToInt(ArrayList::size).sum();
//        }


        return  backwardTree;
    }


    private static ArrayList<Pattern> toExpand(Graph G, ArrayList<Pattern> toExpandPattern, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        ArrayList<Pattern> patterns = new ArrayList<>();
        toExpandPattern.forEach(pattern -> {
            pattern.vertexSet().forEach(inNode -> {
                String inLabel = inNode.getLabel();
                if (!frequentEdgePatternIndex.containsKey(inLabel)) {
                    return;
                }
                frequentEdgePatternIndex.get(inLabel).forEach((outLabel, instanceIDList) -> {
                    if (!pattern.getPatternInfo().containsKey(outLabel)) {
                        return;
                    }
                    pattern.getPatternInfo().get(outLabel).forEach((outNode -> {
                        if (pattern.containsEdge(inNode, outNode) || outNode == inNode) {
                            return;
                        }
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
                            patterns.add(newPattern);
                        }
                    }));
                });
            });
        });
        return patterns;
    }
}
