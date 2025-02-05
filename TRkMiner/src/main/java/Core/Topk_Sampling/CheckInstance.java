package Core.Topk_Sampling;

import Base.*;
import Utils.Utils;
import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class CheckInstance {
    static Logger logger = Logger.getLogger(CheckInstance.class.getName());

    /**
     * @param G                        Graph
     * @param newPattern               Pattern
     * @param newInNode                PNode
     * @param newOutNode               PNode
     * @param frequentEdgePatternIndex HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>>
     *                                 [G, newPattern, newInNode, newOutNode, frequentEdgePatternIndex]* @return java.lang.Boolean
     * @author HappyCower
     * @creed: 从newInNode出发到newOutNode后向更新Patten中的实例
     * @date 2022/3/29 15:39
     */
    public static Boolean checkInstanceByIndex(Graph G, Pattern newPattern, PNode newInNode, PNode newOutNode, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        if (!verifyTwoNodeInGraph(G, newPattern, newInNode, newOutNode)) {
            return false;
        }
        Stack<PNode> stack = new Stack<>();
        HashSet<PNode> visit = new HashSet<>();
        PNode inNode;
        stack.add(newInNode);
        while (!stack.isEmpty()) {
            inNode = stack.pop();
            visit.add(inNode);
            for (var edge : newPattern.edgesOf(inNode)) {
                try {
                    PNode outNode = edge.opposite(inNode);
                    HashSet<Integer> inNodeInstance = inNode.getInstanceIDSet();
                    String inLabel = inNode.getLabel();
                    HashSet<Integer> outNodeInstance = outNode.getInstanceIDSet();
                    String outLabel = outNode.getLabel();
                    ArrayList<ArrayList<Integer>> tempID = frequentEdgePatternIndex.get(inLabel).get(outLabel);
                    HashMap<Integer, HashSet<Integer>> index = Utils.createIndexFromFirst2Second(tempID.get(0).clone(), tempID.get(1).clone());
                    HashSet<Integer> notDelSet = new HashSet<>();
                    inNodeInstance.forEach(inID -> {
                        if (!index.containsKey(inID)) {
                            return;
                        }
                        notDelSet.addAll((HashSet<Integer>) index.get(inID).clone());
                    });
                    notDelSet.retainAll(outNodeInstance);
                    outNode.setInstanceIDSet(notDelSet);
                    if (!Utils.isFrequent(notDelSet)) {
                        return false;
                    }
                    if (!visit.contains(outNode)) {
                        stack.add(outNode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


    /**
     * @param newPattern               Pattern
     * @param newInNode                PNode
     * @param frequentEdgePatternIndex HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>>
     *                                 [newPattern, newInNode, frequentEdgePatternIndex]* @return java.lang.Boolean
     * @author HappyCower
     * @creed: 从newInNode出发更新Patten中的实例
     * @date 2022/4/9 14:05
     */

    public static Boolean checkInstanceByIndex(Pattern newPattern, PNode newInNode, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex) {
        Stack<PNode> stack = new Stack<>();
        HashSet<PNode> visit = new HashSet<>();
        PNode inNode;
        stack.add(newInNode);
        while (!stack.isEmpty()) {
            inNode = stack.pop();
            visit.add(inNode);
            for (var edge : newPattern.edgesOf(inNode)) {
                try {
                    PNode outNode = edge.opposite(inNode);
                    HashSet<Integer> inNodeInstance = inNode.getInstanceIDSet();
                    String inLabel = inNode.getLabel();
                    HashSet<Integer> outNodeInstance = outNode.getInstanceIDSet();
                    String outLabel = outNode.getLabel();
                    ArrayList<ArrayList<Integer>> tempID = frequentEdgePatternIndex.get(inLabel).get(outLabel);
                    HashMap<Integer, HashSet<Integer>> index = Utils.createIndexFromFirst2Second(tempID.get(0).clone(), tempID.get(1).clone());
                    HashSet<Integer> notDelSet = new HashSet<>();
                    inNodeInstance.forEach(inID -> {
                        if (!index.containsKey(inID)) {
                            return;
                        }
                        notDelSet.addAll((HashSet<Integer>) index.get(inID).clone());
                    });
                    notDelSet.retainAll(outNodeInstance);

                    outNode.setInstanceIDSet(notDelSet);
                    if (!Utils.isFrequent(notDelSet)) {
                        return false;
                    }
                    if (!visit.contains(outNode)) {
                        stack.add(outNode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


    /**
     * @param G          Graph
     * @param newPattern Pattern
     * @param newInNode  PNode
     * @param newOutNode PNode
     *                   [G, newPattern, newInNode, newOutNode]* @return java.lang.Boolean
     * @author HappyCower
     * @creed: 验证newInNode和newOutNode是由后向拓展生成的。
     * @date 2022/3/29 15:40
     */

    private static Boolean verifyTwoNodeInGraph(Graph G, Pattern newPattern, PNode newInNode, PNode newOutNode) {
        DijkstraShortestPath<PNode, PEdge> dijkstra = new DijkstraShortestPath<>(newPattern);
        GraphPath<PNode, PEdge> shortestPath = dijkstra.getPath(newInNode, newOutNode);
        ArrayList<PNode> pathList = (ArrayList<PNode>) shortestPath.getVertexList();
        PNode startNode = shortestPath.getStartVertex();
        HashMap<Integer, Boolean> visit = new HashMap<>();
//        System.out.println("startnode:"+startNode);
        for (Integer id : startNode.getInstanceIDSet()) {
            if (!search(G, pathList, 1, id, id, visit)) {
                visit.put(id, false);
            }else{
                visit.put(id,true);
            }
        }
//        System.out.println("pathlist:"+pathList);
//        System.out.println(newPattern);
//        System.out.println("visit:"+ ","+visit.size());
        for (PNode node : pathList) {
            node.getInstanceIDSet().removeIf(id -> !visit.containsKey(id) || !visit.get(id));
        }
//        System.out.println(":"+newPattern);
        if (Utils.isFrequent(newPattern.getMNI())) {
            return true;
        }
        return false;
    }


    /**
     * @param G         Graph
     * @param pathList  ArrayList<PNode>
     * @param nodeIndex int
     * @param id        int
     * @param startID   int
     *                  [G, pathList, nodeIndex, id, startIndex]* @return java.lang.Boolean
     * @author HappyCower
     * @creed: 查看pathList的起点和终点是否成环的递归方法
     * @date 2022/3/29 15:41
     */

    private static Boolean search(Graph G, ArrayList<PNode> pathList, int nodeIndex, int id, int startID, HashMap<Integer, Boolean> visit) {
        if (pathList.size() == nodeIndex) {
            return G.containsEdge(G.getInstanceByID(id), G.getInstanceByID(startID));
        }
        boolean flag = true;
        for (Integer i : pathList.get(nodeIndex).getInstanceIDSet()) {
            if (G.containsEdge(G.getInstanceByID(i), G.getInstanceByID(id)) && search(G, pathList, nodeIndex + 1, i, startID, visit)) {
                visit.put(i, true);
                flag = false;
            }
        }
        if (flag) {
            if (!visit.containsKey(id)) {
                visit.put(id, false);
            }
            return false;
        } else {
            return true;
        }
    }


    /**
     * @param G       图
     * @param pattern 模式
     * @param pNode   新扩展的点
     * @param tempSet 新扩展的点对应与原模式相连的实例id
     *                [G, pattern, pNode, tempSet]* @return Base.Pattern
     * @author HappyCower
     * @creed: 从图更新当前模式加入新的点以后每个点所对应的实例
     * @date 2022/3/19 20:20
     */
    public static void checkInstanceByGraph(Graph G, Pattern pattern, PNode pNode, HashSet<Integer> tempSet) {
        HashSet<Integer> nowInstanceIDSet = pNode.getInstanceIDSet();
        //遍历新插入节点的边，对于前向拓展实际上只有一条新加入的边
        pattern.edgesOf(pNode).forEach(nearEdge -> {
            PNode node;
            try {
                //node表示拓展新点的那个点
                node = nearEdge.opposite(pNode);
                HashSet<Integer> tempLastInstanceIDSet;
                // 判断原来的点的实例是否和更新后的实例数量相等
                if (tempSet.size() != node.getInstanceIDSet().size()) {
                    tempLastInstanceIDSet = nowInstanceIDSet;
                    //从node出发遍历整个pattern
                    for (BreadthFirstIterator<PNode, PEdge> it = new BreadthFirstIterator<>(pattern, node); it.hasNext(); ) {
                        PNode sNode = it.next();
                        if (sNode.equals(pNode)) {
                            continue;
                        }
                        HashSet<Integer> finalTempLastInstanceIDSet = tempLastInstanceIDSet;
                        // 验证当前的sNode的每一个实例是否满足新的pattern
                        sNode.getInstanceIDSet().forEach(id -> {
                            Node nodeInstance = G.getInstanceByID(id);
                            // 去原图里面验证instance是否相连
                            G.edgesOf(nodeInstance).forEach(_nearEdge -> {
                                try {
                                    Node _node = _nearEdge.opposite(nodeInstance);
                                    if (finalTempLastInstanceIDSet.contains(_node.getId())) {
                                        tempSet.add(id);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        });
                        if (tempSet.size() != sNode.getInstanceIDSet().size()) {
                            sNode.delInvaliID(tempSet);
                        }
                        tempSet.clear();
                        tempLastInstanceIDSet = sNode.getInstanceIDSet();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean checkInstanceByIndex(Graph G, Pattern newPattern, PNode newInNode, PNode newOutNode, HashMap<String, HashMap<String, ArrayList<ArrayList<Integer>>>> frequentEdgePatternIndex, PEdge newEdge) {
        newPattern.clone();

        newPattern.removeEdge(newEdge);
//        System.out.println("newPattern start:"+newPattern.vertexSet());
        if (!verifyTwoNodeInGraph(G, newPattern, newInNode, newOutNode)) {
            newPattern.addEdge(newInNode, newOutNode, newEdge);
            return false;
        }
//        System.out.println("---");
//        System.out.println("newPattern end:"+newPattern.vertexSet());
        DijkstraShortestPath<PNode, PEdge> dijkstra = new DijkstraShortestPath<>(newPattern);
        GraphPath<PNode, PEdge> shortestPath = dijkstra.getPath(newInNode, newOutNode);
        ArrayList<PNode> pathList = (ArrayList<PNode>) shortestPath.getVertexList();
//        System.out.println(pathList.size()+" "+newPattern.vertexSet().size());

        Stack<PNode> stack = new Stack<>();
        HashSet<PNode> visit = new HashSet<>();
        PNode inNode;
//        stack.add(newInNode);

        pathList.forEach(node -> {
            stack.add(node);
            visit.add(node);
        });

        while (!stack.isEmpty()) {
            inNode = stack.pop();
            visit.add(inNode);
            for (var edge : newPattern.edgesOf(inNode)) {
                try {
                    PNode outNode = edge.opposite(inNode);
                    if (visit.contains(outNode)) {
                        continue;
                    }
                    HashSet<Integer> inNodeInstance = inNode.getInstanceIDSet();
                    String inLabel = inNode.getLabel();
                    HashSet<Integer> outNodeInstance = outNode.getInstanceIDSet();
                    String outLabel = outNode.getLabel();
                    ArrayList<ArrayList<Integer>> tempID = frequentEdgePatternIndex.get(inLabel).get(outLabel);
                    HashMap<Integer, HashSet<Integer>> index = Utils.createIndexFromFirst2Second(tempID.get(0).clone(), tempID.get(1).clone());
                    HashSet<Integer> notDelSet = new HashSet<>();
                    inNodeInstance.forEach(inID -> {
                        if (!index.containsKey(inID)) {
                            return;
                        }
                        notDelSet.addAll((HashSet<Integer>) index.get(inID).clone());
                    });
                    notDelSet.retainAll(outNodeInstance);
//                    System.out.println("start:"+outNode.getInstanceSize());
                    outNode.setInstanceIDSet(notDelSet);
//                    System.out.println("emd:"+outNode.getInstanceSize());
                    if (!Utils.isFrequent(notDelSet)) {
                        newPattern.addEdge(newInNode, newOutNode, newEdge);
                        return false;
                    }
                    if (!visit.contains(outNode)) {
                        stack.add(outNode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        newPattern.addEdge(newInNode, newOutNode, newEdge);
        return true;
    }
}
