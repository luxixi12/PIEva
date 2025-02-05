package Base;

import Utils.GlobalVar;
import Utils.Utils;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class Pattern extends DefaultUndirectedGraph<PNode, PEdge> {
    HashMap<String, HashSet<PNode>> patternInfo;
    int edgeSum;
    int realMNI;
    int predictionMNI;
    double intrest;

    PNode tempNode;
    PEdge tempEdge;


    public Pattern(Class<? extends PEdge> edgeClass) {
        super(edgeClass);
        this.patternInfo = new HashMap<>();
        this.edgeSum = 0;
        this.realMNI = -1;
        this.predictionMNI = -1;
    }


    @Override
    public boolean addVertex(PNode pNode) {
        String label = pNode.getLabel();
        if (!patternInfo.containsKey(label)) {
            patternInfo.put(label, new HashSet<>());
        }
        patternInfo.get(label).add(pNode);
        tempNode = pNode;
//        this.patternInfo.merge(pNode.label,1, Integer::sum);
        return super.addVertex(pNode);
    }

    @Override
    public boolean addEdge(PNode sourceVertex, PNode targetVertex, PEdge pEdge) {
        edgeSum++;
        return super.addEdge(sourceVertex, targetVertex, pEdge);
    }

    public boolean addEdge(PEdge pEdge) {
        edgeSum++;
        return super.addEdge(pEdge.getInNode(), pEdge.getOutNode(), pEdge);
    }

    /**
     * @param o pattern
     *          [o]* @return boolean
     * @author HappyCower
     * @creed: 比较两个pattern的点类型是否相同
     * @date 2022/3/22 17:30
     */

    public boolean equalsByPatternInfo(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        HashMap<String, HashSet<PNode>> tempInfo = pattern.getPatternInfo();
        if (pattern.getEdgeSum() != this.getEdgeSum() || pattern.getPatternInfo().keySet().size() != this.patternInfo.keySet().size()) {
//            System.out.println(":1 "+this.getEdgeSum()+" "+pattern.getEdgeSum()+ ", " + pattern.getPatternInfo().keySet().size() + ' ' + this.patternInfo.keySet().size());
            return false;
        }
        for (String label : tempInfo.keySet()) {
            if (!tempInfo.containsKey(label) || !this.getPatternInfo().containsKey(label)) {
                return false;
            }
            if (tempInfo.get(label).size() != this.getPatternInfo().get(label).size()) {
//                System.out.println(":3");
                return false;
            }
        }
        return true;
    }


    //获取MNI
    public int getMNI() {
//        if(this.realMNI==-1) this.realMNI = this.vertexSet().stream().mapToInt(PNode::getInstanceSize).min().getAsInt();
//        return this.realMNI;
        return this.vertexSet().stream().mapToInt(PNode::getInstanceSize).min().getAsInt();
    }

    //获取Q，Q=|V|+|E|
//    public int getQ() {
//        return this.getEdgeSize() + this.getVertexSize();
//    }
    public float getQ() {
//        return (float) Math.sqrt(this.getEdgeSize());
//        return this.getEdgeSize();
        return this.getEdgeSize() + this.getVertexSize();
//        int k = 40;
//        return (float) ((float)this.getEdgeSize()+Math.pow(GlobalInfo.labelNum,2)/k);
    }
    public double getinterest(){
        int size=this.getEdgeSize() + this.getVertexSize();
        int sup = this.getMNI();
        double t1 =1+ Math.pow(2,-size);
        double intres =  Math.sqrt(sup)*(1/t1);
        return intres;
    }

    //    normalizationMNI*Q
    static int k = 100;
    static String mode = GlobalVar.mode;

//        static String mode = "sqrt";
    public float getItr() {
        float ans = 0.0f;
        switch (mode) {
            case "BB":
                ans = this.getMNI();
                break;
            case "AA":
                ans = this.getMNI();
                break;
            case "log":
                double n = ((double) GlobalInfo.labelNum * ((double) GlobalInfo.labelNum - 1) / 2) + (double) GlobalInfo.labelNum;
                ans = (float) (Utils.logn(n, (double) this.getQ())) * this.getMNI();
                break;
            case "sqrt":
                float Q = (float) Math.pow(this.getQ(), 1.0 / GlobalVar.theta);
                ans = Q * this.getMNI();
                break;
            case "normal":
                ans = this.getQ() * this.getMNI();
                break;
            case "sigmod":
                int s=this.getEdgeSize();
                ans = (float) Utils.sigmod(s)*this.getMNI();
                break;
        }
//        float n = (float)Math.pow(GlobalInfo.labelNum,2)/k;
//        return this.getMNI() * (this.getQ() + n);
//        float Q = (float) Math.pow(this.getQ(), 1.0 / GlobalVar.theta);
//        return this.getMNI() * Q;
//        return this.getMNI();
//        return this.getMNI() * this.getQ();
//        return this.getMNI() * this.getQ()*this.getQ();
//        return this.getNormalizationMNI() * (int)(Math.pow(this.getQ(), 2));
//        return this.getNormalizationMNI() * this.getQ();
//        return (float) (Utils.log2((double) this.getMNI()) * Utils.log2((double) this.getQ()));
//        double maxQ = ((double) GlobalInfo.labelNum * ((double) GlobalInfo.labelNum - 1) / 2) + (double) GlobalInfo.labelNum;
//        return (float) (Utils.logn(maxQ, (double) this.getQ())) * this.getMNI();
        return ans;

    }


    //获取前向阶段该模式能达到的最大的Itr，此时Q=labelSize个标签的完全图的大小
    public double getMaxItrInForward() {
//        float n = (float) (Math.pow(GlobalInfo.labelNum, 2) / k);
//        int labelSize = GlobalInfo.labelNum;
//        int labelSize = GlobalInfo.coreNode.size();
//        float labelSize = GlobalInfo.coreNode.size();
//        float maxQ = (float) Math.pow((labelSize * (labelSize - 1) / 2) + labelSize, 1.0 / GlobalVar.theta);
//        float maxQ = (float)Math.sqrt((labelSize * (labelSize - 1) / 2));
//        float maxQ = (labelSize * (labelSize - 1) / 2);
//        return this.getMNI() * ((labelSize * (labelSize - 1) / 2) + labelSize);
//        return this.getMNI() * ((float)(GlobalVar.step * (GlobalVar.step - 1) / 2) + n);
//        return this.getMNI() * maxQ;
//        return this.getMNI();
//        return this.getMNI() * (labelSize * (labelSize - 1) / 2);
//        return this.getMNI() * (GlobalVar.step * (GlobalVar.step - 1) / 2);
//        return this.getMNI() * (int)(Math.pow(labelSize * (labelSize - 1),2) / 4);
//        return this.getNormalizationMNI() * (int)(Math.pow(labelSize * (labelSize - 1),2) / 4);
//        return this.getNormalizationMNI() * (float)(labelSize * (labelSize - 1) / 2);
//        return (float) (Utils.log2((double) this.getMNI()) * Utils.log2((double) (labelSize * (labelSize - 1) / 2)));
//        double maxQ = ((double) GlobalInfo.labelNum*((double) GlobalInfo.labelNum-1)/2) +(double)GlobalInfo.labelNum;
//        return this.getMNI();

        double ans = 0.0f;
        float labelSize = GlobalInfo.coreNode.size();
        float maxQ = (labelSize * (labelSize - 1) / 2) + labelSize;
        switch (mode) {
            case "AA":
                ans = this.getMNI();
                break;
            case "log":
                ans = this.getMNI();
                break;
            case "sqrt":
                ans = (float) (Math.pow(maxQ, 1.0 / GlobalVar.theta) * this.getMNI());
                break;
            case "normal":
                ans = maxQ * this.getMNI();
                break;
            case "sigmod":
//                int s=this.getEdgeSize() + this.getVertexSize();
//                ans = (float)Utils.sigmod(s)*this.getMNI();
                ans = this.getMNI();
                break;
        }
        return ans;
    }

    //获取该模式能达到的最大的Itr，此时Q=V个标签的完全图的大小
    public float getMaxItr() {
//        float n = (float) (Math.pow(GlobalInfo.labelNum, 2) / k);
//        return this.getMNI() * (this.getVertexSize() * (this.getVertexSize() - 1) / 2 + this.getVertexSize());
//        return this.getMNI() * ((this.getVertexSize() * (this.getVertexSize() - 1) / 2 ) + n);
//        float vertexSize = (float) this.getVertexSize();
//        float maxQ = (float) Math.pow(vertexSize * ((vertexSize - 1) / 2) + vertexSize, 1.0 / GlobalVar.theta);
//        double maxQ = ((double) vertexSize * ((double) vertexSize - 1) / 2) + (double) vertexSize;
//        float maxQ = (float) Math.sqrt(labelSize*((labelSize-1)/2));
//        float maxQ = this.getQ()*((this.getQ()-1)/2);
//        return this.getMNI();
//        return this.getMNI() * maxQ;
//        return this.getMNI() * (int)(Math.pow(this.getVertexSize() * (this.getVertexSize() - 1),2) / 4);
//        return this.getNormalizationMNI() * (int)(Math.pow(this.getVertexSize() * (this.getVertexSize() - 1),2) / 4);
//        return this.getNormalizationMNI()* (this.getVertexSize() * (this.getVertexSize() - 1) / 2 );
//        return (float) (Utils.log2((double) this.getMNI()) * Utils.log2((double) (this.getVertexSize() * (this.getVertexSize() - 1) / 2)));
        float ans = 0.0f;
        float labelSize = this.getVertexSize();
        float maxQ = (labelSize * (labelSize - 1) / 2) + labelSize;
        switch (mode) {
            case "log":
                double n = ((double) GlobalInfo.labelNum * ((double) GlobalInfo.labelNum - 1) / 2) + (double) GlobalInfo.labelNum;
                ans = (float) (Utils.logn(n, (double)maxQ)) * this.getMNI();
                break;
            case "AA":
                ans = this.getMNI();
                break;
            case "sqrt":
                ans = (float) (Math.pow(maxQ, 1.0 / GlobalVar.theta) * this.getMNI());
                break;
            case "normal":
                ans = maxQ * this.getMNI();
                break;
            case "sigmod":
                int s=this.getEdgeSize();
                ans = (float)Utils.sigmod(s)*this.getMNI();
        }
        return ans;
    }

    //normalization调整权重
//    float weight = 1.0f;
//    float weight = GlobalVar.step * (GlobalVar.step - 1) / 2;
    float weight = 1.0f;
    float offset = 0.0f;

    public float getNormalizationMNI() {
        return offset + weight * (this.getMNI() - GlobalInfo.minMNIInOneEdge) / (GlobalInfo.maxMNIInOneEdge - GlobalInfo.minMNIInOneEdge);
    }

    public int getVertexSize() {
        return vertexSet().size();
    }

    private int getEdgeSize() {
        return this.edgeSet().size();
    }

    public boolean isExistLabel(String label) {
        return this.patternInfo.containsKey(label);
    }

    public HashMap<String, HashSet<PNode>> getPatternInfo() {
        return patternInfo;
    }

    public void setPatternInfo(HashMap<String, HashSet<PNode>> patternInfo) {
        this.patternInfo = patternInfo;
    }

    public int getRealMNI() {
//        if(this.realMNI==-1) this.realMNI = this.vertexSet().stream().mapToInt(PNode::getInstanceSize).min().getAsInt();
//        return this.realMNI;
        return this.getMNI();
    }

    public void setRealMNI(int realMNI) {
        this.realMNI = realMNI;
    }
    public void setIntrest(double intrest) {
        this.intrest = intrest;
    }
    public int getPredictionMNI() {
        return this.getMNI();
    }

    public void setPredictionMNI(int predictionMNI) {
        this.predictionMNI = predictionMNI;
    }

    public int getEdgeSum() {
        return this.edgeSet().size();
//        return edgeSum;
    }

    public void setEdgeSum(int edgeSum) {
        this.edgeSum = edgeSum;
    }

    public int getMaxNodeWeight() {
        var labelMap = GlobalInfo.labelMap;
        return labelMap.get(this.vertexSet().stream().max(Comparator.comparing(o -> labelMap.get(o.getLabel()))).get().getLabel());
    }

    public PNode getTempNode() {
        return tempNode;
    }

    public void setTempNode(PNode node) {
        this.tempNode = node;
    }

    public PEdge getTempEdge() {
        return tempEdge;
    }

    public void setTempEdge(PEdge tempEdge) {
        this.tempEdge = tempEdge;
    }

    public void draw() {
        Utils.draw(this);
    }

    @Override
    public String toString() {
        if (GlobalVar.PAINTING)
            return "Pattern{" +
                    "patternInfor=" + this.edgeSet() +
                    '}';
        else{
            return this.vertexSet().toString() + ", addNode:" + (this.tempNode != null ? this.tempNode.toString() : "") + '\n';
//            StringBuffer sb = new StringBuffer();
//            this.vertexSet().forEach(pNode -> sb.append(pNode.hashCode()));
//            return sb.toString()+ '\n';
        }
//
    }


}
