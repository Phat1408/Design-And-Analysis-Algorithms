import java.util.*;

public class Apriori<T>{
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
        return (1.0/X.size()) * result;
    }

    public Boolean isSubset(HashSet<T> X, HashMap<T, Double> Ti){
        return Ti.keySet().containsAll(X);
    }

    public double probXInTi(HashSet<T> X, HashMap<T, Double> Ti){
        double result = 0.0;
        if(this.isSubset(X, Ti)){
            // System.out.printf("X: %s - Ti: %s\n", X, Ti);
            result = 1.0;
            for(T item: X){
                result *= Ti.get(item);
            }
        }
        return result;
    }

    public HashSet<T> itemsInPrevWPFI(HashSet<HashSet<T>> WPFI){
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
                        // System.out.printf("%d: %f\n", j, storedProbXInT[j]);
                    }

                if(i <= j){
                    double probXInTj_1 = storedProbXInT[j - 1];
                    currLine[j] = probXInTj_1  * prevLine[j - 1] + (1 - probXInTj_1 )* currLine[j - 1];

                    // Có thể tỉa ở P_(msup - k),(|T| - k), 1<= k <= msup nếu P này bé hơn t
                    if(i == msup - i && j == n - i) // tránh out of index cho currLine
                        if (Double.compare(currLine[n - i], t/wX) == -1) 
                            return new double[]{-1.0, Arrays.stream(storedProbXInT).sum()};
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

    public HashSet<T> itemsInUD(){
        HashSet<T> I = new HashSet<T>();
        for(HashMap<T, Double> tran : this.UD){
            I.addAll(tran.keySet());
        }
        return I;
    }

    public T argmin(HashSet<T> X){
        T minWeightItem = null;
        double min = Double.MAX_VALUE;
        for(T item : X){
            double wItem = this.W.get(item);
            if(wItem < min){
                min = wItem;
                minWeightItem = item;
            }
        }
        return minWeightItem;
    }

    public HashSet<HashSet<T>> genSize1WPFI(HashSet<T> I){
        HashSet<HashSet<T>> WPFI1 = new HashSet<HashSet<T>>();
        for(T item : I){
            WPFI1.add(new HashSet<>(){{add(item);}});
        }
        return WPFI1;
    }

    public Object[] scanFindKItemset(HashSet<HashSet<T>> WPFIK, int msup, double t){
        HashSet<HashSet<T>> realWPFIK = new HashSet<HashSet<T>>();
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

    public HashSet<HashSet<T>> genWPFIApriori(HashSet<HashSet<T>> prevWPFI, ArrayList<Double> UWPFI, ArrayList<Double> UI,
        HashSet<T> I, double alpha, int n, double t, double uHat){

        HashSet<HashSet<T>> Ck = new HashSet<HashSet<T>>();
        HashSet<T> Ia = this.itemsInPrevWPFI(prevWPFI);
        int i = 0;

        for(HashSet<T> X : prevWPFI){
            // copy X
            HashSet<T> XCopy = new HashSet<T>(X);
            // copy Ia
            HashSet<T> IaDiffX = new HashSet<T>(Ia);
            // Ia - X
            IaDiffX.removeAll(XCopy);

            for(T Ii : IaDiffX){
                // copy X
                HashSet<T> XUinonIi = new HashSet<T>(X);
                // X U Ii
                XUinonIi.addAll(new HashSet<T>(){{add(Ii);}});

                // w(X U Ii) >= t 
                if(Double.compare(this.getWeight(XUinonIi), t) >= 0){

                    double uX = UWPFI.get(i); // Vẫn đảm bảo được thứ tự
                    double uIi = UI.get(i);

                    // min(uX, uIi) >= uHat and uX * uIi >= alpha * n * uHat
                    if(Double.compare(Math.min(uX, uIi), uHat) >= 0 &&
                    Double.compare(uX * uIi, alpha * n * uHat) >= 0){
                        Ck.add(XUinonIi);
                    }
                }
            }

            T Im = this.argmin(XCopy);
            // copy I
            HashSet<T> IDiffIaDiffX = new HashSet<T>(I);
            // I - Ia - X
            IDiffIaDiffX.removeAll(IaDiffX);

            for(T Ii : IDiffIaDiffX){
                // copy X
                HashSet<T> XUinonIi = new HashSet<T>(X);
                // X U Ii
                XUinonIi.addAll(new HashSet<T>(){{add(Ii);}});

                // w(X U Ii) >= t and w(Ii) < w(Im)
                if(Double.compare(this.getWeight(XUinonIi), t) >= 0 &&
                Double.compare(this.W.get(Ii), this.W.get(Im)) < 0){

                    double uX = UWPFI.get(i);
                    double uIi = UI.get(i);

                    // min(uX, uIi) >= uHat and uX * uIi >= alpha * n * uHat
                    if(Double.compare(Math.min(uX, uIi), uHat) >= 0 &&
                    Double.compare(uX * uIi, alpha * n * uHat) >= 0){
                        Ck.add(XUinonIi);
                    }
                }
            }

            i+= 1;
        }
        return Ck;
    }

    public ArrayList<HashSet<T>> solve(ArrayList<Double> UI){
        return null;
    }
}
