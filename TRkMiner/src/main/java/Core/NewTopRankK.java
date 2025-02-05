package Core;

import Base.*;
import Results.FrequentEdgePatternResult;
import Utils.GlobalVar;
import Utils.Tools;
import Utils.Utils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


public class NewTopRankK {
    static Logger logger = Logger.getLogger(NewTopRankK.class.getName());

    public static TopKList getTopKPatternList() {
        return topKPatternList;
    }

    static TopKList topKPatternList = new TopKList();

    static ArrayList<ArrayList<Pattern>> lazyPatternTree = new ArrayList<>();

    static int numsOfLazyPatternShouldCheck = 0;


    //    // MNI与Pattern的映射
//    public static HashMap<Integer,ArrayList<Pattern>> MNI2Pattern = new HashMap<>();
//    // MNI的优先队列
//    public static PriorityQueue<Integer> MNIQueue = new PriorityQueue<>((o1, o2) -> o2 - o1);
    // 维护前K个最大的Pattern
//    public static PriorityQueue<Pattern> TopKQueue = new PriorityQueue<>((o1, o2) -> o2.getMNI() - o1.getMNI());

    public static void main(String[] args) throws Exception {
        logger.error("***********************[New Top Rank K, Start]***********************");
//        Graph G =  new InitGraph().getUserGraph();
//        Graph G = new InitGraph().getGraphFromPath("src/main/java/Datasets/TriangleTest.lg");
//        Graph G = new InitGraph().getGraphFromPath("src/main/java/Datasets/facebookcmp.txt");
        //*********************************************[Reading Graph]*********************************
        Graph G = new InitGraph().getGraphFromPath(GlobalVar.INPUT_PATH);
//        Utils.draw(G);

        //*********************************************[Get OnePatternEdge]*********************************
        logger.error("Graph loaded:" + GlobalVar.INPUT_PATH);
        logger.error("Graph Info: " + G.vertexSet().size() + " vertices, " + G.edgeSet().size() + " edges");
        logger.error("Mining Info: " + GlobalVar.THRESHOLD + " threshold，k:"+GlobalVar.K);
        long startTime = System.currentTimeMillis();
        FrequentEdgePatternResult frequentEdgePatternResult = NewGetInfoFromGraph.getAllEdgePattern(G);
        logger.error("Label Nums: " + frequentEdgePatternResult.getLabel().size());
        GlobalInfo.labelNum = frequentEdgePatternResult.getLabel().size();
        var onePatternEdge = frequentEdgePatternResult.getFrequentEdgePattern();
//        onePatternEdge.removeIf(pattern -> pattern.getMaxItrInForward() < topKPatternList.getMinimumItr());
        System.out.println("onePatternEdge size: " + onePatternEdge.size());
        logger.error("one edge pattern max:" + onePatternEdge.get(0) + " min:" + onePatternEdge.get(onePatternEdge.size() - 1));
        logger.error("one edge pattern suppose info: mean:"+GlobalInfo.meanMNIInOneEdge +", max:"+GlobalInfo.maxMNIInOneEdge+", min:"+GlobalInfo.minMNIInOneEdge);
        //*********************************************[Reset Threshold]*********************************
//        System.out.println(frequentEdgePatternResult.getFrequentEdgePattern().size());
//        GlobalVar.THRESHOLD = G.vertexSet().size() / frequentEdgePatternResult.getLabel().size() / 10;
//        logger.error("Threshold: " + GlobalVar.THRESHOLD);
//        frequentEdgePatternResult.getFrequentEdgePattern().removeIf(Pattern->Pattern.getMNI()<GlobalVar.THRESHOLD);
//
//        System.out.println(frequentEdgePatternResult.getFrequentEdgePattern().size());

        //*********************************************[Forward]*********************************
        ArrayList<ArrayList<Pattern>> patternTree = ForwardTreeGen.fastRun(G, frequentEdgePatternResult);
//        patternTree.forEach(patterns -> patterns.forEach(pattern -> System.out.println(pattern.getMNI())));
//        Tools.savePatternTree(patternTree);
        long forwardTime = System.currentTimeMillis();
        logger.error("Forward Time: " + (forwardTime - startTime));
        int forwardNums = patternTree.stream().mapToInt(ArrayList::size).sum();
        patternTree.forEach(patterns->patterns.forEach(logger::error));
        logger.error("第一层第一个：点的个数:"+ patternTree.get(0).get(0).vertexSet().size()+" sup:"+patternTree.get(0).get(0).getItr());

        logger.error("第二层第一个:点的个数："+ patternTree.get(1).get(0).vertexSet().size()+" sup:"+patternTree.get(1).get(0).getItr());
////        patternTree.forEach(patterns -> patterns.forEach(pattern -> logger.info(pattern.toString())));
        logger.error("Forward size: " + forwardNums);
//        final int forwardPatternTreeHeight = patternTree.size();


        //*********************************************[Backward]*********************************
        ArrayList<ArrayList<Pattern>> backwardPattern = NewBackwardTreeGenWithEarlyStop.run(G, patternTree, frequentEdgePatternResult);
        int backwardNums = backwardPattern.stream().mapToInt(ArrayList::size).sum();
        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime);
        logger.error("Backward Time: " + (endTime - forwardTime));
        logger.error("Backward size: " + backwardNums);
        logger.error("usedTime:" + usedTime);
        logger.error("PatternNums:" + (forwardNums + backwardNums));
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        logger.error(new Date(System.currentTimeMillis()));

