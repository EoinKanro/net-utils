package io.github.eoinkanro.net.utils.benchmark.model;

import io.github.eoinkanro.commons.utils.CliArgumentUtils;
import io.github.eoinkanro.commons.utils.SystemUtils;
import io.github.eoinkanro.net.utils.core.model.HttpMethod;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import static io.github.eoinkanro.net.utils.benchmark.model.CliArguments.*;
import static io.github.eoinkanro.net.utils.benchmark.model.CliArguments.DURATION_ARGUMENT;

public class BenchmarkCallable implements Callable<BenchmarkData> {

    private static final long SECOND = 1000;

    private final String url = CliArgumentUtils.getArgument(URL_ARGUMENT);
    private final String body = CliArgumentUtils.getArgument(BODY_ARGUMENT);
    private final HttpMethod httpMethod = CliArgumentUtils.getArgument(HTTP_METHOD_ARGUMENT);
    private final Long requests = CliArgumentUtils.getArgument(REQUESTS_ARGUMENT);
    private final Long duration = CliArgumentUtils.getArgument(DURATION_ARGUMENT);
    private final Long timeout = CliArgumentUtils.getArgument(TIMEOUT_ARGUMENT);

    private final long endOfTest = System.currentTimeMillis() + duration;

    @Override
    public BenchmarkData call() throws Exception {
        BenchmarkData result = new BenchmarkData();

        long nextIterationMs = getNextIterationMs();
        long currentRequestsCount = 0;

        while (System.currentTimeMillis() < endOfTest) {
            //wait until next iteration
            if (currentRequestsCount >= requests) {
                SystemUtils.sleep(nextIterationMs - System.currentTimeMillis());
            }
            //update iteration variable
            if (System.currentTimeMillis() >= nextIterationMs) {
                nextIterationMs = getNextIterationMs();
                currentRequestsCount = 0;
            }

            //request
            int responseCode = -1;
            long requestStart = System.currentTimeMillis();
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod(httpMethod.name());
                connection.setConnectTimeout(Math.toIntExact(timeout));
                connection.setReadTimeout(Math.toIntExact(timeout));

                if (body != null && !body.isEmpty()) {
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(body.getBytes());
                    outputStream.flush();
                    outputStream.close();
                } else {
                    connection.connect();
                }

                responseCode = connection.getResponseCode();
            } catch (Exception e) {
                //do nothing
            }

            //save result
            result.incrementResponseCode(responseCode);
            result.addResponseMs(System.currentTimeMillis() - requestStart);
            currentRequestsCount++;
        }

        return result;
    }

    private long getNextIterationMs() {
        return System.currentTimeMillis() + SECOND;
    }

}
