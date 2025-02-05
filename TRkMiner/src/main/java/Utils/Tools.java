package Utils;

import Base.*;
import Core.TopRankK;
import org.apache.log4j.Logger;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Tools {
    static Logger logger = Logger.getLogger(Tools.class.getName());

    /**
     * @param patternTree ArrayList<ArrayList<Pattern>>
     *                    [patternTree]* @return void
     * @author HappyCower
     * @creed: 保存patternTree
     * @date 2022/3/29 15:49
     */

    public static void savePatternTree(ArrayList<ArrayList<Pattern>> patternTree) {
        patternTree.forEach(patterns -> {
            patterns.forEach(pattern -> {
                try {
                    Tools.outputPattern(pattern);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * @param pattern Pattern
     *                [pattern]* @return void
     * @author HappyCower
     * @creed: 将pattern序列化保存。
     * @date 2022/3/29 15:37
     */

    public static void outputPattern(Pattern pattern) throws IOException {
        String fileName = "out.lg";
//        File write = new File(GlobalVar.OUTPUTFILEPATH+fileName);
        File write = new File(fileName);
        if (!write.exists()) {
            write.createNewFile();
        }
        FileWriter fileWritter = new FileWriter(write.getName(), true);
        BufferedWriter writter = new BufferedWriter(fileWritter);
        String data;
        HashMap<PNode, Integer> hm = new HashMap<>();
        try {
            writter.write(GlobalVar.nums++ + ":\r\n");
            writter.flush();
            int id = 0;
            for (PNode pNode : pattern.vertexSet()) {
                hm.put(pNode, id);
                data = "v " + id + " " + pNode.getLabel() + "\r\n";
                writter.write(data);
                id++;
            }
            writter.flush();
            for (PEdge edge : pattern.edgeSet()) {
                data = "e " + hm.get(edge.getInNode()) + " " + hm.get(edge.getOutNode()) + " 0\r\n";
                writter.write(data);
            }
            writter.flush();
            writter.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }

    public static void outputPattern(Pattern pattern, String fileName) throws IOException {
//        String fileName = "out.lg";
//        File write = new File(GlobalVar.OUTPUTFILEPATH+fileName);
//        File write = new File(fileName);
//        if (!write.exists()) {
//            write.createNewFile();
//        }
        FileWriter fileWritter = new FileWriter(fileName, true);
        BufferedWriter writter = new BufferedWriter(fileWritter);
        String data;
        HashMap<PNode, Integer> hm = new HashMap<>();
        try {
//            writter.write(GlobalVar.nums++ + ":\r\n");
            writter.flush();
            int id = 0;
            for (PNode pNode : pattern.vertexSet()) {
                hm.put(pNode, id);
                data = "v " + id + " " + pNode.getLabel() + "\r\n";
                writter.write(data);
                id++;
            }
            writter.flush();
            for (PEdge edge : pattern.edgeSet()) {
                data = "e " + hm.get(edge.getInNode()) + " " + hm.get(edge.getOutNode()) + " 0\r\n";
                writter.write(data);
            }
            writter.flush();
            writter.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }

    public static void outputPatternsWithMNI(ArrayList<Pattern> patterns, String fileName) throws IOException {
//        String fileName = "out.lg";
//        File write = new File(GlobalVar.OUTPUTFILEPATH+fileName);
//        File write = new File(fileName);
//        if (!write.exists()) {
//            write.createNewFile();
//        }
        FileWriter fileWritter = new FileWriter(fileName);
        BufferedWriter writter = new BufferedWriter(fileWritter);
        HashMap<PNode, Integer> hm = new HashMap<>();
//        int ix=0;
        patterns.forEach(pattern -> {
            try {
                StringBuilder data;
//            writter.write(GlobalVar.nums++ + ":\r\n");
                int daxiao=pattern.getVertexSize()+pattern.getEdgeSum();
                double intrs=pattern.getinterest();
//                writter.write(pattern.getMNI() + ":\r\n");
                writter.write(GlobalVar.nums++ + ":"+"支持度"+pattern.getMNI() +"模式大小"+daxiao+"兴趣度"+intrs+ "\n");
//                  writter.write(ix++ + ":\r\n");
                int id = 0;
                for (PNode pNode : pattern.vertexSet()) {
                    hm.put(pNode, id);
                    data = new StringBuilder("v " + id + " " + pNode.getLabel()+"\r\n");
//                    for (int ID : pNode.getInstanceIDSet()) {
//                        data.append(' ');
//                        data.append(ID);
//                    }
//                    data.append("\r\n");
                    writter.write(data.toString());
                    writter.flush();
//                    writter.write(data);
                    id++;
                }
                for (PEdge edge : pattern.edgeSet()) {
                    data = new StringBuilder("e " + hm.get(edge.getInNode()) + " " + hm.get(edge.getOutNode()) + " 1\r\n");
                    writter.write(data.toString());
                }
                writter.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        writter.close();
    }


    public static void outputPatternsWithITR(ArrayList<Pattern> patterns, String fileName) throws IOException {
//        String fileName = "out.lg";
//        File write = new File(GlobalVar.OUTPUTFILEPATH+fileName);
//        File write = new File(fileName);
//        if (!write.exists()) {
//            write.createNewFile();
//        }
        FileWriter fileWritter = new FileWriter(fileName);
        BufferedWriter writter = new BufferedWriter(fileWritter);
        HashMap<PNode, Integer> hm = new HashMap<>();

        patterns.forEach(pattern -> {
            try {
                StringBuilder data;
//            writter.write(GlobalVar.nums++ + ":\r\n");
                writter.write(pattern.getItr() + ":\r\n");
                int id = 0;
                for (PNode pNode : pattern.vertexSet()) {
                    hm.put(pNode, id);
                    data = new StringBuilder("v " + id + " " + pNode.getLabel());
//                    for (int ID :pNode.getInstanceIDSet()){
//                        data.append(' ');
//                        data.append(ID);
//                    }
                    data.append("\r\n");
                    writter.write(data.toString());
                    id++;
                }
                for (PEdge edge : pattern.edgeSet()) {
                    data = new StringBuilder("e " + hm.get(edge.getInNode()) + " " + hm.get(edge.getOutNode()) + " 0\r\n");
                    writter.write(data.toString());
                }
                writter.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        writter.close();
    }


    /**
     * @param filePath String
     * @param draw     Bool
     *                 [filePath, draw]* @return java.util.ArrayList<Base.Graph>
     * @author HappyCower
     * @creed: 测试pattern，统计文件中的模式个数，并且可以输出没有同构模式的模式。
     * mode==true:会统计出没有同构的模式
     * mode==false：会统计所有的模式，不带同构
     * @date 2022/3/29 15:34
     */
    public static ArrayList<Graph> patternTest(String filePath, Boolean mode, Boolean draw) throws IOException {
        final BufferedReader rows = new BufferedReader(new FileReader(new File(filePath)));
        String line;
        ArrayList<Graph> patterns = new ArrayList<>();
        Graph G = new Graph(Edge.class);
        int id, id1, id2;
        String label;
        boolean flag;
        int temp = 0;
        while ((line = rows.readLine()) != null) {
            String[] parts = line.split("\\s+");
            String str = parts[parts.length - 1];
            if (str.charAt(str.length() - 1) == ':' && G.vertexSet().size() != 0) {
                flag = true;
//                if (draw) Utils.draw(G,G.temp);
                for (int i = 0; i < patterns.size(); i++) {
                    Graph g = patterns.get(i);
                    VF2GraphIsomorphismInspector<Node, Edge> vf = new VF2GraphIsomorphismInspector<>(g, G, new NodeComparator(), new EdgeComparator(), true);
                    if (vf.isomorphismExists()) {
                        flag = false;
                        temp = i;
                        break;
                    }
                }
//                if (flag) patterns.add(G);
//                else if (mode) patterns.remove(temp);
                if (flag) patterns.add(G);
                else if (mode) patterns.remove(temp);
                G = new Graph(Edge.class);
                G.temp = str;
            }
            if (parts[0].equals("v")) {
                id = Integer.parseInt(parts[1]);
                label = parts[2];
                G.addNode(new Node(id, label));
            }
            if (parts[0].equals("e")) {
                id1 = Integer.parseInt(parts[1]);
                id2 = Integer.parseInt(parts[2]);
                Node node1 = G.getInstanceByID(id1);
                Node node2 = G.getInstanceByID(id2);
                G.addEdge(node1, node2, new Edge(node1, node2));
            }
        }
        flag = true;
        for (int i = 0; i < patterns.size(); i++) {
            Graph g = patterns.get(i);
            VF2GraphIsomorphismInspector<Node, Edge> vf = new VF2GraphIsomorphismInspector<>(g, G, new NodeComparator(), new EdgeComparator(), true);
            if (vf.isomorphismExists()) {
                flag = false;
                temp = i;
                break;
            }
        }
        rows.close();
        if (flag) patterns.add(G);
        else if (mode) patterns.remove(temp);


        if (draw) patterns.forEach(g -> Utils.draw(g, g.temp));
        System.out.println("当前的pattern数量：" + patterns.size());
        return patterns;
    }


    /**
     * @param arr ArrayList<Integer>
     *            [arr]* @return void
     * @author HappyCower
     * @creed: 将一个数组序列化
     * @date 2022/3/29 15:22
     */

    public static void saveArr(ArrayList<Integer> arr) throws IOException {
        String fileName = "arr.txt";
//        File write = new File(GlobalVar.OUTPUTFILEPATH+fileName);
        File write = new File(fileName);
        if (!write.exists()) {
            write.createNewFile();
        }
        FileWriter fileWritter = new FileWriter(write.getName(), true);
        BufferedWriter writter = new BufferedWriter(fileWritter);
        arr.forEach(i -> {
            try {
                writter.write(String.valueOf(i) + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writter.flush();
        writter.close();
        System.out.println("saveArr Done!");
    }


    /**
     * @param path
     * @param nums [path, nums]* @return void
     * @author HappyCower
     * @creed: 利用高斯分布打标签
     * @date 2022/6/23 15:19
     */

    public static void addLableByGaussion(String path, int nums) throws IOException {
        int avg = 50; //均值
        float variance = 25f; //方差
//        int nums = 5; //数量
        final BufferedReader rows = new BufferedReader(new FileReader(path));
        String line;
        rows.readLine();

        String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        File write = new File(name + "_" + nums + "_" + avg + "_" + variance + ".lg");
        if (!write.exists()) {
            write.createNewFile();
        }
        FileWriter fileWritter = new FileWriter(write.getName(), true);
        BufferedWriter writter = new BufferedWriter(fileWritter);
        writter.write("# t 1\r\n");
        Integer label;
        Random r = new Random();
        while ((line = rows.readLine()) != null && (line.charAt(0) == 'v')) {
            String[] parts = line.split("\\s+"); //按空白字符切分
            label = -1;
            while (label == -1 || !(label >= avg - (nums / 2) && label <= avg + (nums / 2))) {
                label = (int) (Math.sqrt(variance) * r.nextGaussian() + avg); //均值为avg， 方差为variance的正态分布
            }
//            System.out.println(label);
            parts[parts.length - 1] = String.valueOf(label);
            writter.write(String.join(" ", parts) + "\r\n");
        }
        writter.flush();
        while (line != null && ((line.charAt(0) == 'e'))) {
            writter.write(line + "\r\n");
            line = rows.readLine();
        }
        rows.close();
        writter.flush();
        writter.close();
        System.out.println(path + " addLableByGaussion Done!");
    }


    public static void writeCSV(String csv, String name, Integer k, double time, double mem,String s,int m) throws IOException {
//        File write = new File(csv);
//        if (!write.exists()) {
//            write.createNewFile();
//        }
        FileWriter fileWritter = new FileWriter(csv, true);
        BufferedWriter writter = new BufferedWriter(fileWritter);

        writter.write(name + ',' + k + ',' + time + ',' + mem +',' + s+ m+ "\r\n");

        writter.flush();
        writter.close();

    }
}
