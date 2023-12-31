import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class hỗ trợ benchmarks dataset xuất ra file exel gồm 2 cột size của transaction và runtime (seconds) 
 */
public class Plotting {
    public static void main(String[] args) throws IOException {
        List<Integer> sizes = new ArrayList<>();
        List<Double> runtimesInSeconds = new ArrayList<>(); 
        List<Integer> msups = new ArrayList<>();
        List<Double> thresholds = new ArrayList<>();
        List<Double> alphas = new ArrayList<>();

        for (int size = 1000; size <= 10000; size += 1000) {
            UncertainDataset ud = new UncertainDataset(5, 15, size);
            ud.gen();
            double t = 0.5, alpha = 0.6;
            int msup = (int) Math.round(0.01 * ud.UD.size());
            Apriori<Integer> ap = new Apriori<Integer>(ud.UD, ud.W, ud.UI, msup, alpha, t);

            long startTime = System.nanoTime();
            ap.solve();
            long endTime = System.nanoTime();

            double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
            sizes.add(size);
            msups.add(msup);
            thresholds.add(t);
            alphas.add(alpha);
            runtimesInSeconds.add(durationInSeconds);
        }

        exportToCSV("real_runtime.csv", sizes, runtimesInSeconds, msups, thresholds, alphas);
    }

    private static void exportToCSV(String fileName, List<Integer> sizes, List<Double> runtimesInSeconds, 
        List<Integer> msups, List<Double> thresholds, List<Double> alphas) throws IOException {
        try (FileWriter csvWriter = new FileWriter(fileName)) {
            csvWriter.append("Size,Runtime (seconds),msup, t, alpha\n");
            for (int i = 0; i < sizes.size(); i++) {
                csvWriter.append(sizes.get(i).toString()).append(",");
                csvWriter.append(runtimesInSeconds.get(i).toString()).append(",");
                csvWriter.append(msups.get(i).toString()).append(",");
                csvWriter.append(thresholds.get(i).toString()).append(",");
                csvWriter.append(alphas.get(i).toString()).append("\n");
            }
        }
    }
}
