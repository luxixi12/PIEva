package Base;

import java.util.Comparator;

public class PNodeComparator implements Comparator<PNode> {
    @Override
    public int compare(PNode node1, PNode node2) {
        if (node1.getLabel().equals(node2.getLabel())) {
            return 0;
        }
        return 1;
    }
}
