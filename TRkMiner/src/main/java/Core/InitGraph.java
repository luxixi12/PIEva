package Core;

import Base.*;
import Utils.GlobalVar;
import org.apache.log4j.Logger;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.ujmp.core.io.ImportMatrixMAT;
import org.ujmp.core.Matrix;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class InitGraph {
    static Logger logger = Logger.getLogger(InitGraph.class.getName());

    /**
     * @return Base.Graph
     * @author HappyCower
     * @creed: 新建一个自定义图
     * @date 2022/3/24 15:28
     */

    public Graph getUserGraph() {
        Graph graph = new Graph(Edge.class);

        Node node0 = new Node(graph.getNodeSize(), "IR");

        graph.addNode(node0);

        Node node1 = new Node(graph.getNodeSize(), "DB");
        graph.addNode(node1);
        graph.addEdge(node0, node1, new Edge(node0, node1));

        Node node2 = new Node(graph.getNodeSize(), "DM");
        graph.addNode(node2);
        graph.addEdge(node1, node2, new Edge(node1, node2));

        Node node3 = new Node(graph.getNodeSize(), "IR");
        graph.addNode(node3);
        graph.addEdge(node1, node3, new Edge(node1, node3));
        graph.addEdge(node2, node3, new Edge(node2, node3));

        Node node4 = new Node(graph.getNodeSize(), "IR");
        graph.addNode(node4);
        graph.addEdge(node3, node4, new Edge(node3, node4));

        Node node5 = new Node(graph.getNodeSize(), "DB");
        graph.addNode(node5);
        graph.addEdge(node4, node5, new Edge(node4, node5));

        Node node6 = new Node(graph.getNodeSize(), "DB");
        graph.addNode(node6);
        graph.addEdge(node5, node6, new Edge(node5, node6));

        Node node7 = new Node(graph.getNodeSize(), "DM");
        graph.addNode(node7);
        graph.addEdge(node5, node7, new Edge(node5, node7));
        graph.addEdge(node6, node7, new Edge(node6, node7));

        Node node8 = new Node(graph.getNodeSize(), "IR");
        graph.addNode(node8);
        graph.addEdge(node7, node8, new Edge(node7, node8));
        graph.addEdge(node6, node8, new Edge(node6, node8));
        graph.addEdge(node5, node8, new Edge(node5, node8));

        Node node9 = new Node(graph.getNodeSize(), "IR");
        graph.addNode(node9);
        graph.addEdge(node9, node8, new Edge(node9, node8));

        System.out.println("已成功创建图：" + graph);
//        DepthFirstIterator it = new DepthFirstIterator(graph);
//        while (it.hasNext()){
//            System.out.println(it.next());
//        }

        return graph;
    }


    public Graph getGraphFromPath(String filePath) throws IOException {
        final BufferedReader rows = new BufferedReader(new FileReader(new File(filePath)));
        String line;
        Graph G = new Graph(Edge.class);
        rows.readLine();
        int id, id1, id2;
        String label;
        while ((line = rows.readLine()) != null && (line.charAt(0) == 'v')) {
            String[] parts = line.split("\\s+"); //按空白字符切分
            id = Integer.parseInt(parts[1]);
            label = parts[2];
            G.addNode(new Node(id, label));
        }
        while (line != null && ((line.charAt(0) == 'e'))) {
            String[] parts = line.split("\\s+"); //按空白字符切分
            id1 = Integer.parseInt(parts[1]);
            id2 = Integer.parseInt(parts[2]);
            Node node1 = G.getInstanceByID(id1);
            Node node2 = G.getInstanceByID(id2);
            if(!G.containsEdge(node1, node2)){
                G.addEdge(node1, node2, new Edge(node1, node2));
            }

            line = rows.readLine();
        }
        rows.close();
        System.out.println("read:" + filePath + " Graph");
        return G;
    }


    public static Graph getGraphFromCSVFile(String filePath) throws IOException {
        Graph G = new Graph(Edge.class);
        int id, id1, id2;
        String label;
        File nodeCSV = new File(filePath + "/node.csv");
        File edgeCSV = new File(filePath + "/edges.csv");
        File write = new File("newGraph.lg");
        if (!write.exists()) {
            write.createNewFile();
        }
        FileWriter fileWritter = new FileWriter(write.getName(), true);
        BufferedWriter writter = new BufferedWriter(fileWritter);
        String data;
        try {
            BufferedReader textFile = new BufferedReader(new FileReader(nodeCSV));
            String line;
            line = textFile.readLine();
            while ((line = textFile.readLine()) != null) {
                String[] parts = line.split(",");
                id = Integer.parseInt(parts[0]);
                label = parts[1];
                G.addNode(new Node(id, label));

                data = "v " + parts[0] + " " + parts[1] + "\r\n";
                writter.write(data);
            }
            textFile.close();
            writter.flush();
            textFile = new BufferedReader(new FileReader(edgeCSV));
            line = textFile.readLine();
            while ((line = textFile.readLine()) != null) {
                String[] parts = line.split(",");
                id1 = Integer.parseInt(parts[0]);
                id2 = Integer.parseInt(parts[1]);
                Node node1 = G.getInstanceByID(id1);
                Node node2 = G.getInstanceByID(id2);
                G.addEdge(node1, node2, new Edge(node1, node2));

                data = "e " + parts[0] + " " + parts[1] + " 0\r\n";
                writter.write(data);
            }

            textFile.close();
            writter.flush();
            writter.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
        System.out.println("从CSV创建图形成功");
        return G;
    }


    public static Graph getGraphFromMATFile(String filePath) {
        Graph G = new Graph(Edge.class);
        File file = new File(filePath);
        Matrix data = ImportMatrixMAT.fromFile(file);
        return G;
    }
}
