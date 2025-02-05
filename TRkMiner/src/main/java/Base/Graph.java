package Base;

import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.HashMap;
import java.util.function.Supplier;

public class Graph extends DefaultUndirectedGraph<Node, Edge> {

    private HashMap<Integer, Node> nodeSet;

    public String temp;

    public Graph(Class<? extends Edge> edgeClass) {
        super(edgeClass);
        this.nodeSet = new HashMap<>();
    }

    public void addNode(Node node) {
        this.nodeSet.put(node.getId(), node);
        this.addVertex(node);
    }

    public Integer getNodeSize() {
        return this.nodeSet.size();
    }

    public Node getInstanceByID(int id) {
        return this.nodeSet.get(id);
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodeSet=" + this.vertexSet() +
                '}';
    }
}
