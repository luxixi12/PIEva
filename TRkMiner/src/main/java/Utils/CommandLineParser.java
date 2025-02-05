package Utils;

public class CommandLineParser {
    public static void parse(String[] args) {
        for(int i=0;i<args.length;i++) {
            String[] parts = args[i].split("=");
            String key = parts[0];
            String value = parts[1];
            if(key.compareTo("inpath")==0){
                GlobalVar.in_sampling = value;
                GlobalVar.INPUT_PATH = value;
            }
            else if (key.compareTo("outpath")==0)
                GlobalVar.out_sampling = value;
            else if (key.compareTo("k")==0) {
                GlobalVar.K = Integer.parseInt(value);
                GlobalVar.B = GlobalVar.K;
                GlobalVar.MAX_NUM = GlobalVar.K;
            } else if (key.compareTo("pattern")==0) {
                GlobalVar.out_pattern = value;
            } else if (key.compareTo("limitedTime")==0) {
                GlobalVar.limitedTime = Integer.parseInt(value);
            }
        }

    }

}
