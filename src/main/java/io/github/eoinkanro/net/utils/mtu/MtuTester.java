package io.github.eoinkanro.net.utils.mtu;

import io.github.eoinkanro.commons.utils.CliArgumentUtils;
import io.github.eoinkanro.commons.utils.SystemUtils;
import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.commons.utils.model.OsType;
import io.github.eoinkanro.net.utils.core.model.Constant;
import io.github.eoinkanro.net.utils.core.utils.Printer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static io.github.eoinkanro.net.utils.mtu.model.CliArguments.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MtuTester {

    private static final int PING_HEADER_SIZE = 28;

    private static final Integer START = CliArgumentUtils.getArgument(START_ARGUMENT);
    private static final Integer END = CliArgumentUtils.getArgument(END_ARGUMENT);
    private static final Integer STEP = CliArgumentUtils.getArgument(STEP_ARGUMENT);
    private static final String IP = CliArgumentUtils.getArgument(IP_ARGUMENT);

    public static void run() {
        if (!isArgumentsFine()) {
            printHelp();
            return;
        }

        Printer.println("Starting mtu tester...");
        int maxMtu = 0;

        try {
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
        } catch (InterruptedException e) {
            Printer.println(Arrays.toString(e.getStackTrace()));
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Printer.println(Arrays.toString(e.getStackTrace()));
        }

        Printer.println("Max mtu: " + maxMtu);
    }

    /**
     * Check if all arguments are presented
     */
    private static boolean isArgumentsFine() {
        for (CliArgument<?> arg : ALL_ARGUMENTS) {
            if (CliArgumentUtils.getArgument(arg) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Print help of all used arguments
     */
    private static void printHelp() {
        Printer.println(Constant.HELP_AVAILABLE_ARGUMENTS);
        ALL_ARGUMENTS.forEach(arg -> Printer.println(arg.getHelp()));
    }

    private static List<String> getCommands(int mtu) {
        int packageSize = mtu - PING_HEADER_SIZE;
        if (SystemUtils.getOsType() == OsType.WINDOWS) {
            return Arrays.asList("ping", "-w", "1000", "-n", "1", "-f", "-l", String.valueOf(packageSize), IP);
        } else {
            return Arrays.asList("ping", "-M", "do", "-s", String.valueOf(packageSize), "-c", "1", IP);
        }
    }

}
