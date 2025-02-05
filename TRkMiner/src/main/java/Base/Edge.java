package Base;

import org.jgrapht.graph.DefaultEdge;

public class Edge extends DefaultEdge {
    Node inNode;
    Node outNode;

    public Edge(Node inNode, Node outNode) {
        this.inNode = inNode;
        this.outNode = outNode;
    }

//    @Override
//    public String toString() {
//        return "Edge{" +
//                "inNode=" + inNode +
//                ", outNode=" + outNode +
//                '}';
//    }

    @Override
    public String toString() {
        return "";
    }


    public Node getInNode() {
        return inNode;
    }

    public void setInNode(Node inNode) {
        this.inNode = inNode;
    }

    public Node getOutNode() {
        return outNode;
    }

    public void setOutNode(Node outNode) {
        this.outNode = outNode;
    }


    public Node opposite(Node node) throws Exception {
        if (node.equals(inNode)){
            return this.outNode;
        }else if(node.equals(outNode)){
            return this.inNode;
        }else{
            throw new Exception("当前Edge中不存在该Node");
        }
    }
}
