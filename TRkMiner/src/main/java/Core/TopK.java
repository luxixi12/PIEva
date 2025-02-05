package Core;

import Base.Graph;
import Base.Pattern;
import Results.FrequentEdgePatternResult;
import Utils.Utils;
import Utils.Tools;
import Utils.GlobalVar;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;



public class TopK {
    static Logger logger = Logger.getLogger(TopK.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.error("***********************[start]***********************");
//        Graph G =  new InitGraph().getUserGraph();
//        Graph G = new InitGraph().getGraphFromPath("src/main/java/Datasets/TriangleTest.lg");
//        Graph G = new InitGraph().getGraphFromPath("src/main/java/Datasets/facebookcmp.txt");
        Graph G = new InitGraph().getGraphFromPath(GlobalVar.INPUT_PATH);
//        Graph G = InitGraph.getGraphFromCSVFile("src/main/java/Datasets/LastFM");
//        Utils.draw(G);

//        logger.error("Graph loaded:" + GlobalVar.INPUT_PATH);
//        logger.error("Graph Info: " + G.vertexSet().size() + " vertices, " + G.edgeSet().size() + " edges");
//        logger.error("Mining Info: " + GlobalVar.THRESHOLD + " threshold");
//        long startTime = System.currentTimeMillis();
//        FrequentEdgePatternResult frequentEdgePatternResult = GetInfoFromGraph.getFrquentEdgePattern(G);
//        Tools.saveArr(GlobalVar.globalNum);
//        var index = frequentEdgePatternResult.getFrequentEdgePatternIndex();
//        var label4_10 = index.get("4").get("10").get(0);
//        var label10_4 = index.get("10").get("4").get(0);
//        var label4_8 = index.get("4").get("8").get(0);
//        var label8_4 = index.get("8").get("4").get(0);
//        var label8_10 = index.get("8").get("10").get(0);
//        var label10_8 = index.get("10").get("8").get(0);
//        var s4_10 = new HashSet<>(index.get("4").get("10").get(0)) ;
//        var s10_4 = new HashSet<>(index.get("10").get("4").get(0));
//        var s4_8 = new HashSet<>(index.get("4").get("8").get(0));
//        var s8_4 = new HashSet<>(index.get("8").get("4").get(0));
//        var s8_10 = new HashSet<>(index.get("8").get("10").get(0));
//        var s10_8 = new HashSet<>(index.get("10").get("8").get(0));
//        logger.error("start:s4_10:"+s4_10.size()+" s10_4:"+s10_4.size()+" s4_8:"+s4_8.size()+" s8_4:"+s8_4.size()+" s8_10:"+s8_10.size()+" s10_8:"+s10_8.size());
//        s4_8.retainAll(s4_10);
//        logger.error("s4_8:"+s4_8.size());
//        var new_s8_4 = new HashSet<>();
//        for (int i = 0; i < s4_8.size(); i++) {
//            if(s4_8.contains(label4_8.get(i))) {
//                new_s8_4.add(label8_4.get(i));
//            }
//        }
//        var new_s10_4 = new HashSet<>();
//        for (int i = 0; i < s4_10.size(); i++) {
//            if(s4_10.contains(label4_10.get(i))) {
//                new_s10_4.add(label10_4.get(i));
//            }
//        }
//        s8_4.retainAll(new_s8_4);
//        s10_4.retainAll(new_s10_4);
//        logger.error("8-4-10::s8_4:"+s8_4.size()+" , s4_8:"+s4_8.size()+" , s10_4: "+s10_4.size());
//
//
//        s8_10.retainAll(s8_4);
//        var new_s10_8 = new HashSet<>();
//        for (int i = 0; i < s8_10.size(); i++) {
//            if(s8_10.contains(label8_10.get(i))) {
//                new_s10_8.add(label10_8.get(i));
//            }
//        }
//        s10_8.retainAll(new_s10_8);
//        logger.error("10-8::s10_8:"+s10_8.size()+" , s8_10:"+s8_10.size());
//
//        var new_s4 = new HashSet<>();



        logger.error("Graph loaded:"+GlobalVar.INPUT_PATH);
        logger.error("Graph Info: " + G.vertexSet().size()+ " vertices, " + G.edgeSet().size() + " edges");
        logger.error("Mining Info: " + GlobalVar.THRESHOLD + " threshold");
        long startTime = System.currentTimeMillis();
        FrequentEdgePatternResult frequentEdgePatternResult = GetInfoFromGraph.getFrquentEdgePattern(G);
//        Tools.saveArr(GlobalVar.globalNum);
        ArrayList<ArrayList<Pattern>> patternTree = ForwardTreeGen.run(G, frequentEdgePatternResult);
        Tools.savePatternTree(patternTree);
        long forwardTime = System.currentTimeMillis();
        logger.error("Forward Time: " + (forwardTime - startTime));
        int forwardNums = patternTree.stream().mapToInt(ArrayList::size).sum();
//        patternTree.forEach(patterns -> patterns.forEach(pattern -> logger.info(pattern.toString())));
        logger.error("Forward size: " + forwardNums);
        final int forwardPatternTreeHeight = patternTree.size();
        ArrayList<ArrayList<Pattern>> backwardPattern = BackwardTreeGen.run(G, patternTree, frequentEdgePatternResult);
        int backwardNums = backwardPattern.stream().mapToInt(ArrayList::size).sum();
        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime);
        logger.error("Backward Time: " + (endTime - forwardTime));
        logger.error("Backward size: " + backwardNums);
        logger.error("usedTime:" + usedTime);
        logger.error("PatternNums:" + (forwardNums+backwardNums));
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        logger.error(new Date(System.currentTimeMillis()));

//        backwardPattern.forEach(patterns -> patterns.forEach(pattern -> logger.info(pattern.toString())));

        if (GlobalVar.DRAWING) {
            patternTree.forEach(patterns -> patterns.forEach(Utils::draw));
            backwardPattern.forEach(patterns -> patterns.forEach(Utils::draw));
        }

        if (GlobalVar.SAVE_PATTERN_TREE) {
            Tools.savePatternTree(patternTree);
//            Tools.savePatternTree(backwardPattern);
        }
    }
}