       //*********************************************[check by forward]*********************************

//        lazyPatternTree.forEach(patterns -> patterns.forEach(pattern -> {
//            if (pattern.getMaxItrInForward() >= topKPatternList.getMinimumItr()) numsOfLazyPatternShouldCheck++;
//        } ));
//        logger.error("numsOfLazyPatternNums:" + lazyPatternTree.stream().mapToInt(ArrayList::size).sum());
//        logger.error("numsOfLazyPatternShouldCheck:" + numsOfLazyPatternShouldCheck);


        //*********************************************[topK]*********************************
        HashMap<Float, ArrayList<Pattern>> topK = topKPatternList.getTopKPatternList();
        SkipList topKSup = topKPatternList.getTopKItrList();
        SkipList.Node head = topKSup.getHead();
        topKSup.printAll();
//        System.out.println(topK);
        ArrayList<Pattern> topKPatterns = new ArrayList<>();
        while (head.forwards[0] != null) {
            float k = head.forwards[0].getData();
//            System.out.println(k + ":" + topK.get(k).size());
            topK.get(k).forEach(pattern -> logger.error(k+" "+pattern+" "+pattern.getItr()));
            topK.get(k).forEach(pattern -> Utils.draw(pattern,""+k));

            topK.get(k).forEach(pattern -> {
                        try {Tools.outputPattern(pattern);}
                        catch (IOException e) {e.printStackTrace();}
            });
            head = head.forwards[0];
        }
//        Tools.savePatternTree(topKPatterns);
//        topK.forEach((k, patterns) -> patterns.forEach(Utils::draw));
//        topK.forEach((k, patterns) -> patterns.forEach(pattern -> {
//            Utils.draw(pattern, k + "");
//        }));
//        topK.forEach((k, patterns) -> patterns.forEach(pattern -> logger.error(k+" : "+pattern)));


        logger.error("cutNum:"+ GlobalInfo.cutNums);
//        backwardPattern.forEach(patterns -> patterns.forEach(pattern -> logger.info(pattern.toString())));
//
//        if (GlobalVar.DRAWING) {
//            patternTree.forEach(patterns -> patterns.forEach(Utils::draw));
//            backwardPattern.forEach(patterns -> patterns.forEach(Utils::draw));
//        }
//
//        if (GlobalVar.SAVE_PATTERN_TREE) {
//            Tools.savePatternTree(patternTree);
////            Tools.savePatternTree(backwardPattern);
//        }
    }
}
