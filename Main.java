import java.util.*;

public class Main {
    public static void main(String[] args) {       
        
        ArrayList<HashMap<String, Double>> UD = new ArrayList<HashMap<String, Double>>(){
            {
                add(new HashMap<String, Double>(){{put("A", 0.8); put("B", 0.2); put("D", 0.5); put("F", 1.0);}});
                add(new HashMap<String, Double>(){{put("B", 0.1); put("C", 0.7); put("D", 1.0); put("E", 1.0); put("G", 0.1);}});
                add(new HashMap<String, Double>(){{put("A", 0.5); put("D", 0.2); put("F", 0.5); put("G", 1.0);}});
                add(new HashMap<String, Double>(){{put("D", 0.8); put("E", 0.2); put("G", 0.9);}});
                add(new HashMap<String, Double>(){{put("C", 1.0); put("D", 0.5); put("F", 0.8); put("G", 1.0);}});
                add(new HashMap<String, Double>(){{put("A", 1.0); put("B", 0.2); put("C", 0.1);}});
            }
        };

        HashMap<String, Double> W = new HashMap<String, Double>(){
            {
                put("A", 0.6);
                put("B", 0.5);
                put("C", 0.4);
                put("D", 0.3);
                put("E", 0.7);
                put("F", 0.8);
                put("G", 0.1);
            }
        };

        Apriori<String> ap = new Apriori<String>(UD, W);

        // HashSet<Integer> X1 = new HashSet<Integer>(){
        //     {add(4); add(3); add(2);}
        // };
        // HashSet<Integer> X2 = new HashSet<Integer>(){
        //     {add(3); add(2); add(4);}
        // };

        // HashSet<HashSet<Integer>> WPFI = new HashSet<>(){{add(X1); add(X2);}};

        // for(HashSet<Integer> X: WPFI){
        //     System.out.println(X);
        // }
        
        HashSet<HashSet<String>> size1WPFI = ap.genSize1WPFI(ap.itemsInUD());
        System.out.println(ap.scanFindKItemset(size1WPFI, 2, 0.2));

        // System.out.println(new HashSet<Integer>(){{addAll(X1); addAll(X2);}});
        // double wX = ap.getWeight(X);
        // System.out.println(wX);
        // System.out.println(Arrays.toString(ap.probXInUD(X, 1, 0.6, wX)));

    }
}
