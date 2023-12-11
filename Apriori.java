import java.util.*;

public class Apriori<T> implements Cloneable{
    ArrayList<HashMap<T, Double>> UD;
    HashMap<T, Double> W;

    public Apriori(ArrayList<HashMap<T, Double>> UD, HashMap<T, Double> W){
        this.UD = UD;
        this.W = W;
    }

    public double getWeight(HashSet<T> itemset){
        double result = 0.0;
        for(T item: itemset){
            result += this.W.get(item);
        }
        return result;
    }

    public Boolean isSubset(HashSet<T> itemset, HashMap<T, Double> transaction){
        return transaction.keySet().containsAll(itemset);
    }

    public double probItemsetInTransaction(HashSet<T> itemset, HashMap<T, Double> transaction){
        double result = 0.0;
        if(isSubset(itemset, transaction)){
            for(T item: itemset){
                result += transaction.get(item);
            }
        }
        return result;
    }

    public double[] probXGreaterEqualMsup(HashSet<T> itemset, int msup, double t){
        int n = this.UD.size();
        double[] prevLine = new double[n - msup];
        Arrays.fill(prevLine, 1.0);

        int numTransVisited = 1;
        double uItemset = 0.0;

        double[] storedProbItemsetInT = new double[n];
        Arrays.fill(storedProbItemsetInT, -1.0);

        for(int i = 1; i <= msup; i++){
            double[] currLine = new double[n - msup + i];
            for(int j = 0; j <= n - msup + i; i++){
                if(i > j) currLine[j] = 0;
                else{
                    // not in storedProbItemsetInT[j] aka storedProbItemsetInT[j] = -1.0
                    if(Double.compare(storedProbItemsetInT[j], -1.0) == 0){
                        storedProbItemsetInT[j] = this.probItemsetInTransaction(itemset, this.UD.get(j));
                    }
                    double probItemsetInTj = storedProbItemsetInT[j];

                    // kết hợp tìm tổng xác suất của itemset
                    if(numTransVisited <= n){
                        uItemset += probItemsetInTj;
                        numTransVisited += 1;
                    }

                    currLine[j] = probItemsetInTj * prevLine[j - 1] + (1 - probItemsetInTj)* currLine[j - 1];

                    // Có thể tỉa ở P_(i - k),(j - k)
                    if(j == n - i) // tránh out of index cho currLine
                        if (Double.compare(currLine[n - i], probItemsetInTj) < t ) return new double[]{-1.0, uItemset};
                }
                // prevLine = Arrays.copyOf(currLine, currLine.length); Xem lại
            }
        }
        return new double[] {0.0, 0.0};
    }

    // public ArrayList<HashSet<T>> scanFindKItemset(ArrayList<HashSet<T>> WPFIK){
        
    // }
}
