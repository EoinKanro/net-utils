package io.github.eoinkanro.net.utils;

import io.github.eoinkanro.commons.utils.CliArgumentUtils;
import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.net.utils.benchmark.HttpBenchmark;
import io.github.eoinkanro.net.utils.core.model.Constant;
import io.github.eoinkanro.net.utils.core.utils.Printer;

public class NetUtilsApplication {

    private static final CliArgument<Action> ARGUMENT_ACTION = CliArgument.<Action>builder()
            .key("action")
            .castFunction(Action::valueOf)
            .referenceClass(Action.class)
            .description("available functions of the program")
            .build();

    public static void main(String[] args) {
        CliArgumentUtils.init(args);
        Action action = CliArgumentUtils.getArgument(ARGUMENT_ACTION);

        if (action == null) {
            printError();
        }
        if (action == Action.BENCHMARK) {
            HttpBenchmark.run();
        }
    }

    private static void printError() {
        Printer.println(Constant.HELP_AVAILABLE_ARGUMENTS);
        Printer.println(ARGUMENT_ACTION.getHelp());
    }

    private enum Action {
        MTU,
        BENCHMARK
    }

}