import java.util.Arrays;

public class Main {
    public static void main(String[] args) {       
        
        // Generate Dataset
        UncertainDataset ud = new UncertainDataset(3, 20, 30);
        ud.gen();
        System.out.println(ud.UD);
        System.out.println();
        System.out.println(ud.W);
        System.out.println();
        System.out.println(ud.UI);

        double t = 0.5, alpha = 0.2;
        int msup = (int) Math.round(0.1 * ud.UD.size());
        Apriori<Integer> ap = new Apriori<Integer>(ud.UD, ud.W, ud.UI, msup, alpha, t);
        System.out.println(ap.solve());
    }
}
