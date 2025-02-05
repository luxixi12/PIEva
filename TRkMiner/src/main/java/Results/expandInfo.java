package Results;

import Base.PNode;

import java.util.ArrayList;

public class expandInfo {
    PNode inNode;
    PNode outNode;
    ArrayList<Integer> instanceList;
    public expandInfo(PNode inNode, PNode outNode, ArrayList<Integer> instanceList){
        this.inNode = inNode;
        this.outNode = outNode;
        this.instanceList = instanceList;
    }


}
