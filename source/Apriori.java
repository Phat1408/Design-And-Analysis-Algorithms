import java.util.*;

/**
 * UD: Tập dữ liệu không chắc chắn, được biểu diễn dưới dạng List của Map. Mỗi
 * Map chứa các item và xác suất của item đó.
 * 
 * W: Map chứa trọng số cho mỗi item.
 * 
 * UI: Map chứa tổng xác suất của mỗi item trong tất cả các transaction.
 * 
 * msup: msup là minimum support, ví dụ như 0.02 * n (với n là size của UD)
 * 
 * alpha là một số thực, ý nghĩa giúp loại bỏ các wPFI ở ngưỡng nào (có thể hiểu
 * giúp tỉa nhiều hay ít)
 * 
 * t là minimum probabilistic threshold hay ngưỡng xác suất, số thực này do
 * người dùng tự định nghĩa
 * 
 * uHat là nghiệm (xấp xỉ) của phương trình 1 - cdf_Poisson(msup - 1, u) = t/m
 * (trong bai nay uHat tính xấp xỉ là msup - 1)
 * 
 * I: Set chứa tất cả item trong UD.
 * 
 * WPFIK: Set các itemset là wPFI với kích thước k.
 * 
 * UWPFIK: Map chứa tổng xác suất ứng với từng itemset.
 *
 * Ví dụ:
 * UD: [{1=0.5, 2=0.7}, {1=0.6, 3=0.8}, {2=0.4}]
 * W: {1=0.6, 2=0.7, 3=0.8}
 * UI: {1=1.1, 2=1.1, 3=0.8}
 * msup: 100
 * alpha: 0.5
 * t: 0.3
 * uHat: 99
 */
public class Apriori<T> {
    List<Map<T, Double>> UD;
    Map<T, Double> W;
    int msup, n;
    double alpha, t, uHat;
    Map<Object, Double> UI;
    Set<T> I;
    Set<Set<T>> WPFIK = new HashSet<Set<T>>();
    Map<Object, Double> UWPFIK = new HashMap<Object, Double>();

    public Apriori(List<Map<T, Double>> UD, Map<T, Double> W, Map<Object, Double> UI,
            int msup, double alpha, double t) {
        this.UD = UD;
        this.W = W;
        this.UI = UI;
        this.msup = msup;
        this.n = this.UD.size();
        this.t = t;
        this.I = this.itemsInUD();
        this.uHat = (double) this.msup - 1;
    }

    /**
     * Hàm tính trung bình trọng số (weight) của itemset X
     * 
     * @param X là một itemset, ví dụ: [16, 18, 19, 10, ...]
     * @return trả về một số thực, mang ý nghĩa là trung bình của tổng các item
     *         trong itemset X
     */
    public double getWeight(Set<T> X) {
        double result = 0.0;
        for (T item : X) {
            result += this.W.get(item);
        }
        return (1.0 / X.size()) * result;
    }

    /**
     * Hàm kiểm tra xem một itemset có là tập con của một transaction trong UD hay
     * không
     * 
     * @param X  là một itemset, ví dụ: [16, 17, 20, 5, 13]
     * @param Ti là một tập transaction thứ i của UD chứa item và xác suất của item
     *           đó của transaction, ví dụ: {16=0.956419577441147,
     *           17=0.3526279950664786, 20=1.0, 5=0.802305893491428, 6=1.0,
     *           7=0.9256701777620175, 8=0.1, 9=0.37190244282707074,
     *           13=0.47885782012625805, 15=0.522246084003489}
     * @return true hoặc false
     */
    public Boolean isSubset(Set<T> X, Map<T, Double> Ti) {
        return Ti.keySet().containsAll(X);
    }

    /**
     * Hàm tính xác suất của itemset X subseteq Ti
     * 
     * @param X  là một itemset, ví dụ: [16, 17, 20, 5, 13]
     * @param Ti là một tập transaction thứ i của UD chứa item và xác suất của item
     *           đó của
     *           transaction, ví dụ: {16=0.956419577441147, 17=0.3526279950664786,
     *           20=1.0, 5=0.802305893491428, 6=1.0, 7=0.9256701777620175, 8=0.1,
     *           9=0.37190244282707074, 13=0.47885782012625805,
     *           15=0.522246084003489}
     * @return Trả một số thực, mang ý nghĩa về xác suất của itemset X subseteq Ti
     */
    public double probXInTi(Set<T> X, Map<T, Double> Ti) {
        double result = 0.0;
        if (this.isSubset(X, Ti)) {
            result = 1.0;
            for (T item : X) {
                result *= Ti.get(item);
            }
        }
        return result;
    }

    /**
     * Hàm lấy các item có trong WPFI trước đó
     * 
     * @param WPFI là tập hợp các WPFI có size là k - 1, ví dụ: [[1, 3], [2, 3], [3,
     *             4], ...]
     * @return Trả về một itemset là tập hợp các items có trong WPFI size k - 1, ví
     *         dụ: [1, 2, 3, 4, ...]
     */
    public Set<T> itemsInPrevWPFI(Set<Set<T>> WPFI) {
        Set<T> items = new HashSet<T>();
        for (Set<T> wPFI : WPFI) {
            items.addAll(wPFI);
        }
        return items;
    }

