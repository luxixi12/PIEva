package Utils;

import Base.*;
import Results.NewPatternWithANewPNodeResult;
import Results.NewPatternWithTwoNewPNodeResult;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.ext.JGraphXAdapter;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Utils {

    /**
     * @param pattern 模式
     * @param node    [pattern, node]* @return Results.NewPatternWithANewPNodeResult
     * @author HappyCower
     * @creed: 复制对应的模式，同时返回某个点的新地址
     * @date 2022/3/19 20:29
     */
    public static NewPatternWithANewPNodeResult CopyPatternAndANode(Pattern pattern, PNode node) {
        Pattern newPattern = new Pattern(PEdge.class);
        HashMap<PNode, PNode> temp = new HashMap<>();
//        System.out.println("---------"+node);
//        pattern.vertexSet().forEach(System.out::println);
//        draw(pattern);
        pattern.edgeSet().forEach(pEdge -> {
            PNode inNode = pEdge.getInNode();
            PNode outNode = pEdge.getOutNode();
            PNode newInNode, newOutNode;
            if (temp.containsKey(inNode)) {
                newInNode = temp.get(inNode);
            } else {
                newInNode = new PNode(inNode.getLabel(), (HashSet<Integer>) inNode.getInstanceIDSet().clone());
                temp.put(inNode, newInNode);
            }
            if (temp.containsKey(outNode)) {
                newOutNode = temp.get(outNode);
            } else {
                newOutNode = new PNode(outNode.getLabel(), (HashSet<Integer>) outNode.getInstanceIDSet().clone());
                temp.put(outNode, newOutNode);
            }
            newPattern.addVertex(newInNode);
            newPattern.addVertex(newOutNode);
            newPattern.addEdge(newInNode, newOutNode, new PEdge(newInNode, newOutNode));
        });
        newPattern.setPredictionMNI(pattern.getRealMNI());
        return new NewPatternWithANewPNodeResult(newPattern, temp.get(node));
    }


    /**
     * @param pattern 模式
     * @param node1   点
     * @param node2   点
     *                [pattern, node1, node2]* @return Results.NewPatternWithTwoNewPNodeResult
     * @author HappyCower
     * @creed: 复制一个模式，并返回给定的两个点的新地址
     * @date 2022/3/24 11:40
     */

    public static NewPatternWithTwoNewPNodeResult CopyPatternAndTwoNode(Pattern pattern, PNode node1, PNode node2) {
        Pattern newPattern = new Pattern(PEdge.class);
        HashMap<PNode, PNode> temp = new HashMap<>();
        pattern.edgeSet().forEach(pEdge -> {
            PNode inNode = pEdge.getInNode();
            PNode outNode = pEdge.getOutNode();
            PNode newInNode, newOutNode;
            if (temp.containsKey(inNode)) {
                newInNode = temp.get(inNode);
            } else {
                newInNode = new PNode(inNode.getLabel(), (HashSet<Integer>) inNode.getInstanceIDSet().clone());
                temp.put(inNode, newInNode);
            }
            if (temp.containsKey(outNode)) {
                newOutNode = temp.get(outNode);
            } else {
                newOutNode = new PNode(outNode.getLabel(), (HashSet<Integer>) outNode.getInstanceIDSet().clone());
                temp.put(outNode, newOutNode);
            }
            newPattern.addVertex(newInNode);
            newPattern.addVertex(newOutNode);
            newPattern.addEdge(newInNode, newOutNode, new PEdge(newInNode, newOutNode));
        });
        return new NewPatternWithTwoNewPNodeResult(newPattern, temp.get(node1), temp.get(node2));
    }

    /**
     * @param num [num]* @return double
     * @author HappyCower
     * @creed 计算log2
     * @date 2022/6/23 15:20
     */

    public static double log2(Double num) {
        return Math.log(num) / Math.log(2);
    }


    public static double logn(Double n, Double num) {
        return Math.log(num) / Math.log(n);
    }
    public static double sigmod(double x) {
//        double e = 1.71828;
        double e = 1.8;

        double a = GlobalVar.sigmod_a;
        return 1.0f/(1.0f+Math.pow(e,-a*x));
    }

    /**
     * @param instanceIDSet [instanceIDSet]* @return int
     * @author HappyCower
     * @creed: 获取当前instance的能拓展出的最大值
     * @date 2022/6/23 15:20
     */

    public static int getMaxItrByInstanceIDSet(ArrayList<Integer> instanceIDSet) {
//        int labelSize = GlobalInfo.labelNum;
        int maxl = GlobalVar.step;
//        int maxQ = (labelSize*(labelSize-1))/2+labelSize;
        int maxQ = (maxl * (maxl - 1)) / 2;
//        return maxQ*instanceIDSet.size();
        return (int) Double.POSITIVE_INFINITY;
    }

    public static int getMaxItrByInstanceIDSet(int instanceListSize) {
        //        int labelSize = GlobalInfo.labelNum;
        int maxl = GlobalVar.step;
//        int maxQ = (labelSize*(labelSize-1))/2+labelSize;
        int maxQ = (maxl * (maxl - 1)) / 2;
//        return maxQ*instanceListSize;
        return (int) Double.POSITIVE_INFINITY;
    }

    /**
     * @param pattern [pattern]* @return boolean
     * @author HappyCower
     * @creed: 判断当前的pattern是否频繁
     * @date 2022/3/19 20:30
     */
    public static boolean isFrquentPattern(Pattern pattern) {
        return pattern.getMNI() >= GlobalVar.THRESHOLD;
//        for (PNode node : pattern.vertexSet()) {
//            if (node.getInstanceIDSet().size() < GlobalVar.THRESHOLD) {
//                return false;
//            }
//        }
//        return true;
    }


    /**
     * @param G  Graph
     * @param id [G, id]* @return Base.Node
     * @author HappyCower
     * @creed: 通过ID获取图中的
     * @date 2022/3/19 20:30
     */
    public static Node getInstanceByIDFromGraph(Graph G, Integer id) {
        for (Node instance : G.vertexSet()) {
            if (instance.getId() == id) {
                return instance;
            }
        }
        return new Node(-1, "ERROR");
    }


    /**
     * @param patten 模式
     * @param id     [patten, id]* @return boolean
     * @author HappyCower
     * @creed: 判断当前的id是否包含在当前模式的所有点中
     * @date 2022/3/19 20:33
     */
    public static boolean isInPatternByID(Pattern patten, Integer id) {
        for (PNode pNode : patten.vertexSet()) {
            if (pNode.getInstanceIDSet().contains(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param pattern 模式
     *                [pattern]* @return void
     * @author HappyCower
     * @creed: 可视化模式
     * @date 2022/3/24 11:40
     */

    public static void draw(Pattern pattern) {
        JFrame frame = new JFrame(pattern.toString());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JGraphXAdapter<PNode, PEdge> graphAdapter =
                new JGraphXAdapter<>(pattern);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        frame.add(new mxGraphComponent(graphAdapter));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void draw(Pattern pattern, String name) {
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JGraphXAdapter<PNode, PEdge> graphAdapter =
                new JGraphXAdapter<>(pattern);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        frame.add(new mxGraphComponent(graphAdapter));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void draw(Graph G) {
        JFrame frame = new JFrame("Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JGraphXAdapter<Node, Edge> graphAdapter =
                new JGraphXAdapter<>(G);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        frame.add(new mxGraphComponent(graphAdapter));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }


    public static void draw(Graph G, String s) {
        JFrame frame = new JFrame(s);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JGraphXAdapter<Node, Edge> graphAdapter =
                new JGraphXAdapter<>(G);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        frame.add(new mxGraphComponent(graphAdapter));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    /**
     * @param patternList 待判断的pattern列表
     * @param newPattern  待比较的pattern
     *                    [patternList, newPattern]* @return boolean
     * @author HappyCower
     * @creed: 比较给定pattern在给定的patternList中是否存在同构，VF2算法实现
     * @date 2022/3/24 11:41
     */


    public static boolean isIsomorphismInCurrentNewPatternList(ArrayList<Pattern> patternList, Pattern newPattern) {
        if (patternList.size() == 0) {
            return false;
        }
        for (Pattern pattern : patternList) {
            if (pattern.equalsByPatternInfo(newPattern)) {
                VF2GraphIsomorphismInspector<PNode, PEdge> vf = new VF2GraphIsomorphismInspector<>(pattern, newPattern, new PNodeComparator(), new PEdgeComparator(), true);
                if (vf.isomorphismExists()) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isIsomorphismInCurrentNewPatternList(List<Pattern> patternList, Pattern newPattern) {
        if (patternList.size() == 0) {
//            System.out.println("1");
            return false;
        }
        for (Pattern pattern : patternList) {
//            System.out.println('2');
            if (pattern.equalsByPatternInfo(newPattern)) {
//                System.out.println('3');
//                VF2GraphIsomorphismInspector<PNode, PEdge> vf = new VF2GraphIsomorphismInspector<>(pattern, newPattern, new PNodeComparator(), new PEdgeComparator(), true);
                VF2GraphIsomorphismInspector<PNode, PEdge> vf = new VF2GraphIsomorphismInspector<>(pattern, newPattern, true);
                if (vf.isomorphismExists()) {
//                    System.out.println('4');
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param set 一个由HashSet的组成的列表
     *            [set]* @return boolean
     * @author HappyCower
     * @creed: 判断给定的所有HashSet是否都满足频繁阈值
     * @date 2022/3/24 11:44
     */

    @SafeVarargs
    public static boolean isFrequent(HashSet<Integer>... set) {
        if (set.length == 0) {
            return false;
        }
        for (HashSet<Integer> hs : set) {
            if (hs.size() < GlobalVar.THRESHOLD) {
                return false;
            }
        }
        return true;
    }


    /**
     * @param list 任意多个int
     *             [list]* @return boolean
     * @author HappyCower
     * @creed: 判断给定的所有int是否都满足频繁阈值
     * @date 2022/3/24 19:34
     */

    public static boolean isFrequent(Integer... list) {
        if (list.length == 0) {
            return false;
        }
        for (Integer i : list) {
            if (i < GlobalVar.THRESHOLD) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param v ArrayList<ArrayList<Integer>>
     *          [v]* @return boolean
     * @author HappyCower
     * @creed: 判断Arr里面的Arr是否频繁
     * @date 2022/3/29 15:51
     */

    public static boolean isFrequent(ArrayList<ArrayList<Integer>> v) {
        for (ArrayList<Integer> list : v) {
            HashSet<Integer> set = new HashSet<>(list);
            if (set.size() < GlobalVar.THRESHOLD) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param o1 ArrayList<Integer>
     * @param o2 ArrayList<Integer>
     *           [o1, o2]* @return java.util.HashMap<java.lang.Integer,java.util.HashSet<java.lang.Integer>>
     * @author HappyCower
     * @creed: 利用第二给参数给第一个参数建立索引信息，返回的值可以快速查询到一中对应二中所包含的所有的值
     * @date 2022/3/24 11:46
     */

    public static HashMap<Integer, HashSet<Integer>> createIndexFromFirst2Second(Object o1, Object o2) {
        HashMap<Integer, HashSet<Integer>> hm = new HashMap<>();
        ArrayList<Integer> arr1 = (ArrayList<Integer>) o1;
        ArrayList<Integer> arr2 = (ArrayList<Integer>) o2;
        for (int i = 0; i < arr1.size(); i++) {
            int num1 = arr1.get(i);
            int num2 = arr2.get(i);
            if (hm.containsKey(num1)) {
                hm.get(num1).add(num2);
            } else {
                hm.put(num1, new HashSet<>(List.of(num2)));
            }
        }
        return hm;
    }


    /**
     *
     * @param pattern
     [pattern]* @return boolean
     * @author HappyCower
     * @creed: 验证当前pattern是否包含coreNode
     * @date 2022/6/23 15:28
     */

    public static boolean containAllCoreNode(Pattern pattern) {
        if (pattern.vertexSet().size() < GlobalInfo.coreNode.size()) return false;
        AtomicBoolean flag = new AtomicBoolean(true);
        GlobalInfo.coreNode.forEach((label, i) -> {
            if(!pattern.isExistLabel(label)) {
                flag.set(false);
                return;
            }
        });
        return flag.get();
    }


    /**
     *
     * @param pattern
     [pattern]* @return boolean
     * @author HappyCower
     * @creed: 验证当前模式的点都属于coreNode
     * @date 2022/6/23 15:34
     */

    public static boolean isAllPatternNodeInCoreNode(Pattern pattern){
        if (pattern.vertexSet().size() > GlobalInfo.coreNode.size()) return false;
        AtomicBoolean flag = new AtomicBoolean(true);
        pattern.vertexSet().forEach(pNode -> {
            if(!GlobalInfo.coreNode.containsKey(pNode.getLabel())){
                flag.set(false);
                return;
            }
        });
        return flag.get();
    }


    /**
     *
     * @author HappyCower
     * @creed: 随机获取num个coreNode
     * @date 2022/6/23 15:34
     */
    public static void getRandomCoreNode(int num) {
        GlobalInfo.coreNode.clear();
        var nodeLabel = new ArrayList<>(GlobalInfo.labelMap.keySet());
        Collections.shuffle(nodeLabel);
        for (int i = 0; i < num; i++) {
            GlobalInfo.coreNode.put(nodeLabel.get(i), GlobalInfo.labelMap.get(nodeLabel.get(i)));
        }
    }


    /**
     *
     * @param num
     [num]* @return void
     * @author HappyCower
     * @creed: 获取最大的num个节点作为coreNode
     * @date 2022/6/30 15:05
     */

    public static void getMaxCoreNode(int num) {
        GlobalInfo.coreNode.clear();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(GlobalInfo.labelMap.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (int i = 0; i < num; i++) {
            GlobalInfo.coreNode.put(list.get(i).getKey(), list.get(i).getValue());
        }
    }

    /**
     *
     * @param num
     [num]* @return void
     * @author HappyCower
     * @creed: 获取最小的num个节点作为coreNode
     * @date 2022/6/30 15:06
     */

    public static void getMinCoreNode(int num) {
        GlobalInfo.coreNode.clear();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(GlobalInfo.labelMap.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));
        for (int i = 0; i < num; i++) {
            GlobalInfo.coreNode.put(list.get(i).getKey(), list.get(i).getValue());
        }
    }


    /**
     *
     * @param
     * @return void
     * @author HappyCower
     * @creed: 缩小Core的大小
     * @date 2022/7/14 12:55
     */
    public static void updataCoreNode() {
//        float maxQ = (float)Math.pow(GlobalInfo.coreNode.size()*(GlobalInfo.coreNode.size()-1)+GlobalInfo.coreNode.size(),1.0/GlobalVar.theta);
//        float msup = GlobalInfo.topKPatternList.getMinimumItr()/maxQ;
//        GlobalInfo.coreNode.entrySet().removeIf(e -> e.getValue() < msup);
//        System.out.println("UpdataCoreNode:"+GlobalInfo.labelMap.size()+"->"+GlobalInfo.coreNode.size());
    }

    /**
     *
     * @param patternTree
     [patternTree]* @return ArrayList<ArrayList<Pattern>>
     * @author HappyCower
     * @creed: 提前修剪patternTree，防止内存消耗过高
     * @date 2022/7/14 13:44
     */

    public static void cutForwardPatternTree(HashMap<Integer, ArrayList<Pattern>> patternTree){
        patternTree.forEach((key, list) -> {
            list.removeIf(pattern -> pattern.getMaxItr() < GlobalInfo.topKPatternList.getMinimumItr());
//            if(list.size() == 0) patternTree.remove(key);
        });
    }

    public static void cutForwardPatternTree(ConcurrentHashMap<Integer, List<Pattern>> patternTree){
        patternTree.forEach((key, list) -> {
            list.removeIf(pattern -> pattern.getMaxItr() < GlobalInfo.topKPatternList.getMinimumItr());
//            if(list.size() == 0) patternTree.remove(key);
        });
    }

    public static void delIsomorphismInCurrentPatternList(List<Pattern> patterns) {

        patterns.removeIf(p1 -> {
            patterns.stream().anyMatch(p2->{
                VF2GraphIsomorphismInspector<PNode, PEdge> vf = new VF2GraphIsomorphismInspector<>(p1, p2, new PNodeComparator(), new PEdgeComparator(), true);
                return vf.isomorphismExists();
            });
            return false;
        });
    }

    /**
     *
     * @param patternPriorityQueue
     [patternPriorityQueue]* @return void
     * @author HappyCower
     * @creed: 保存前K个模式信息
     * @date 2022/10/19 15:09
     */

    public static void saveKPatternInfo(PatternPriorityQueue patternPriorityQueue) {
//        patternPriorityQueue.saveMNI();

    }

    /**
     *
     * @param pattern
     [pattern]* @return void
     * @author HappyCower
     * @creed: 复制一个pattern的结构信息
     * @date 2022/11/2 20:22
     */

    public static Pattern copyPatterstructure(Pattern pattern) {
        Pattern newPattern = new Pattern(PEdge.class);
        pattern.vertexSet().forEach(newPattern::addVertex);
//        pattern.edgeSet().forEach(pEdge -> newPattern.addEdge(pEdge.getInNode(),pEdge.getOutNode()));
        pattern.edgeSet().forEach(newPattern::addEdge);
        return newPattern;
    }

}
