package io.github.eoinkanro.net.utils.mtu;

import io.github.eoinkanro.commons.utils.CliArgumentUtils;
import io.github.eoinkanro.commons.utils.SystemUtils;
import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.commons.utils.model.OsType;
import io.github.eoinkanro.net.utils.core.ActionExecutor;
import io.github.eoinkanro.net.utils.core.utils.Printer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static io.github.eoinkanro.net.utils.mtu.model.CliArguments.*;

public class MtuTester extends ActionExecutor {

    private static final int PING_HEADER_SIZE = 28;

    private static final Integer START = CliArgumentUtils.getArgument(START_ARGUMENT);
    private static final Integer END = CliArgumentUtils.getArgument(END_ARGUMENT);
    private static final Integer STEP = CliArgumentUtils.getArgument(STEP_ARGUMENT);
    private static final String IP = CliArgumentUtils.getArgument(IP_ARGUMENT);
    private static final Boolean SETUP = CliArgumentUtils.getArgument(SETUP_ARGUMENT);
    private static final List<String> INTERFACES = CliArgumentUtils.getArgument(INTERFACES_ARGUMENT);

    @Override
    protected void execute() throws Exception {
        Printer.println("Starting mtu tester...");
        int maxMtu = testMaxMtu();

        if (maxMtu > 0 && SETUP && INTERFACES != null && INTERFACES.size() > 0) {
            setupMtu(maxMtu);
        }
    }

    private int testMaxMtu() throws IOException, InterruptedException {
        int maxMtu = 0;

        for (int i = START; i <= END; i += STEP) {
            maxMtu = i;

            ProcessBuilder processBuilder = new ProcessBuilder(getTestMtuCommands(i));
            Process process = processBuilder.start();
            process.waitFor();

            if (process.exitValue() != 0) {
                if (i == START) {
                    maxMtu = -1;
                } else {
                    maxMtu -= STEP;
                }
                break;
            }
        }

        Printer.println("Max mtu: " + maxMtu);
        return maxMtu;
    }

    private void setupMtu(int mtu) throws IOException, InterruptedException {
        Printer.println("Setting mtu " + mtu + " ...");

        for (String interf : INTERFACES) {
            Printer.println(interf);
            ProcessBuilder processBuilder = new ProcessBuilder(getSettingMtuCommands(interf, mtu));
            Process process = processBuilder.start();
            process.waitFor();

            if (process.exitValue() != 0) {
                Printer.println("Error");
                process.inputReader().lines().forEach(Printer::println);
            } else {
                Printer.println("Done");
            }
        }
    }

    @Override
    protected List<CliArgument<?>> getNecessaryArguments() {
        return NECESSARY_ARGUMENTS;
    }

    @Override
    protected List<CliArgument<?>> getAllArguments() {
        return ALL_ARGUMENTS;
    }

    private List<String> getTestMtuCommands(int mtu) {
        int packageSize = mtu - PING_HEADER_SIZE;
        if (SystemUtils.getOsType() == OsType.WINDOWS) {
            return Arrays.asList("ping", "-w", "1000", "-n", "1", "-f", "-l", String.valueOf(packageSize), IP);
        } else {
            return Arrays.asList("ping", "-M", "do", "-s", String.valueOf(packageSize), "-c", "1", IP);
        }
    }

    private List<String> getSettingMtuCommands(String interf, int mtu) {
        if (SystemUtils.getOsType() == OsType.WINDOWS) {
            return Arrays.asList("netsh", "interface", "ipv4", "set", "interface", "\"" + interf + "\"", "mtu=" + mtu);
        } else {
            return Arrays.asList("ip", "link", "set", "mtu", String.valueOf(mtu), interf);
        }
    }

}
