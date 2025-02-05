package Utils;

import java.util.ArrayList;
//import java.util.logging.Logger;

public class     GlobalVar {
    public static int THRESHOLD = 1;

    public static String name = "mico";
    public static final String OUTPUT_PATH = "src/main/java/Output/";
    //    public static final String INPUT_PATH = "src/main/java/Datasets/test.lg";
//    public static final String INPUT_PATH = "src/main/java/Datasets/facebookNew5Label.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/synthetic.txt";
//        public static String INPUT_PATH = "src/main/java/Datasets/patent-0.100.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/facebookzhishu.txt";
//    public static String INPUT_PATH = "src/main/java/Datasets/twitter-labels-gram-0.100.lg";
    public static String INPUT_PATH = "src/main/java/Datasets/mico.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/synthetic_WS_n300000_e3000000_96.txt";
//    public static String INPUT_PATH = "src/main/java/Datasets/twitch-0.100.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/dblp.txt";
//    public static String INPUT_PATH = "src/main/java/Datasets/amazon-0.100.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/citeseer.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/mico-0.200.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/facebookcmp.txt";
//    public static String INPUT_PATH = "src/main/java/Datasets/facebook7label-0.12.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/AstroPh_100label_p.txt";
//    public static String INPUT_PATH = "src/main/java/Datasets/aids.txt";
//    public static String INPUT_PATH = "src/main/java/Datasets/synthetic_WS_n1000000_e10000000_48.txt";
//    public static String INPUT_PATH = "src/main/java/Datasets/TriangleTest.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/patent_citations-0.200.lg";
//    public static String INPUT_PATH = "src/main/java/Datasets/Youtubedatacmp-0.100.lg";
//    public static final String INPUT_PATH = "src/main/java/Datasets/TriangleTest.lg";
//    public static final String INPUT_PATH = "src/main/java/Datasets/aids.txt";
//    public static final String INPUT_PATH = "src/main/java/Datasets/facebookcmp_20_50_25.0.lg";


    public static final float itr = 0.2f;

    public static  int K = 1100;

    public static  int M= 0;
    public static int MAX_NUM = 0;
    public static int B =  0;

    public static final boolean SAVE_PATTERN_TREE = true;      //是否保存模式树到文件

    public static final boolean PAINTING = false;       //是否输出一些中间信息

    public static int nums = 0;     //output时的一些中间变量

    public static final boolean DRAWING = false; // 是否可视化

    public static int limitedTime = 0; // 是否可视化

    public static ArrayList<Integer> globalNum = new ArrayList<>();

    public static int step = 5;


    public static int  theta = 7;    // 用于约束边的权重,theta越大权重越低

    public static String mode = "sigmod";

    public static double sigmod_a = 1; //sigmode = sigmod(Q)*sup

    // Sampling path
    public static String in_sampling = "";
//    public static String out_sampling = "src/main/java/Datasets/real/twitter-labels-grami.txt";
    public static String out_sampling = "src/main/java/Datasets/1.txt";

    public static String out_pattern = "src/main/java/Datasets/Out/8000patent_size.txt";

}
