package io.github.eoinkanro.net.utils;

import io.github.eoinkanro.commons.utils.CliArgumentUtils;
import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.net.utils.benchmark.HttpBenchmark;
import io.github.eoinkanro.net.utils.core.model.Constant;
import io.github.eoinkanro.net.utils.core.utils.Printer;
import io.github.eoinkanro.net.utils.interfaces.InterfacesTester;
import io.github.eoinkanro.net.utils.mtu.MtuTester;

import java.util.Arrays;
import java.util.List;

public class NetUtilsApplication {

    private static final CliArgument<Action> ARGUMENT_ACTION = CliArgument.<Action>builder()
            .keys(List.of("action", "A"))
            .castFunction(Action::valueOf)
            .referenceClass(Action.class)
            .description("Available functions of the program. Example: " + Arrays.toString(Action.values()))
            .build();

    public static void main(String[] args) {
        CliArgumentUtils.init(args);
        Action action = CliArgumentUtils.getArgument(ARGUMENT_ACTION);

        if (action == null) {
            printHelp();
            return;
        }

        switch (action) {
            case BENCHMARK -> new HttpBenchmark().run();
            case MTU -> new MtuTester().run();
            case INTERFACES -> new InterfacesTester().run();
        }
    }

    private static void printHelp() {
        Printer.println(Constant.HELP_AVAILABLE_ARGUMENTS);
        Printer.println(ARGUMENT_ACTION.getHelp());
    }

    private enum Action {
        MTU,
        INTERFACES,
        BENCHMARK
    }

}