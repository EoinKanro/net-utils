package io.github.eoinkanro.net.utils.core;

import io.github.eoinkanro.commons.utils.CliArgumentUtils;
import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.commons.utils.model.CliArgumentCastFunctions;
import io.github.eoinkanro.net.utils.core.model.Constant;
import io.github.eoinkanro.net.utils.core.utils.Printer;

import java.util.Arrays;
import java.util.List;

public abstract class ActionExecutor {

    private static final CliArgument<Boolean> HELP_ARGUMENT = CliArgument.<Boolean>builder()
            .key("help")
            .referenceClass(Boolean.class)
            .castFunction(CliArgumentCastFunctions.TO_BOOLEAN)
            .description("Print help")
            .defaultValue(false)
            .build();

    public void run() {
        try {
            if (!isArgumentsFine(getNecessaryArguments())) {
                printHelp(getAllArguments());
                return;
            }

            if (CliArgumentUtils.getArgument(HELP_ARGUMENT)) {
                printHelp(getAllArguments());
            }

            execute();
        } catch (InterruptedException e) {
            Printer.println(Arrays.toString(e.getStackTrace()));
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Printer.println(Arrays.toString(e.getStackTrace()));
        }
    }

    protected abstract void execute() throws Exception;

    protected abstract List<CliArgument<?>> getNecessaryArguments();

    protected abstract List<CliArgument<?>> getAllArguments();

    /**
     * Check if all arguments are presented
     */
    private boolean isArgumentsFine(List<CliArgument<?>> args) {
        for (CliArgument<?> arg : args) {
            if (CliArgumentUtils.getArgument(arg) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Print help of all used arguments
     */
    private void printHelp(List<CliArgument<?>> args) {
        Printer.println(Constant.HELP_AVAILABLE_ARGUMENTS);
        args.forEach(arg -> Printer.println(arg.getHelp()));
    }
}
