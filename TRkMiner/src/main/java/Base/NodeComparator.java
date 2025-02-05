package Base;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node node1, Node node2) {
        if (node1.getLabel().equals(node2.getLabel())) {
            return 0;
        }
        return 1;
    }
}
