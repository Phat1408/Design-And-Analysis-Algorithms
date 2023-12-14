import java.util.*;

public class Main {
    public static void main(String[] args) {       
        
        // Gnenerate Dataset
        UncertainDataset ud = new UncertainDataset(5, 15, 10);
        ud.gen();
        // System.out.println(ud.UI);
        // System.out.println("Uncertain Dataset UD:");
        // for(int i = 0; i < ud.length; i++){
        //     System.out.println(ud.UD.get(i));
        // }
        // System.out.println("Weight table:");
        // System.out.println(ud.W);
        // System.out.println("--------------------------------");
        // System.out.println("Mean of size-1-candidates:");
        // System.out.println(ud.UI);

        // Generate size 1 candidate and scan themk
        double t = 0.5, alpha = 0.6;
        int msup = (int) Math.round(0.01 * ud.length);
        Apriori<Integer> ap = new Apriori<Integer>(ud.UD, ud.W, ud.UI, msup, alpha, t);
        System.out.println(ap.solve());

        
    }
}
