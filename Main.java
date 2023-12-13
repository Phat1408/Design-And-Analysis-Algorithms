import java.util.*;

public class Main {
    public static void main(String[] args) {       
        
        // ArrayList<HashMap<String, Double>> UD = new ArrayList<HashMap<String, Double>>(){
        //     {
        //         add(new HashMap<String, Double>(){{put("A", 0.8); put("B", 0.2); put("D", 0.5); put("F", 1.0);}});
        //         add(new HashMap<String, Double>(){{put("B", 0.1); put("C", 0.7); put("D", 1.0); put("E", 1.0); put("G", 0.1);}});
        //         add(new HashMap<String, Double>(){{put("A", 0.5); put("D", 0.2); put("F", 0.5); put("G", 1.0);}});
        //         add(new HashMap<String, Double>(){{put("D", 0.8); put("E", 0.2); put("G", 0.9);}});
        //         add(new HashMap<String, Double>(){{put("C", 1.0); put("D", 0.5); put("F", 0.8); put("G", 1.0);}});
        //         add(new HashMap<String, Double>(){{put("A", 1.0); put("B", 0.2); put("C", 0.1);}});
        //     }
        // };

        // HashMap<String, Double> W = new HashMap<String, Double>(){
        //     {
        //         put("A", 0.6);
        //         put("B", 0.5);
        //         put("C", 0.4);
        //         put("D", 0.3);
        //         put("E", 0.7);
        //         put("F", 0.8);
        //         put("G", 0.1);
        //     }
        // };
        
        // Gnenerate Dataset
        UncertainDataset ud = new UncertainDataset(2, 15, 1000);
        ud.gen();
        // System.out.println(ud.UI);
        // System.out.println("--------------------------------");
        // for(int i = 0; i < ud.length; i++){
        //     System.out.println(ud.UD.get(i));
        // }
        // System.out.println("--------------------------------");


        // Generate size 1 candidate and scan them
        double t = 0.5, alpha = 0.6;
        int msup = (int) Math.round(0.01 * ud.length);
        // double uHat = (double) msup;
        Apriori<Integer> ap = new Apriori<Integer>(ud.UD, ud.W, ud.UI, msup, alpha, t);
        System.out.println(ap.solve());
        // HashSet<HashSet<Integer>> C1 = ap.genSize1WPFI();
        // System.out.println();
        
        // ap.scanFindKItemset(C1, msup, t);
        // HashSet<HashSet<Integer>> C2 = ap.genWPFIApriori(ap.WPFIK, ap.UWPFIK, alpha, ud.length, t, uHat);
        // System.out.println(C2);
        
    }
}
