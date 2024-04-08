package io.github.eoinkanro.net.utils.benchmark.model;

import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.commons.utils.model.CliArgumentCastFunctions;
import io.github.eoinkanro.net.utils.core.model.Constant;
import io.github.eoinkanro.net.utils.core.model.HttpMethod;
import io.github.eoinkanro.net.utils.core.utils.TimeConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CliArguments {

    public static final CliArgument<String> URL_ARGUMENT = CliArgument.<String>builder()
            .key("url")
            .castFunction(CliArgumentCastFunctions.TO_STRING)
            .referenceClass(String.class)
            .description("Request url")
            .build();

    public static final CliArgument<HttpMethod> HTTP_METHOD_ARGUMENT = CliArgument.<HttpMethod>builder()
            .key("method")
            .castFunction(Constant.TO_HTTP_METHOD)
            .referenceClass(HttpMethod.class)
            .description("Request method")
            .build();

    public static final CliArgument<String> BODY_ARGUMENT = CliArgument.<String>builder()
            .key("body")
            .castFunction(CliArgumentCastFunctions.TO_STRING)
            .referenceClass(String.class)
            .description("Request body")
            .defaultValue("")
            .build();

    public static final CliArgument<Integer> THREAD_ARGUMENT = CliArgument.<Integer>builder()
            .key("thread")
            .castFunction(CliArgumentCastFunctions.TO_INT)
            .referenceClass(Integer.class)
            .description("Parallel request threads amount")
            .defaultValue(1)
            .build();

    public static final CliArgument<Long> REQUESTS_ARGUMENT = CliArgument.<Long>builder()
            .key("requests")
            .castFunction(CliArgumentCastFunctions.TO_LONG)
            .referenceClass(Long.class)
            .description("Requests amount per second per thread")
            .build();

    public static final CliArgument<Long> DURATION_ARGUMENT = CliArgument.<Long>builder()
            .key("duration")
            .castFunction(TimeConverter::formattedStringToLongMs)
            .referenceClass(Long.class)
            .description("Duration of test. Example: 1M")
            .defaultValue(60000L)
            .build();

    public static final CliArgument<Long> TIMEOUT_ARGUMENT = CliArgument.<Long>builder()
            .key("timeout")
            .castFunction(TimeConverter::formattedStringToLongMs)
            .referenceClass(Long.class)
            .description("Timeout of requests. Example: 10S")
            .defaultValue(10000L)
            .build();

    public static final List<CliArgument<?>> ALL_ARGUMENTS = List.of(URL_ARGUMENT, HTTP_METHOD_ARGUMENT,
            BODY_ARGUMENT, THREAD_ARGUMENT, REQUESTS_ARGUMENT, DURATION_ARGUMENT, TIMEOUT_ARGUMENT);

    /**
     * TODO
     * Headers?
     */
}
