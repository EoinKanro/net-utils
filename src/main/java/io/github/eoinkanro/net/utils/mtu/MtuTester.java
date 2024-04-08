package io.github.eoinkanro.net.utils.mtu;

import io.github.eoinkanro.commons.utils.CliArgumentUtils;
import io.github.eoinkanro.commons.utils.SystemUtils;
import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.commons.utils.model.OsType;
import io.github.eoinkanro.net.utils.core.ActionExecutor;
import io.github.eoinkanro.net.utils.core.utils.Printer;

import java.util.Arrays;
import java.util.List;

import static io.github.eoinkanro.net.utils.mtu.model.CliArguments.*;

public class MtuTester extends ActionExecutor {

    private static final int PING_HEADER_SIZE = 28;

    private static final Integer START = CliArgumentUtils.getArgument(START_ARGUMENT);
    private static final Integer END = CliArgumentUtils.getArgument(END_ARGUMENT);
    private static final Integer STEP = CliArgumentUtils.getArgument(STEP_ARGUMENT);
    private static final String IP = CliArgumentUtils.getArgument(IP_ARGUMENT);

    @Override
    protected void execute() throws Exception {
        Printer.println("Starting mtu tester...");
        int maxMtu = 0;

        for (int i = START; i <= END; i += STEP) {
            maxMtu = i;
            List<String> commands = getCommands(i);

            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            Process process = processBuilder.start();
            process.waitFor();

            int result = process.exitValue();
            if (result != 0) {
                if (i == START) {
                    maxMtu = -1;
                } else {
                    maxMtu -= STEP;
                }
                break;
            }
        }

        Printer.println("Max mtu: " + maxMtu);
    }

    @Override
    protected List<CliArgument<?>> getNecessaryArguments() {
        return getAllArguments();
    }

    @Override
    protected List<CliArgument<?>> getAllArguments() {
        return ALL_ARGUMENTS;
    }

    private List<String> getCommands(int mtu) {
        int packageSize = mtu - PING_HEADER_SIZE;
        if (SystemUtils.getOsType() == OsType.WINDOWS) {
            return Arrays.asList("ping", "-w", "1000", "-n", "1", "-f", "-l", String.valueOf(packageSize), IP);
        } else {
            return Arrays.asList("ping", "-M", "do", "-s", String.valueOf(packageSize), "-c", "1", IP);
        }
    }

}
