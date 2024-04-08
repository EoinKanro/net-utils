package io.github.eoinkanro.net.utils.core.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CastFunctions {

    public static final Function<String, HttpMethod> TO_HTTP_METHOD = HttpMethod::valueOf;
}
