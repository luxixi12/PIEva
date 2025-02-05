package Base;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge> {
    @Override
    public int compare(Edge edge1, Edge edge2) {
        String inNodeLabel1 = edge1.getInNode().getLabel();
        String inNodeLabel2 = edge2.getInNode().getLabel();
        String outNodeLabel1 = edge1.getOutNode().getLabel();
        String outNodeLabel2 = edge2.getOutNode().getLabel();
        if((inNodeLabel1.equals(inNodeLabel2)&&outNodeLabel1.equals(outNodeLabel2))
                || inNodeLabel1.equals(outNodeLabel2)&&outNodeLabel1.equals(inNodeLabel2)){
            return 0;
        }
        return 1;
    }
}
