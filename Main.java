import java.util.*;

public class Main {
    public static void main(String[] args) {       
        
        // HashSet<Integer> test = new HashSet<Integer>();
        // test.add(1);
        // test.add(2);
        // test.add(3);

        // Itemset<Integer> itemset = new Itemset<Integer>(test);
        // System.out.println(itemset.getItems());

        HashMap<Integer, Double> W = new HashMap<Integer, Double>();
        W.put(1, 0.8);
        W.put(2, 0.5);
        W.put(5, 0.7);

        HashSet<Integer> itemset = new HashSet<Integer>();
        itemset.add(1);
        itemset.add(2);
        
        ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        System.out.println(Double.compare(-1.0, -1.00000000000));


        // Transaction<Integer> trans = new Transaction<Integer>(hm);
        // System.out.println(trans.getItemsAndProbabilities());
        // System.out.println(hm.get(1));

        // HashSet<Integer> hs = new HashSet<Integer>();
        // hs.add(1);
        // hs.add(2);
        // hs.add(3);
        // for(Integer item : hs){
        //     System.out.println(item);
        // }

    }
}