    /**
     * Hàm tính P(Sup(X) >= msup) đồng thời tính uX
     * @param X là một itemset, ví dụ: [16, 18, 19, 10, ...]
     * @param msup là minimum support, một số thực, ví dụ như 0.02 * n (với n là size của UD)
     * @param t là minimum probabilistic threshold hay ngưỡng xác suất, một số thực
     * @param wX là weight của itemset X, là một số thực
     * @return trả về 2 giá trị kiểu double bao gồm P(Sup(X) >= msup) và uX, ví dụ [0.72, 3.82]
     */
    public double[] probXInUD(Set<T> X, int msup, double t, double wX) {
        int n = this.UD.size();
        double[] prevLine = new double[n - msup + 1];
        Arrays.fill(prevLine, 1.0);
        double[] currLine = Arrays.copyOf(prevLine, prevLine.length); // chỉ dành cho việc khởi tạo, không có ý nghĩa

        double[] storedProbXInT = new double[n]; // Lưu trữ xác suất của itemset X trong transaction tj
        Arrays.fill(storedProbXInT, -1.0); // khởi tạo tất cả là -1.0 biểu thị cho việc chưa có xác suất nào được lưu

        for (int i = 1; i <= msup; i++) {
            currLine = new double[n - msup + i + 1]; // chạy từ 0 -> |T| - msup + i
            for (int j = 0; j <= n - msup + i; j++) {
                // không có trong storedProbItemsetInT[j] đồng nghĩa với storedProbItemsetInT[j] = -1.0
                if (j < n)
                    if (Double.compare(storedProbXInT[j], -1.0) == 0) {
                        storedProbXInT[j] = this.probXInTi(X, this.UD.get(j));
                    }

                if (i <= j) {
                    double probXInTj_1 = storedProbXInT[j - 1];
                    currLine[j] = probXInTj_1 * prevLine[j - 1] + (1 - probXInTj_1) * currLine[j - 1];

                    // Có thể tỉa ở P_(msup - k),(|T| - k), 1<= k <= msup nếu P này bé hơn t/wX
                    if (i == msup - i && j == n - i) // tránh out of index cho currLine
                        if (Double.compare(currLine[n - i], t / wX) == -1)
                            return new double[] { -1.0, Arrays.stream(storedProbXInT).sum() };
                }
            }
            prevLine = Arrays.copyOf(currLine, currLine.length);
        }
        return new double[] { currLine[n], Arrays.stream(storedProbXInT).sum() };
    }

    /**
     * Hàm kiểm tra xem w(X).Pr(Sup(X) >= msup) >= t hay không, hay kiểm tra xem
     * itemset X có phải là wPFI hay không
     * 
     * @param wX        là một số thực, mang ý nghĩa là trung bình của tổng các item
     *                  trong một itemset được tính thông qua hàm getWeight()
     * @param probXInUD là xác suất Pr(Sup(X) >= msup) của một itemset X được tính
     *                  thông qua hàm probXInUD()
     * @param t         là probabilistic threshold ngưỡng xác suất tối thiểu để một
     *                  itemset X là một PFI
     * @return true hoặc false
     */
    public boolean isWPFI(double wX, double probXInUD, double t) {
        return Double.compare(wX * probXInUD, t) >= 0;
    }

    /**
     * Hàm lấy tên của tất cả item trong UD
     * 
     * @return set chứa tên của tất cả item trong UD, ví dụ: I = [16, 18, 3, ...]
     */
    public Set<T> itemsInUD() {
        Set<T> tmpI = new HashSet<>();
        for (Map<T, Double> tran : this.UD) {
            tmpI.addAll(tran.keySet());
        }
        return tmpI;
    }

    /**
     * Hàm tìm item có weight nhỏ nhất trong itemset X
     * 
     * @param X là một itemset, ví dụ: [2, 3, 14]
     * @return Trả về item có weight nhỏ nhất trong itemset X, ví dụ: 2
     */
    public T argmin(Set<T> X) {
        T minWeightItem = null;
        double min = Double.MAX_VALUE;
        for (T item : X) {
            double wItem = this.W.get(item);
            if (wItem < min) {
                min = wItem;
                minWeightItem = item;
            }
        }
        return minWeightItem;
    }

    /**
     * Hàm hổ trợ gán các itemset size 1 trong I vào WPFIK
     * I là tập hợp các item có trong UD, ví dụ: I = [16, 18, 3, 19, 9, 13, ...]
     * Output: WPFIK chứa tập hợp các itemset size 1, ví dụ [[17], [2], [4], ...]
     */
    public void genSize1WPFI() {
        Set<Set<T>> WPFI1 = new HashSet<Set<T>>();
        for (T item : this.I) {
            WPFI1.add(new HashSet<>() {
                {
                    add(item);
                }
            });
        }
        this.WPFIK = WPFI1;
    }

