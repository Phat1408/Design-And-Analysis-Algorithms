import java.util.*;

public class Apriori<T> implements Cloneable{
    ArrayList<HashMap<T, Double>> UD;
    HashMap<T, Double> W;

    public Apriori(ArrayList<HashMap<T, Double>> UD, HashMap<T, Double> W){
        this.UD = UD;
        this.W = W;
    }

    public double getWeight(HashSet<T> X){
        double result = 0.0;
        for(T item: X){
            result += this.W.get(item);
        }
        return result;
    }

    public Boolean isSubset(HashSet<T> X, HashMap<T, Double> Ti){
        return Ti.keySet().containsAll(X);
    }

    public double probXInTi(HashSet<T> X, HashMap<T, Double> Ti){
        double result = 1.0;
        if(this.isSubset(X, Ti)){
            for(T item: X){
                result *= Ti.get(item);
            }
        }
        return result;
    }

    public HashSet<T> itemInPrevWPFI(ArrayList<HashSet<T>> WPFI){
        HashSet<T> items = new HashSet<T>();
        for(HashSet<T> wPFI : WPFI){
            items.addAll(wPFI);
        }
        return items;
    }

    // P(Sup(X) >= msup)
    public double[] probXInUD(HashSet<T> X, int msup, double t, double wX){
        int n = this.UD.size();
        double[] prevLine = new double[n - msup + 1];
        Arrays.fill(prevLine, 1.0);
        double[] currLine = Arrays.copyOf(prevLine, prevLine.length); // just for initilize

        double[] storedProbXInT = new double[n];
        Arrays.fill(storedProbXInT, -1.0);

        for(int i = 1; i <= msup; i++){
            currLine = new double[n - msup + i + 1]; // 0 -> |T| - msup + i
            for(int j = 0; j <= n - msup + i; j++){
                // not in storedProbItemsetInT[j] aka storedProbItemsetInT[j] = -1.0
                if(j < n)
                    if(Double.compare(storedProbXInT[j], -1.0) == 0){
                        storedProbXInT[j] = this.probXInTi(X, this.UD.get(j));
                        System.out.printf("%d: %f\n", j, storedProbXInT[j]);
                    }

                if(i <= j){
                    double probXInTj_1 = storedProbXInT[j - 1];
                    currLine[j] = probXInTj_1  * prevLine[j - 1] + (1 - probXInTj_1 )* currLine[j - 1];

                    // Có thể tỉa ở P_(msup - k),(|T| - k), 1<= k <= msup nếu P này bé hơn t
                    // if(i == msup - i && j == n - i) // tránh out of index cho currLine
                    //     if (Double.compare(currLine[n - i], t/wX) == -1) 
                    //         return new double[]{-1.0, Arrays.stream(storedProbXInT).sum()};
                }
            }
            prevLine = Arrays.copyOf(currLine, currLine.length);
        }
        return new double[] {currLine[n], Arrays.stream(storedProbXInT).sum()};
    }

    public boolean isWPFI(HashSet<T> X, double probXInUD, double t){
        // w(X) . P(Sup(X) >= msup) >= t ? 
        return Double.compare(this.getWeight(X) * probXInUD, t) >= 0;
        
    }

    public HashSet<HashSet<T>> getSize1WPFI(HashSet<T> I){
        HashSet<HashSet<T>> WPFI1 = new HashSet<HashSet<T>>();
        for(T item : I){
            WPFI1.add(new HashSet<>(){{add(item);}});
        }
        return WPFI1;
    }

    public Object[] scanFindKItemset(ArrayList<HashSet<T>> WPFIK, int msup, double t){
        ArrayList<HashSet<T>> realWPFIK = new ArrayList<HashSet<T>>();
        ArrayList<Double> UWPFI = new ArrayList<Double>();

        for(HashSet<T> wPFI : WPFIK){
            double wX = this.getWeight(wPFI);
            double[] packed = this.probXInUD(wPFI, msup, t, wX);
            // packed[0] : P(Sup(X) >= msup)
            // packed[1] : uX
            if(this.isWPFI(wPFI, packed[0], t)){
                System.out.printf("wPFI: %s - uX: %f\n", wPFI, packed[1]);
                realWPFIK.add(wPFI);
                UWPFI.add(packed[1]);
            }
        }
        
        Object[] results = new Object[2];
        results[0] = realWPFIK;
        results[1] = UWPFI;
        return results;
    }
}
