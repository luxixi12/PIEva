package Base;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class GlobalInfo {

    // 同构验证耗时
    public static long isoTime = 0;

    // match确认耗时
    public static long checkTime = 0;

    // 修剪的模式数
    public static int cutNums = 0;

    //最大的单边MNI
    public static int maxMNIInOneEdge = 0;

    //最小的单边MNI
    public static int minMNIInOneEdge = 0;

    //平均的单边MNI
    public static int meanMNIInOneEdge = 0;

    //label数量
    public static int labelNum = 0;

    //标签的Map
    public static HashMap<String, Integer> labelMap = new HashMap<String, Integer>();

    //core Node
    public static HashMap<String, Integer> coreNode = new HashMap<>();

    //topK模式的List,方便全局取用
    public static NewTopKList topKPatternList;
    public static NewTopKListA topKPatternListA;
//    public static TopKList topKPatternList;

    //指向全局优先队列，方便取用
//    public static PriorityQueue<Pattern> priorityQueue;
    public static PriorityBlockingQueue<Pattern> priorityQueue;

    public static long getIsoTime() {
        return isoTime;
    }

    public static void setIsoTime(long isoTime) {
        GlobalInfo.isoTime = isoTime;
    }

    public static long getCheckTime() {
        return checkTime;
    }

    public static void setCheckTime(long checkTime) {
        GlobalInfo.checkTime = checkTime;
    }

    public static int getCutNums() {
        return cutNums;
    }

    public static void setCutNums(int cutNums) {
        GlobalInfo.cutNums = cutNums;
    }

    public static int getMaxMNIInOneEdge() {
        return maxMNIInOneEdge;
    }

    public static void setMaxMNIInOneEdge(int maxMNIInOneEdge) {
        GlobalInfo.maxMNIInOneEdge = maxMNIInOneEdge;
    }

    public static int getMinMNIInOneEdge() {
        return minMNIInOneEdge;
    }

    public static void setMinMNIInOneEdge(int minMNIInOneEdge) {
        GlobalInfo.minMNIInOneEdge = minMNIInOneEdge;
    }

    public static int getMeanMNIInOneEdge() {
        return meanMNIInOneEdge;
    }

    public static void setMeanMNIInOneEdge(int meanMNIInOneEdge) {
        GlobalInfo.meanMNIInOneEdge = meanMNIInOneEdge;
    }

    public static int getLabelNum() {
        return labelNum;
    }

    public static void setLabelNum(int labelNum) {
        GlobalInfo.labelNum = labelNum;
    }
}

