import java.util.*;

/**
 * minSizeTransaction: Kích thước tối thiểu của một transaction
 * maxSizeTransaction: Kích thước tối đa của một transaction
 * length: Số lượng transaction trong tập dữ liệu không chắc chắn UD
 * 
 * UD: List chứa transaction mỗi transaction sẽ là một Map chứa các item và xác suất của item đó, ví dụ:
 * [{16=0.636008192309531}, {12=0.8961329102567097}, {5=0.873682851751415, 12=0.6193355107455638}]
 * 
 * W: Map chứa các item và weight của item đó, ví dụ:
 * {16=0.636008192309531, 5=0.873682851751415, 12=1.5154684210022735}
 * 
 * UI: Map chứa các item và tổng xác suất của item đó trong tất cả transaction, ví dụ:
 * {16=0.10667144376532833, 5=0.725728658241622, 12=0.037313805342546025}
 */
public class UncertainDataset {
    int minSizeTransaction, maxSizeTransaction, length;
    List<Map<Integer, Double>> UD = new ArrayList<Map<Integer, Double>>();
    Map<Integer, Double> W = new HashMap<Integer, Double>();
    Map<Object, Double> UI = new HashMap<Object, Double>();

    /**
     * Constructor không tham số, khởi tạo với các giá trị mặc định.
     */
    public UncertainDataset() {
        this.minSizeTransaction = 10;
        this.maxSizeTransaction = 20;
        this.length = 1000;
    }

    /**
     * Constructor với tham số, cho phép tùy chỉnh kích thước tối thiểu và tối đa
     * của transaction và số lượng transaction.
     */
    public UncertainDataset(int minSizeTransaction, int maxSizeTransaction, int length) {
        this.minSizeTransaction = minSizeTransaction;
        this.maxSizeTransaction = maxSizeTransaction;
        this.length = length;
    }

    /**
     * Phương thức tạo ra một giá trị xác suất ngẫu nhiên cho từng item trong mỗi
     * transaction theo phân phối Gaussian.
     * 
     * @return một giá trị xác suất ngẫu nhiên theo phân phối Gaussian.
     */
    public double genGuassianProbablitity() {
        double mean = 0.5;
        double variance = 0.125;
        Random random = new Random();
        double gaussianValue = random.nextGaussian();
        double probability = mean + Math.sqrt(variance) * gaussianValue;
        probability = Math.max(0.1, Math.min(1, probability));
        return probability;
    }

    /**
     * Phương thức tạo ra một giá trị xác suất ngẫu nhiên cho trọng số weight cho
     * từng item theo phân phối đều.
     * 
     * @return một giá trị xác suất ngẫu nhiên theo phân phối đều.
     */
    public double genUniformWeight() {
        Random random = new Random();
        double weight = random.nextDouble();
        while (Double.compare(weight, 0) == 0) {
            weight = random.nextDouble();
        }

        return weight;
    }

    /**
     * Phương thức tạo ra một số nguyên ngẫu nhiên trong khoảng cho trước.
     * 
     * @param max Giá trị tối đa.
     * @param min Giá trị tối thiểu.
     * @return Số nguyên ngẫu nhiên được tạo ra trong khoảng (min, max).
     */
    public int randomInt(int max, int min) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Phương thức để tạo ra tập dữ liệu không chắc chắc UD với kích thước length và
     * kích thước của mỗi transaction là một số nguyên ngẫu nhiên trong khoảng
     * (minSizeTransaction, maxSizeTransaction), tập trọng số W, và tập UI chứa tổng
     * xác suất của từng item trong tất cả transation.
     * item mặc định là một số ngẫu nhiên từ 1 đến 20
     * xác suất của mỗi item được tạo thông qua hàm genGuassianProbablitity();
     * trọng số của mỗi item được tạo thông qua hàm genUniformWeight();
     * 
     * @return
     *         UD: List chứa transaction với số lượng length mỗi transaction sẽ là một Map chứa các item và xác suất của item đó, ví dụ:
     *         [{16=0.636008192309531}, {12=0.8961329102567097}, {5=0.873682851751415, 12=0.6193355107455638}]
     * 
     *         W: Map chứa các item và weight của item đó, ví dụ:
     *         {16=0.636008192309531, 5=0.873682851751415, 12=1.5154684210022735}
     * 
     *         UI: Map chứa các item và tổng xác suất của item đó trong tất cả transaction, ví dụ:
     *         {16=0.10667144376532833, 5=0.725728658241622, 12=0.037313805342546025}
     */
    public void gen() {
        for (int i = 0; i < this.length; i++) {
            int numItemsInTran = this.randomInt(this.maxSizeTransaction, this.minSizeTransaction);
            Map<Integer, Double> tran = new HashMap<Integer, Double>();

            for (int j = 0; j < numItemsInTran; j++) {
                int item = randomInt(20, 1);

                if (tran.get(item) == null) {
                    double prob = this.genGuassianProbablitity();
                    tran.put(item, prob);
                    this.UI.put(item, this.UI.getOrDefault(item, 0.0) + prob);
                }

                if (this.W.get(item) == null) {
                    this.W.put(item, this.genUniformWeight());
                }

            }

            this.UD.add(tran);
        }
    }
}