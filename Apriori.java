import java.util.*;

public class Apriori<T>{
    int msup, n;
    double alpha, t;
    ArrayList<HashMap<T, Double>> UD;
    HashMap<T, Double> W;
    // some helpers
    HashMap<Object, Double> UI;
    HashSet<T> I;
    // Holding current size-k-WPFI and current U of size-k-WPFI
    HashSet<HashSet<T>> WPFIK = new HashSet<HashSet<T>>();
    HashMap<Object, Double> UWPFIK = new HashMap<Object, Double>();

    public Apriori(ArrayList<HashMap<T, Double>> UD, HashMap<T, Double> W, HashMap<Object, Double> UI, int msup, double alpha, double t){
        this.UD = UD;
        this.W = W;
        this.UI = UI;
        this.msup = msup;
        this.n = this.UD.size();
        this.t = t;
        this.I = this.itemsInUD();
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

    public boolean isWPFI(double wX, double probXInUD, double t){
        // w(X) . P(Sup(X) >= msup) >= t ? 
        return Double.compare(wX * probXInUD, t) >= 0;
        
    }

    // public HashSet<T> itemsInUD(){
    //     HashSet<T> I = new HashSet<T>();
    //     for(HashMap<T, Double> tran : this.UD){
    //         I.addAll(tran.keySet());
    //     }
    //     return I;
    // }

    public HashSet<T> itemsInUD(){
        HashSet<T> tmpI = new HashSet<>();
        for(HashMap<T, Double> tran : this.UD){
            tmpI.addAll(tran.keySet());
        }
        return tmpI;
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

    public HashSet<HashSet<T>> genSize1WPFI(){
        HashSet<HashSet<T>> WPFI1 = new HashSet<HashSet<T>>();
        for(T item : this.I){
            WPFI1.add(new HashSet<>(){{add(item);}});
        }
        return WPFI1;
    }

    public void scanFindKItemset(HashSet<HashSet<T>> WPFIK, int msup, double t){
        HashSet<HashSet<T>> realWPFIK = new HashSet<HashSet<T>>();
        HashMap<Object, Double> UWPFIK = new HashMap<Object, Double>();

        for(HashSet<T> wPFI : WPFIK){
            // System.out.println(wPFI);
            double wX = this.getWeight(wPFI);
            double[] packed = this.probXInUD(wPFI, msup, t, wX);
            // System.out.println(Arrays.toString(packed));
            // packed[0] : P(Sup(X) >= msup)
            // packed[1] : uX
            if(this.isWPFI(wX, packed[0], t)){
                realWPFIK.add(wPFI);
                UWPFIK.put(wPFI, packed[1]);
                // System.out.printf("wPFI: %s - uX: %f\n", wPFI, UWPFI.get(wPFI));
            }
        }
        
        // Object[] results = new Object[2];
        // results[0] = realWPFIK;
        // results[1] = UWPFI;
        // return results;
        this.WPFIK = realWPFIK;
        this.UWPFIK = UWPFIK;
    }


    public HashSet<HashSet<T>> genWPFIApriori(HashSet<HashSet<T>> prevWPFI, HashMap<Object, Double> UWPFI, double alpha, int n, double t, double uHat){

        HashSet<HashSet<T>> Ck = new HashSet<HashSet<T>>();
        HashSet<T> Ia = this.itemsInPrevWPFI(prevWPFI);
  
        for(HashSet<T> X : prevWPFI){
            // copy X
            HashSet<T> XCopy = new HashSet<T>(X);
            // copy Ia
            HashSet<T> IaDiffX = new HashSet<T>(Ia);
            // Ia - X
            IaDiffX.removeAll(XCopy);

            for(T Ii : IaDiffX){
                // copy X
                HashSet<T> XUnionIi = new HashSet<T>(X);
                // X U Ii
                XUnionIi.addAll(new HashSet<T>(){{add(Ii);}});

                // w(X U Ii) >= t 
                if(Double.compare(this.getWeight(XUnionIi), t) >= 0){

                    double uX = UWPFI.get(X); // Vẫn đảm bảo được thứ tự
                    double uIi = UI.get(Ii); // Xem lại

                    // min(uX, uIi) >= uHat and uX * uIi >= alpha * n * uHat
                    if(Double.compare(Math.min(uX, uIi), uHat) >= 0 &&
                    Double.compare(uX * uIi, alpha * n * uHat) >= 0){
                        Ck.add(XUnionIi);
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

                    double uX = UWPFI.get(X);
                    double uIi = UI.get(Ii);

                    // min(uX, uIi) >= uHat and uX * uIi >= alpha * n * uHat
                    if(Double.compare(Math.min(uX, uIi), uHat) >= 0 &&
                    Double.compare(uX * uIi, alpha * n * uHat) >= 0){
                        Ck.add(XUinonIi);
                    }
                }
            }
        }
        return Ck;
    }

    public HashSet<HashSet<T>> solve(){
        ArrayList<HashSet<HashSet<T>>> WPFI = new ArrayList<>();
        HashSet<HashSet<T>> C0 = this.genSize1WPFI();
        double uHat = this.msup; // msup - 1
        scanFindKItemset(C0, msup, this.t);
        WPFI.add(this.WPFIK);
        int k = 1;
        // System.out.println(WPFI.get(k - 1));
        while(!WPFI.get(k - 1).isEmpty()){
            // System.out.println(WPFI.get(k - 1));
            HashSet<HashSet<T>> Ck = this.genWPFIApriori(this.WPFIK, this.UWPFIK, this.alpha, this.n, this.t, uHat);
            // System.out.println(Ck);
            scanFindKItemset(Ck, this.msup, this.t);
            WPFI.add(this.WPFIK);
            k++;
        }
        return WPFI.get(k - 2);
    }
}
