package Base;

import Utils.GlobalVar;
import Utils.Tools;
import Utils.Utils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @param
 * @author HappyCower
 * @return
 * @creed: 本类用来维护固定长度的优先队列，存的是pattern
 * @date 2022/7/26 16:22
 */

public class PatternPriorityQueue {

    private PriorityBlockingQueue<Pattern> patternPriorityQueue;

    private Logger logger = Logger.getLogger(PatternPriorityQueue.class.getName());
    Lock lock = new ReentrantLock();

    private int length;

    public  PatternPriorityQueue() {
        this.patternPriorityQueue = new PriorityBlockingQueue<>(GlobalVar.K, (p1, p2) -> Float.compare(p1.getMNI(), p2.getMNI()));
        this.length = GlobalVar.K;
    }

    public PatternPriorityQueue(int length) {
        this.patternPriorityQueue = new PriorityBlockingQueue<>(GlobalVar.K, (p1, p2) -> Float.compare(p1.getMNI(), p2.getMNI()));
        this.length = length;
    }


//    public void insert(Pattern pattern) {
//
//        try {
//            lock.lock();
////            System.out.println("B"+patternPriorityQueue.size()+" "+length+" "+patternPriorityQueue.size());
//            if (patternPriorityQueue.size() >= length) {
//                int minMNI = patternPriorityQueue.element().getMNI();
//                GlobalVar.THRESHOLD = patternPriorityQueue.element().getMNI();
//
////                System.out.println(minMNI + " " + pattern.getMNI());
//                if (pattern.getMNI() > minMNI) {
//                    patternPriorityQueue.poll();
//                    patternPriorityQueue.add(pattern);
//                    GlobalVar.THRESHOLD = patternPriorityQueue.element().getMNI();
////                    printTopKMNI();
////                    System.out.println("new thr:"+GlobalVar.THRESHOLD+" "+patternPriorityQueue.element().getMNI());
////                    printTopKMNI();
//                }
//            } else {
//                patternPriorityQueue.add(pattern);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            lock.unlock();
//        }
//
//    }
//
    public void insert(Pattern pattern) {

        try {
            lock.lock();
//            System.out.println("B"+patternPriorityQueue.size()+" "+length+" "+patternPriorityQueue.size());
            if (patternPriorityQueue.size() >= length) {
                int minMNI = patternPriorityQueue.element().getMNI();
                GlobalVar.THRESHOLD = patternPriorityQueue.element().getMNI();

//                System.out.println("MAX_NUM"+GlobalVar.MAX_NUM);
                if(GlobalVar.MAX_NUM<=0){
//                    GlobalVar.MAX_NUM = 0;
                    GlobalVar.THRESHOLD = (int)Double.POSITIVE_INFINITY;
                }else {

//                System.out.println(minMNI + " " + pattern.getMNI());
                    if (pattern.getMNI() > minMNI) {
                        patternPriorityQueue.poll();
                        patternPriorityQueue.add(pattern);
                        GlobalVar.THRESHOLD = patternPriorityQueue.element().getMNI();

                        GlobalVar.MAX_NUM = GlobalVar.K;


//                    printTopKMNI();
//                    System.out.println("new thr:"+GlobalVar.THRESHOLD+" "+patternPriorityQueue.element().getMNI());
//                    printTopKMNI();
                    }
                }
            } else {
                patternPriorityQueue.add(pattern);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void add(Pattern pattern) {
        insert(pattern);
    }

    public int minMNI() {
        return patternPriorityQueue.peek().getMNI();
    }

    public int size() {
        return patternPriorityQueue.size();
    }

    public boolean isFull() {
        return patternPriorityQueue.size() == length;
    }

    public void print() {
        patternPriorityQueue.forEach(System.out::println);
    }

    public boolean contains(Pattern pattern) {
        return patternPriorityQueue.contains(pattern);
    }

    public void savePatterns(String filePath) throws IOException {
//        System.out.println(filePath);
        ArrayList<Pattern> temp = new ArrayList<>();
        while (patternPriorityQueue.size() > GlobalVar.K){
            System.out.println('q');
            patternPriorityQueue.poll();
        }
        patternPriorityQueue.forEach(temp::add);
        temp.sort((o1, o2) -> Float.compare(o2.getMNI(), o1.getMNI()));
        Tools.outputPatternsWithMNI(temp, filePath);
    }

    public void saveMNI(String filePath) throws IOException {
//        System.out.println(filePath);
//        File write = new File(filePath);
//        if (!write.exists()) {
//            if (!write.createNewFile()){
//                System.out.println('-');
//            }
//        }
        FileWriter fileWritter = new FileWriter(filePath);
        BufferedWriter writter = new BufferedWriter(fileWritter);

        var mni = new ArrayList<Integer>();
        patternPriorityQueue.forEach(pattern -> mni.add(pattern.getMNI()));
//        mni.sort(Integer::compareTo);
        mni.sort((o1, o2) -> Integer.compare(o2, o1));
        mni.forEach(i -> {
            try {
                writter.write(i.toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        });
        writter.flush();
        writter.close();

        System.out.println("save MNI success!");
    }

    public void draw() {
        patternPriorityQueue.forEach(Utils::draw);
    }

    public void printTopKMNI() {
        StringBuffer sb = new StringBuffer();
        sb.append("topKItr: ");
        ArrayList<Integer> list = new ArrayList<>();
        patternPriorityQueue.forEach(pattern -> {
            list.add(pattern.getMNI());
        });
        Arrays.stream(list.toArray()).sorted().forEach(i -> sb.append(i + ", "));
        logger.error(sb.toString());

    }

    public float getMinimumMNI() {
        return patternPriorityQueue.element().getMNI();
    }

    public float getMinimumItr() {
        return patternPriorityQueue.element().getMNI();
    }

}
