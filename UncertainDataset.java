import java.util.*;

public class UncertainDataset {
    int minSizeTransaction, maxSizeTransaction, length;
    ArrayList<HashMap<Integer, Double>> UD = new ArrayList<HashMap<Integer, Double>>();
    HashMap<Integer, Double> W = new HashMap<Integer, Double>();
    HashMap<Object, Double> UI = new HashMap<Object, Double>();

    public UncertainDataset(){
        this.minSizeTransaction = 10;
        this.maxSizeTransaction = 20;
        this.length = 1000;
    }

    public UncertainDataset(int minSizeTransaction, int maxSizeTransaction, int length){
        this.minSizeTransaction = minSizeTransaction;
        this.maxSizeTransaction = maxSizeTransaction;
        this.length = length;
    }

    public double genGuassianProbablitity(){
        double mean = 0.5;
        double variance = 0.125;
        Random random = new Random();
        double gaussianValue = random.nextGaussian();
        double probability = mean + Math.sqrt(variance) * gaussianValue;
        probability = Math.max(0.1, Math.min(1, probability));
        return probability;
    }

    public double genUniformWeight() {
        Random random = new Random();
        double weight = random.nextDouble();
        while (Double.compare(weight, 0) == 0) {
            weight = random.nextDouble();
        }

        return weight;
    }

    public int randomInt(int max, int min){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public void gen(){
        for(int i = 0; i < this.length; i++){
            int numItemsInTran = this.randomInt(this.maxSizeTransaction, this.minSizeTransaction);
            HashMap<Integer, Double> tran = new HashMap<Integer, Double>();

            // gen a transaction
            for(int j = 0; j < numItemsInTran; j++){
                int item = randomInt(20, 1);
                double prob = this.genGuassianProbablitity();
                tran.put(item, prob);

                // helping caculation UI
                // 2 lines below for convert itemset to String
                // System.out.printf("curr Item: %d\n", item);
                // HashSet<Integer> size1Itemset = new HashSet<>(){{add(item);}};
                // String strSize1Itemset = size1Itemset.toString();
                if(this.W.get(item) == null) this.W.put(item, this.genUniformWeight());
                if(this.UI.get(item) == null) this.UI.put(item, 0.0);
                this.UI.put(item, this.UI.get(item) + prob);
            }

            this.UD.add(tran);
        }
    } 
}