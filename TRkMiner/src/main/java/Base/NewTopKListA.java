package Base;

import Utils.GlobalVar;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Utils.Tools;
import org.apache.log4j.Logger;

public class NewTopKListA {
    private PriorityBlockingQueue<Float> topKItr;

    public Logger logger = Logger.getLogger(NewTopKList.class.getName());

    private int nums;

    Lock lock = new ReentrantLock();

    //    private HashMap<Integer, ArrayList<Pattern>> topKPatternList;
    private ConcurrentHashMap<Float, List<Pattern>> topKPatternList;

    public NewTopKListA() {
        this.topKItr = new PriorityBlockingQueue<>(GlobalVar.K, Float::compare);
//        this.topKItr = new PriorityBlockingQueue<>(GlobalVar.K, (f1, f2) -> Float.compare(f1, f2));
        this.topKPatternList = new ConcurrentHashMap<>();
        this.nums = 0;
    }

    public void insert(Pattern pattern) {
//        int itr = pattern.getItr();
        float itr = pattern.getItr();
        if (topKItr.size() >= GlobalVar.K && itr < topKItr.peek()) {
            return;
        }
//        System.out.println("itr: " + itr);

        if (topKPatternList.containsKey(itr)) {
            topKPatternList.get(itr).add(pattern);
            return;
        }

        if (topKItr.size() >= GlobalVar.K) {
            try {
                lock.lock();
                if (itr < topKItr.peek()) {
                    return;
                }
                topKPatternList.remove(topKItr.peek());
                topKItr.poll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        topKItr.put(itr);
        topKPatternList.put(itr, Collections.synchronizedList(new ArrayList<>(List.of(pattern))));

    }

    public float getMinimumItr() {
        if (topKItr.size() >= GlobalVar.K) {
            return topKItr.peek();
        }
        return 0;
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
    public int getNums() {
        return nums;
    }

    public PriorityBlockingQueue<Float> getTopKItrList() {
        return topKItr;
    }

    public ConcurrentHashMap<Float, List<Pattern>> getTopKPatternList() {
        return topKPatternList;
    }

    public void printTopKItr() {
        StringBuffer sb = new StringBuffer();
        sb.append("当前topKItr: ");
        Arrays.stream(topKItr.toArray()).sorted().forEach(i -> sb.append(i + ", "));
        logger.error(sb.toString());
//        topKItr.forEach(System.out::print);
    }


    public void printTopKPatternList() {
        StringBuffer sb = new StringBuffer();
        sb.append("TopKPatternList: \n");
//        topKItr.forEach(itr->topKPatternList);
        topKPatternList.entrySet().forEach(entry -> {
            if (!topKItr.contains(entry.getKey()))
                topKPatternList.remove(entry.getKey());
        });
        TreeMap<Float, List<Pattern>> topK = new TreeMap<>(topKPatternList);

        topK.forEach((k, v) -> {
            sb.append("Itr=" + k + ": ");
            v.forEach(sb::append);
        });
        logger.error(sb.toString());
    }

    public void drawTopK() {
        topKPatternList.forEach((k, v) -> {
            v.forEach(Pattern::draw);
        });
    }

    public void clear() {
        nums = 0;
        GlobalVar.THRESHOLD = 1;
        topKPatternList.clear();
        topKItr.clear();
    }

    public void saveAllPattern(String dataName, Integer K) throws IOException {
        TreeMap<Float, List<Pattern>> topK = new TreeMap<>(topKPatternList);
        ArrayList<Pattern> out = new ArrayList<>();
        topK.forEach((k, v) -> {
            out.addAll(v);
        });
        Tools.outputPatternsWithITR(out, "src/main/java/out/" + dataName + "_" + K + ".txt");
    }

    public void add(Pattern pattern) {
        insert(pattern);
    }

    public boolean contains(Pattern pattern) {
        AtomicBoolean flag = new AtomicBoolean(false);
        topKPatternList.forEach((k, v) -> {
            v.forEach(p -> {
                if(p==pattern){
                    flag.set(true);
                    return;
                }
            });
            if(flag.get()) {
                return;
            }
        });
        return flag.get();
    }
}
