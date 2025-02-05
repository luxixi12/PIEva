package Base;

import Utils.GlobalVar;
import org.jgrapht.graph.DefaultEdge;

import java.util.Comparator;

public class PEdge extends DefaultEdge implements Comparator<PEdge> {
    PNode inNode;
    PNode outNode;

    public PEdge(PNode inNode, PNode outNode) {
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
        if (GlobalVar.PAINTING) {
            return "Edge{" +
                    "inNode=" + inNode +
                    ", outNode=" + outNode +
                    '}';
        } else {
            return inNode+","+outNode;
        }

    }

    public PNode getInNode() {
        return inNode;
    }

    public void setInNode(PNode inNode) {
        this.inNode = inNode;
    }

    public PNode getOutNode() {
        return outNode;
    }

    public void setOutNode(PNode outNode) {
        this.outNode = outNode;
    }


    /**
     * @param pNode PNode
     *              [pNode]* @return Base.PNode
     * @author HappyCower
     * @creed: 获取当前边上所给node的另一边的点
     * @date 2022/3/24 14:53
     */

    public PNode opposite(PNode pNode) throws Exception {
        if (pNode.equals(inNode)) {
            return this.outNode;
        } else if (pNode.equals(outNode)) {
            return this.inNode;
        } else {
            throw new Exception("当前Edge中不存在该Node");
        }
    }

    @Override
    public int compare(PEdge edge1, PEdge edge2) {
        String inNodeLabel1, inNodeLabel2, outNodeLabel1, outNodeLabel2;
        inNodeLabel1 = edge1.getInNode().getLabel();
        inNodeLabel2 = edge2.getInNode().getLabel();
        outNodeLabel1 = edge1.getOutNode().getLabel();
        outNodeLabel2 = edge2.getOutNode().getLabel();
        if ((inNodeLabel1.equals(inNodeLabel2) && outNodeLabel1.equals(outNodeLabel2))
                || inNodeLabel1.equals(outNodeLabel2) && outNodeLabel1.equals(inNodeLabel2)) {
            return 0;
        }
        return 1;
    }
}
