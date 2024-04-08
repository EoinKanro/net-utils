package io.github.eoinkanro.net.utils.benchmark.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class BenchmarkData {

    private final List<Long> responseMs = new LinkedList<>();
    private final Map<Integer, Long> responseCode = new HashMap<>();

    public void addResponseMs(long ms) {
        responseMs.add(ms);
    }

    public void incrementResponseCode(int code) {
        responseCode.compute(code, (k, v) -> (v == null) ? 1 : v + 1);
    }

}