    /**
     * Hàm tỉa (purning) có chức năng loại bỏ các itemset trong WPFI size k không
     * phải là wPFI
     * 
     * Output:
     * List WPFIK chứa các list itemset thật sự là WPFI và map UWPFIK chứa tổng xác
     * suất ứng với
     * từng list itemset, ví dụ:
     * WPFIK = [[1, 6], [2, 6], ...]
     * UWPFIK = {[1, 6]=1.2509412038087944, [2, 6]=2.530819979565079, ...}
     */
    public void scanFindKItemset() {
        Set<Set<T>> realWPFIK = new HashSet<Set<T>>();
        Map<Object, Double> UWPFIK = new HashMap<Object, Double>();

        for (Set<T> wPFI : this.WPFIK) {
            double wX = this.getWeight(wPFI);
            double[] packed = this.probXInUD(wPFI, this.msup, this.t, wX);
            if (this.isWPFI(wX, packed[0], t)) {
                realWPFIK.add(wPFI);
                UWPFIK.put(wPFI, packed[1]);
            }
        }

        this.WPFIK = realWPFIK;
        this.UWPFIK = UWPFIK;
    }

    /**
     * Hàm sinh các itemset (xấp xỉ) wPFI size k, dồng thời loại bỏ các itemset size
     * k (chuẩn bị sinh) không là wPFI
     * 
     * Output:
     * List WPFIK chứa các list itemsets size k, được sinh ra và xấp xỉ wPFI, ví dụ:
     * [[3, 4], [1, 6], [2, 6], [2, 4, 11], [2, 6, 9], [1, 6, 10], [17, 4, 7, 8],
     * [4, 7, 11, 14], ...]
     */
    public void genWPFIApriori() {

        Set<Set<T>> Ck = new HashSet<Set<T>>();
        Set<T> Ia = this.itemsInPrevWPFI(this.WPFIK);

        for (Set<T> X : this.WPFIK) {
            // copy X
            Set<T> XCopy = new HashSet<T>(X);
            // copy Ia
            Set<T> IaDiffX = new HashSet<T>(Ia);
            // Ia - X
            IaDiffX.removeAll(XCopy);

            for (T Ii : IaDiffX) {
                // copy X
                Set<T> XUnionIi = new HashSet<T>(X);
                // X U Ii
                XUnionIi.addAll(new HashSet<T>() {
                    {
                        add(Ii);
                    }
                });

                // w(X U Ii) >= t
                if (Double.compare(this.getWeight(XUnionIi), t) >= 0) {

                    double uX = this.UWPFIK.get(X);
                    double uIi = this.UI.get(Ii);

                    // min(uX, uIi) >= uHat and uX * uIi >= alpha * n * uHat
                    if (Double.compare(Math.min(uX, uIi), this.uHat) >= 0 &&
                            Double.compare(uX * uIi, this.alpha * this.n * this.uHat) >= 0) {
                        Ck.add(XUnionIi);
                    }
                }
            }

            T Im = this.argmin(XCopy);
            // copy I
            Set<T> IDiffIaDiffX = new HashSet<T>(I);
            // I - Ia - X
            IDiffIaDiffX.removeAll(IaDiffX);

            for (T Ii : IDiffIaDiffX) {
                // copy X
                Set<T> XUinonIi = new HashSet<T>(X);
                // X U Ii
                XUinonIi.addAll(new HashSet<T>() {
                    {
                        add(Ii);
                    }
                });

                // w(X U Ii) >= t and w(Ii) < w(Im)
                if (Double.compare(this.getWeight(XUinonIi), t) >= 0 &&
                        Double.compare(this.W.get(Ii), this.W.get(Im)) < 0) {

                    double uX = this.UWPFIK.get(X);
                    double uIi = this.UI.get(Ii);

                    // min(uX, uIi) >= uHat and uX * uIi >= alpha * n * uHat
                    if (Double.compare(Math.min(uX, uIi), this.uHat) >= 0 &&
                            Double.compare(uX * uIi, this.alpha * this.n * this.uHat) >= 0) {
                        Ck.add(XUinonIi);
                    }
                }
            }
        }
        this.WPFIK = Ck;
    }

    /**
     * Hàm thực hiện thuật toán Apriori
     * 
     * @return # Trả về WPFI kế cuối (kế tập rỗng), ví dụ:
     *         [[1, 3, 5], [1, 2, 7], [1, 3, 7], [1, 3, 8], [1, 4, 7], [1, 5, 7],
     *         [1, 5, 8], [1, 6, 7], [2, 5, 7], [1, 3, 11], [3, 5, 7], [1, 3, 12],
     *         [1, 5, 10], [1, 7, 8], [1, 2, 13], [1, 3, 13], [1, 5, 11], [1, 7, 9],
     *         [2, 7, 8], [1, 5, 12], [1, 18, 5], ...]
     */
    public Set<Set<T>> solve() {
        List<Set<Set<T>>> WPFI = new ArrayList<>();
        this.genSize1WPFI();
        this.scanFindKItemset();
        WPFI.add(this.WPFIK);
        int k = 1;

        while (!WPFI.get(k - 1).isEmpty()) {
            this.genWPFIApriori();
            this.scanFindKItemset();
            WPFI.add(this.WPFIK);
            k++;
        }
        return WPFI.get(k - 2);
    }
}
