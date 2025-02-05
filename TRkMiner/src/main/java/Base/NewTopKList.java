//package Base;
//
//import Utils.GlobalVar;
//import Utils.Tools;
//import org.apache.log4j.Logger;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.PriorityBlockingQueue;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class NewTopKList {
//    private PriorityBlockingQueue<Float> topKItr;
////    private PriorityBlockingQueue<Integer> topKItr;
//
//    public Logger logger = Logger.getLogger(NewTopKList.class.getName());
//
//    private int nums;
//
//    Lock lock = new ReentrantLock();
//
//        private ConcurrentHashMap<Integer, List<Pattern>> topKPatternList;
////    private ConcurrentHashMap<Float, List<Pattern>> topKPatternList;
//
////    public NewTopKList() {
////        this.topKItr = new PriorityBlockingQueue<>(GlobalVar.K, Float::compare);
//////        this.topKItr = new PriorityBlockingQueue<>(GlobalVar.K, (f1, f2) -> Float.compare(f1, f2));
////        this.topKPatternList = new ConcurrentHashMap<>();
////        this.nums = 0;
////    }
//
//    public NewTopKList() {
//        this.topKItr = new PriorityBlockingQueue<>(GlobalVar.K, Integer::compare);
////        this.topKItr = new PriorityBlockingQueue<>(GlobalVar.K, (f1, f2) -> Float.compare(f1, f2));
//        this.topKPatternList = new ConcurrentHashMap<>();
//        this.nums = 0;
//    }
//
//    public void insert(Pattern pattern) {
//        float itr = pattern.getItr();
////        float itr = pattern.getItr();
////        int itr = pattern.getMNI();
//        if (topKItr.size() == GlobalVar.K && itr < topKItr.peek()) {
//            return;
//        }
////        System.out.println("itr: " + itr);
//
//        if (topKPatternList.containsKey(itr)) {
//            topKPatternList.get(itr).add(pattern);
//            return;
//        }
//
//        if (topKItr.size() == GlobalVar.K) {
//            try {
//                lock.lock();
//                topKPatternList.remove(topKItr.peek());
//                topKItr.poll();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                lock.unlock();
//            }
//        }
////        printTopKItr();
////        System.out.println(GlobalVar.THRESHOLD+","+GlobalVar.K+","+topKItr.size());
//        topKItr.put(itr);
//        topKPatternList.put(itr, Collections.synchronizedList(new ArrayList<>(List.of(pattern))));
//        if (topKItr.size() == GlobalVar.K) {
//            GlobalVar.THRESHOLD = topKItr.peek();
//        }
//
//
//
////        try {
////            lock.lock();
////            if (patternPriorityQueue.size() >= length) {
////                int minMNI = patternPriorityQueue.element().getMNI();
////                if (pattern.getMNI() > minMNI) {
////                    patternPriorityQueue.poll();
////                    patternPriorityQueue.add(pattern);
////                    GlobalVar.THRESHOLD = patternPriorityQueue.element().getMNI();
////                }
////            } else {
////                patternPriorityQueue.add(pattern);
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            lock.unlock();
////        }
//
//    }
//
//    public float getMinimumItr() {
//        if (topKItr.size() >= GlobalVar.K) {
//            return topKItr.peek();
//        }
//        return 0;
//    }
////    public void savePatterns(String filePath) throws IOException {
//////        System.out.println(filePath);
////        ArrayList<Pattern> temp = new ArrayList<>();
////        while (topKItr.size() > GlobalVar.K){
////            System.out.println('q');
////            topKItr.poll();
////            topKPatternList.remove(topKItr.peek());
////        }
////        topKPatternList.forEach(temp::add);
////        temp.sort((o1, o2) -> Double.compare(o2.getMaxItrInForward(), o1.getMaxItrInForward()));
////        Tools.outputPatternsWithMNI(temp, filePath);
////    }
//
//    public int getPatternSize(){
//        AtomicInteger size = new AtomicInteger();
//        topKPatternList.forEach((k, v) -> {
//            v.forEach(pattern -> {
//                size.getAndIncrement();
//            });
//        });
//        return size.get();
//    }
//    public int getNums() {
//        return nums;
//    }
//
//    public PriorityBlockingQueue<Integer> getTopKItrList() {
//        return topKItr;
//    }
//
//    public ConcurrentHashMap<Integer, List<Pattern>> getTopKPatternList() {
//        return topKPatternList;
//    }
//
//    public void printTopKItr() {
//        StringBuffer sb = new StringBuffer();
//        sb.append("当前topKItr: ");
//        Arrays.stream(topKItr.toArray()).sorted().forEach(i -> sb.append(i + ", "));
//        logger.error(sb.toString());
////        topKItr.forEach(System.out::print);
//    }
//
//
//    public void printTopKPatternList() {
//        StringBuffer sb = new StringBuffer();
//        sb.append("TopKPatternList: \n");
////        topKItr.forEach(itr->topKPatternList);
//        topKPatternList.entrySet().forEach(entry -> {
//            if (!topKItr.contains(entry.getKey()))
//                topKPatternList.remove(entry.getKey());
//        });
//        TreeMap<Integer, List<Pattern>> topK = new TreeMap<>(topKPatternList);
//
//        topK.forEach((k, v) -> {
//            sb.append("Itr=" + k + ": ");
//            v.forEach(sb::append);
//        });
//        logger.error(sb.toString());
//    }
//
//    public void drawTopK() {
//        topKPatternList.forEach((k, v) -> {
//            v.forEach(Pattern::draw);
//        });
//    }
//
//    public void clear() {
//        nums = 0;
//        GlobalVar.THRESHOLD = 1;
//        topKPatternList.clear();
//        topKItr.clear();
//    }
//
//    public void saveAllPattern(String dataName, Integer K) throws IOException {
//        TreeMap<Integer, List<Pattern>> topK = new TreeMap<>(topKPatternList);
//        ArrayList<Pattern> out = new ArrayList<>();
//        topK.forEach((k, v) -> {
//            out.addAll(v);
//        });
//        Tools.outputPatternsWithITR(out, "src/main/java/out/" + dataName + "_" + K + ".txt");
//    }
//
//    public void add(Pattern pattern) {
//        insert(pattern);
//    }
//
//    public boolean contains(Pattern pattern) {
//        AtomicBoolean flag = new AtomicBoolean(false);
//        topKPatternList.forEach((k, v) -> {
//            v.forEach(p -> {
//                if(p==pattern){
//                    flag.set(true);
//                    return;
//                }
//            });
//            if(flag.get()) {
//                return;
//            }
//        });
//        return flag.get();
//    }
//}
package Base;

