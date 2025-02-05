package Base;

import java.util.Comparator;

public class PEdgeComparator implements Comparator<PEdge> {
    @Override
    public int compare(PEdge edge1, PEdge edge2) {
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
