package Core.Topk_Sampling;

import Base.Graph;
import Base.NewTopKList;
import Base.Pattern;
import Base.PatternPriorityQueue;
import Core.InitGraph;
import Results.FrequentEdgePatternResult;
import Utils.CommandLineParser;
import Utils.GlobalVar;
import Utils.Tools;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class Sampling {
    static Logger logger = Logger.getLogger(ForwardTreeGen.class.getName());

    static ConcurrentHashMap<Integer, List<Pattern>> patternTree;

    static PriorityBlockingQueue<Pattern> priorityQueue;

    public static PatternPriorityQueue getPatternPriorityQueue() {
        return patternPriorityQueue;
    }

    static PatternPriorityQueue patternPriorityQueue;
    static NewTopKList topKPatternList = new NewTopKList();

    static ExecutorService forwardThreadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        CommandLineParser.parse(args);
        GlobalVar.in_sampling = GlobalVar.INPUT_PATH;
        ArrayList<String> dataNames = new ArrayList<>() {{
//            add(filename);
//            add("Youtubedatacmp2.txt_undirected.txt");
//            add("patent.txt");
//            add("amazondate.txt_undirected.txt");
//  add("skitter.txt");
//            add("mico.lg");
//
//            add("synthetic_WS_n100000_e1000000_96_100label.txt");
//            add("synthetic_WS_n200000_e2000000_96_100label.txt");
//            add("synthetic_WS_n300000_e3000000_96.txt");
//            add("synthetic_WS_n400000_e4000000_96_100label.txt");
//            add("synthetic_WS_n500000_e5000000_96_100label.txt");


//            add("citeseercmp.txt_undirected.txt"
          add("twitch.txt");
//            add("dblp.txt");
//            add("amazondate.txt_undirected.txt");
//            add("youtube.txt");
//            add("twitter-labels-grami1.txt_undirected.txt");


        }};
        ArrayList<Integer> Ks = new ArrayList<>() {{
//            add(50);
//            add(100);
//            add(150);
//            add(200);
//            add(250);
//            add(300);
//            add(500);
//            add(700);
            add(500);
//            add(knum);

//            add(1100);
//            add(1300);
//            add(1500);
//            add(800);
        }};
        dataNames.forEach(name -> {
            Ks.forEach(k -> {
                for (int i = 0; i < 1; i++) {
                    Runtime runtime = Runtime.getRuntime();
                    runtime.gc();
                    try {
                        running(name, k);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        });

    }
    public static void running(String dataName, int K) throws Exception {
        GlobalVar.sigmod_a=1;
        GlobalVar.K = K;
        GlobalVar.B = K;
        GlobalVar.MAX_NUM=K;
        GlobalVar.THRESHOLD = 1;
        GlobalVar.M=0;
        String dataPath = "src/main/java/Datasets/" + dataName;
//        String dataPath = "/work/home/w_123/topRank/Datasets/" + dataName;
        Graph G = new InitGraph().getGraphFromPath(dataPath);
        System.out.println("Graph Info: " + G.vertexSet().size() + " vertices, " + G.edgeSet().size() + " edges");



        topKPatternList=new NewTopKList();
        priorityQueue = new PriorityBlockingQueue<>(100, (p1, p2) -> Float.compare(p2.getMNI(), p1.getMNI()));
//        patternPriorityQueue = new patternPriorityQueue();

//        GlobalInfo.topKPatternList = topKPatternList;

        FrequentEdgePatternResult frequentEdgePatternResult = GetInfoFromGraph.getEdgePattern(G);

        System.out.println("labelNums: "+frequentEdgePatternResult.getLabel().size());

        double startTime = System.currentTimeMillis();
//        Runtime runtime = Runtime.getRuntime();
        SamplingMining.run(G, frequentEdgePatternResult);
        double mem=MemoryLogger.getInstance().checkMemory();
        double endTime = System.currentTimeMillis();
        double time = (endTime - startTime)/1000;
        System.out.println("UseTime:"+time);
        System.out.println("内存:"+mem);
        String s="无扩展";
        try {
            Tools.writeCSV("src/main/java/out/result.csv", dataName, K, time, mem,s,GlobalVar.M);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(GlobalVar.THRESHOLD);
        System.out.println(GlobalVar.out_sampling+" output, Done!");

        System.out.println("---------------------");
    }
}
