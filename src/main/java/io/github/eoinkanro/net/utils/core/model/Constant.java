package io.github.eoinkanro.net.utils.core.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {

    public static final String HELP_AVAILABLE_ARGUMENTS = "Available arguments:";

    public static final Function<String, HttpMethod> TO_HTTP_METHOD = HttpMethod::valueOf;

}
