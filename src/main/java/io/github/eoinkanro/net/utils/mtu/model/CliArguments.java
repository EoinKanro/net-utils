package io.github.eoinkanro.net.utils.mtu.model;

import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.commons.utils.model.CliArgumentCastFunctions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CliArguments {

    public static final CliArgument<String> IP_ARGUMENT = CliArgument.<String>builder()
            .key("ip")
            .castFunction(CliArgumentCastFunctions.TO_STRING)
            .referenceClass(String.class)
            .description("IP of host to ping")
            .build();

    public static final CliArgument<Integer> START_ARGUMENT = CliArgument.<Integer>builder()
            .key("start")
            .castFunction(CliArgumentCastFunctions.TO_INT)
            .referenceClass(Integer.class)
            .description("Low limit of mtu")
            .defaultValue(1300)
            .build();

    public static final CliArgument<Integer> END_ARGUMENT = CliArgument.<Integer>builder()
            .key("end")
            .castFunction(CliArgumentCastFunctions.TO_INT)
            .referenceClass(Integer.class)
            .description("Max limit of mtu")
            .defaultValue(1600)
            .build();

    public static final CliArgument<Integer> STEP_ARGUMENT = CliArgument.<Integer>builder()
            .key("step")
            .castFunction(CliArgumentCastFunctions.TO_INT)
            .referenceClass(Integer.class)
            .description("Step of mtu")
            .defaultValue(2)
            .build();

    public static final List<CliArgument<?>> ALL_ARGUMENTS = List.of(IP_ARGUMENT, START_ARGUMENT,
            END_ARGUMENT, STEP_ARGUMENT);
}
