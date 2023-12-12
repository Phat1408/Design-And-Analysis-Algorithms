import java.util.*;

public class Main {
    public static void main(String[] args) {       
        
        ArrayList<HashMap<Integer, Double>> UD = new ArrayList<HashMap<Integer, Double>>(){
            {
                add(new HashMap<Integer, Double>(){{put(1, 0.4); put(2, 1.0); put(3, 0.3);}});
                add(new HashMap<Integer, Double>(){{put(1, 1.0); put(2, 0.8);}});
            }
        };

        HashMap<Integer, Double> W = new HashMap<Integer, Double>(){
            {
                put(1, 0.6);
                put(2, 0.8);
                put(3, 0.4);
            }
        };

        Apriori<Integer> ap = new Apriori<Integer>(UD, W);
        HashSet<Integer> X1 = new HashSet<Integer>(){
            {add(1); add(3); add(2);}
        };
        HashSet<Integer> X2 = new HashSet<Integer>(){
            {add(3); add(2); add(4);}
        };

        // System.out.println(new HashSet<Integer>(){{addAll(X1); addAll(X2);}});
        // double wX = ap.getWeight(X);
        // System.out.println(wX);
        // System.out.println(Arrays.toString(ap.probXInUD(X, 1, 0.6, wX)));

    }
}
