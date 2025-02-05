package Base;

public class Node {
    int id;
    String label;

    public Node(int id, String label){
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

//    @Override
//    public String toString() {
//        return "Node{" +
//                "id=" + this.id +
//                ", label=" + this.label +
//                '}';
//    }

    @Override
    public String toString() {
        return "id=" + this.id +
                ",label=" + this.label;
    }
}
