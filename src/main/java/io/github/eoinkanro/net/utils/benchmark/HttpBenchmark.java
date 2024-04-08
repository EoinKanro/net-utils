package io.github.eoinkanro.net.utils.benchmark;

import io.github.eoinkanro.commons.utils.CliArgumentUtils;
import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.net.utils.benchmark.model.BenchmarkCallable;
import io.github.eoinkanro.net.utils.benchmark.model.BenchmarkData;
import io.github.eoinkanro.net.utils.core.model.Constant;
import io.github.eoinkanro.net.utils.core.utils.Printer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static io.github.eoinkanro.net.utils.benchmark.model.CliArguments.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpBenchmark {

    private static final String LINE_DELIMITER = "---------------------------------------";

    public static final int PERCENTILE_10 = 10;
    public static final int PERCENTILE_25 = 25;
    public static final int PERCENTILE_50 = 50;
    public static final int PERCENTILE_75 = 75;
    public static final int PERCENTILE_90 = 90;
    public static final int PERCENTILE_95 = 95;
    public static final int PERCENTILE_99 = 99;
    private static final String PERCENTILE_FORMAT = "%-10s | %-11s";
    private static final String STATUS_FORMAT = "%-10s";

    public static void run() {
        if (!isArgumentsFine()) {
            printHelp();
            return;
        }

        Printer.println("Starting http benchmark...");
        int threads = CliArgumentUtils.getArgument(THREAD_ARGUMENT);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<BenchmarkData> result = new ArrayList<>(threads);

        try {
            List<FutureTask<BenchmarkData>> tasks = new ArrayList<>(threads);

            for (int i = 0; i < threads; i++) {
                FutureTask<BenchmarkData> task = new FutureTask<>(new BenchmarkCallable());
                tasks.add(task);
                executorService.execute(task);
            }

            for (FutureTask<BenchmarkData> task : tasks) {
                result.add(task.get());
            }

        } catch (InterruptedException e) {
            Printer.println(Arrays.toString(e.getStackTrace()));
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Printer.println(Arrays.toString(e.getStackTrace()));
        } finally {
            executorService.shutdownNow();
        }

        printBenchmarkResult(result);
    }

    /**
     * Check if all arguments are presented
     */
    private static boolean isArgumentsFine() {
        for (CliArgument<?> arg : ALL_ARGUMENTS) {
            if (CliArgumentUtils.getArgument(arg) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Print help of all used arguments
     */
    private static void printHelp() {
        Printer.println(Constant.HELP_AVAILABLE_ARGUMENTS);
        ALL_ARGUMENTS.forEach(arg -> Printer.println(arg.getHelp()));
    }

    /**
     * Print result of benchmark
     */
    private static void printBenchmarkResult(List<BenchmarkData> result) {
        List<Long> responsesMs = new LinkedList<>();
        result.forEach(data -> responsesMs.addAll(data.getResponseMs()));
        Collections.sort(responsesMs);

        long avg = getAvg(responsesMs);
        long percentile10Result = getPercentile(responsesMs, PERCENTILE_10);
        long percentile25Result = getPercentile(responsesMs, PERCENTILE_25);
        long percentile50Result = getPercentile(responsesMs, PERCENTILE_50);
        long percentile75Result = getPercentile(responsesMs, PERCENTILE_75);
        long percentile90Result = getPercentile(responsesMs, PERCENTILE_90);
        long percentile95Result = getPercentile(responsesMs, PERCENTILE_95);
        long percentile99Result = getPercentile(responsesMs, PERCENTILE_99);

        Map<Integer, Long> statuses = calculateStatuses(result);
        long status2XX = 0;
        long statusOther = 0;
        for (Map.Entry<Integer, Long> entry : statuses.entrySet()) {
            if (entry.getKey() >= 200 && entry.getKey() < 300) {
                status2XX += entry.getValue();
            } else {
                statusOther += entry.getValue();
            }
        }

        Printer.println(LINE_DELIMITER);
        Printer.println("AVG: " + avg + " ms, MIN: " + responsesMs.get(0) + " ms, MAX: " + responsesMs.get(responsesMs.size() - 1) + "ms");
        Printer.println("2xx: " + status2XX + " other: " + statusOther);
        Printer.println(LINE_DELIMITER);
        Printer.println(String.format(PERCENTILE_FORMAT, "Percentile", "Response ms"));
        Printer.println(String.format(PERCENTILE_FORMAT, PERCENTILE_10, percentile10Result));
        Printer.println(String.format(PERCENTILE_FORMAT, PERCENTILE_25, percentile25Result));
        Printer.println(String.format(PERCENTILE_FORMAT, PERCENTILE_50, percentile50Result));
        Printer.println(String.format(PERCENTILE_FORMAT, PERCENTILE_75, percentile75Result));
        Printer.println(String.format(PERCENTILE_FORMAT, PERCENTILE_90, percentile90Result));
        Printer.println(String.format(PERCENTILE_FORMAT, PERCENTILE_95, percentile95Result));
        Printer.println(String.format(PERCENTILE_FORMAT, PERCENTILE_99, percentile99Result));
        Printer.println(LINE_DELIMITER);
        Printer.println(String.format(STATUS_FORMAT, "Status") + " | Count");

        statuses.forEach((k,v) ->
                Printer.println(String.format(STATUS_FORMAT, k) + " | " + v)
        );
    }

    /**
     * Calculate avg of responses ms
     *
     * @param responsesMs  all responses ms
     * @return  avg
     */
    private static long getAvg(List<Long> responsesMs) {
        long avg = 0;
        for (Long ms : responsesMs) {
            avg += ms;
        }
        avg = avg / responsesMs.size();
        return avg;
    }

    /**
     * Get percentile from responses ms
     *
     * @param responsesMs  sorted responses ms
     * @param percentile   percentile
     * @return             percentile ms
     */
    private static long getPercentile(List<Long> responsesMs, double percentile) {
        return responsesMs.get((int) Math.round(percentile / 100.0 * (responsesMs.size() - 1)));
    }

    /**
     * Sum all statuses from responses to one map
     *
     * @param result result of executing
     * @return all statuses from result
     */
    private static Map<Integer, Long> calculateStatuses(List<BenchmarkData> result) {
        Map<Integer, Long> statuses = new HashMap<>();

        result.forEach(r ->
                r.getResponseCode().forEach((code, count) ->
                        statuses.compute(code, (k, v) -> (v == null) ? count : v + count)
                )
        );

        return statuses;
    }

}