import Utils.GlobalVar;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class NewTopKList {
    private PriorityBlockingQueue<Float> topKItr;

    public Logger logger = Logger.getLogger(NewTopKList.class.getName());

    private int nums;

    Lock lock=new ReentrantLock();

    //    private HashMap<Integer, ArrayList<Pattern>> topKPatternList;
    private ConcurrentHashMap<Float, List<Pattern>> topKPatternList;

    public NewTopKList() {
        this.topKItr = new PriorityBlockingQueue<>(GlobalVar.K, Float::compare);
//        this.topKItr = new PriorityBlockingQueue<>(GlobalVar.K, (f1, f2) -> Float.compare(f1, f2));
        this.topKPatternList = new ConcurrentHashMap<>();
        this.nums = 0;
    }

    public void insert(Pattern pattern) {
//        int itr = pattern.getItr();
        GlobalVar.MAX_NUM = GlobalVar.K;
        float itr = pattern.getItr();
        if(topKItr.size()>=GlobalVar.K && itr < topKItr.peek()) {return;}
//        System.out.println("itr: " + itr);
        try{
            lock.lock();
            if (topKPatternList.containsKey(itr)) {
                topKPatternList.get(itr).add(pattern);
                return;
            }
            if (topKItr.size()>= GlobalVar.K) {
                if(itr < topKItr.peek()) {return;}
                topKPatternList.remove(topKItr.peek());
                topKItr.poll();
            }
            topKItr.put(itr);
            topKPatternList.put(itr, Collections.synchronizedList(new ArrayList<>(List.of(pattern))));

            if(topKItr.size()==GlobalVar.K){
//                float Itr=topKItr.peek();
//                List<Pattern> patternList = topKPatternList.get(itr);
//                // 初始化最小值为一个较大的值
//                int min = Integer.MAX_VALUE;
////                Pattern minPattern = null;
//
//// 遍历列表，找到具有最小min值的Pattern
//                for (Pattern p : patternList) {
//                    int patternMin = pattern.getMNI();  // 假设Pattern类有getMin()方法
//                    if (patternMin < min) {
//                        min = patternMin;
//                    }
//                }
                float aaa=topKItr.peek();
                GlobalVar.THRESHOLD=(int)aaa;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public float getnum() {
//        if (topKItr.size() >= GlobalVar.K)
        Map<Float, Integer> valueCountMap = new HashMap<>();
        for (Float value : topKItr) {
            valueCountMap.put(value, valueCountMap.getOrDefault(value, 0) + 1);
        }

// 统计所有值的总数
        int totalCount = 0;
        for (Map.Entry<Float, Integer> entry : valueCountMap.entrySet()) {
            totalCount += entry.getValue();
        }

        return totalCount;
    }
    public int getPatternSize(){
        AtomicInteger size = new AtomicInteger();
        topKPatternList.forEach((k, v) -> {
            v.forEach(pattern -> {
                size.getAndIncrement();
            });
        });
        return size.get();
    }
    public float getMinimumItr() {
        if (topKItr.size() >= GlobalVar.K)
        {
            return topKItr.peek();
        }
        return 0;
    }

    public int getNums() {
        return nums;
    }

    public PriorityBlockingQueue<Float> getTopKItrList() {
        return topKItr;
    }

    public ConcurrentHashMap<Float, List<Pattern>> getTopKPatternList() {
        return topKPatternList;
    }

    public void printTopKItr(){
        StringBuffer sb = new StringBuffer();
        sb.append("当前topKItr: ");

        System.out.println("兴趣度个数:"+topKItr.size());

        Arrays.stream(topKItr.toArray()).sorted().forEach(i-> sb.append(i+", "));
        logger.error(sb.toString());
    }


    public void printTopKPatternList(){
        StringBuffer sb = new StringBuffer();
        sb.append("TopKPatternList: \n");
//        topKItr.forEach(itr->topKPatternList);
        topKPatternList.entrySet().forEach(entry->{
            if(!topKItr.contains(entry.getKey()))
                topKPatternList.remove(entry.getKey());
        });
        TreeMap<Float, List<Pattern>> topK = new TreeMap<>(topKPatternList);

        topK.forEach((k, v) -> {
            sb.append("Itr=" +k + ": ");
            v.forEach(sb::append);
        });
        logger.error(sb.toString());
    }

    public void drawTopK(){
        topKPatternList.forEach((k, v) -> {
            v.forEach(Pattern::draw);
        });
    }
}
