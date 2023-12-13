import java.util.*;

public class Main {
    public static void main(String[] args) {       
        
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
        Apriori<Integer> ap = new Apriori<Integer>(ud.UD, ud.W, ud.UI, msup, alpha, t);
        System.out.println(ap.solve());

        
    }
}
