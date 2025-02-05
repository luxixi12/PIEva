package Base;

import Utils.GlobalVar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class PNode implements Comparator<PNode> {
    String label;
    HashSet<Integer> instanceIDSet;
//    HashSet<Integer> tempSet;

    public PNode(String label, HashSet<Integer> instanceIDSet) {
        this.label = label;
        this.instanceIDSet = instanceIDSet;
//        this.tempSet = new HashSet<>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public HashSet<Integer> getInstanceIDSet() {
        return instanceIDSet;
    }

    public int getInstanceSize() {
        return instanceIDSet.size();
    }

    public void setInstanceIDSet(HashSet<Integer> instanceIDSet) {
        this.instanceIDSet = instanceIDSet;
    }

    public void addInstanceID(int id) {
        this.instanceIDSet.add(id);
    }

//    public HashSet<Integer> getTempSet() {
//        return tempSet;
//    }
//
//    public void setTempSet(HashSet<Integer> tempSet) {
//        this.tempSet = tempSet;
//    }
//
//    public void addTemp(int i) {
//        this.tempSet.add(i);
//    }

//    @Override
//    public String toString() {
//        return "PNode{" +
//                "label='" + label + '\'' +
//                ", IDSet=" + instanceIDSet +
//                '}';
//    }

    @Override
    public String toString() {
        if(GlobalVar.PAINTING) return label + instanceIDSet;
//        else return label+':'+instanceIDSet.size();
        else return label+":"+instanceIDSet.size();
//        else if(instanceIDSet.size()<10) return label+':'+instanceIDSet;
//        else return label+':'+instanceIDSet.size();


    }


    public void delInvaliID(HashSet<Integer> notDelSet) {
        this.getInstanceIDSet().retainAll(notDelSet);
    }

    @Override
    public int compare(PNode node1, PNode node2) {
        if (node1.getLabel().equals(node2.getLabel())) {
            return 0;
        }
        return 1;
    }
}
