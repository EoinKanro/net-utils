package io.github.eoinkanro.net.utils.interfaces;

import io.github.eoinkanro.commons.utils.SystemUtils;
import io.github.eoinkanro.commons.utils.model.CliArgument;
import io.github.eoinkanro.commons.utils.model.OsType;
import io.github.eoinkanro.net.utils.core.ActionExecutor;
import io.github.eoinkanro.net.utils.core.utils.Printer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InterfacesTester extends ActionExecutor {

    public static final String WHITE_SPACES_REGEX = "\\s+";
    private static final String CONNECTED = "Connected";
    private static final String SPACE = " ";
    public static final int BEGIN_INTERFACE_INDEX = 3;
    public static final String END_INTERFACE_SYMBOL = ":";
    public static final String STATE_UP = "state UP";

    @Override
    protected void execute() throws Exception {
        Printer.println("Starting getting interfaces names...");

        ProcessBuilder processBuilder = new ProcessBuilder(getCommands());
        Process process = processBuilder.start();
        process.waitFor();

        List<String> result = isWindows() ? getWindowsResult(process) : getOtherResult(process);

        Printer.println("Connected interfaces:");
        Printer.println(result.toString());
    }

    private List<String> getWindowsResult(Process process) {
        List<String> result = new ArrayList<>();

        process.inputReader().lines().forEach(l -> {
            if (l.contains(CONNECTED)) {
                String[] interfaceStatus = l.split(WHITE_SPACES_REGEX);

                StringBuilder interfaceName = new StringBuilder();
                for (int i = BEGIN_INTERFACE_INDEX; i < interfaceStatus.length; i++) {
                    interfaceName.append(interfaceStatus[i]);
                    interfaceName.append(SPACE);
                }
                result.add(interfaceName.toString().trim());
            }
        });
        return result;
    }

    private List<String> getOtherResult(Process process) {
        List<String> result = new ArrayList<>();

        process.inputReader().lines().forEach(l -> {
            if (l.contains(STATE_UP)) {
                result.add(l.substring(BEGIN_INTERFACE_INDEX, l.indexOf(END_INTERFACE_SYMBOL, BEGIN_INTERFACE_INDEX)));
            }
        });
        return result;
    }

    @Override
    protected List<CliArgument<?>> getNecessaryArguments() {
        return getAllArguments();
    }

    @Override
    protected List<CliArgument<?>> getAllArguments() {
        return Collections.emptyList();
    }

    private List<String> getCommands() {
        if (isWindows()) {
            return Arrays.asList("netsh", "interface", "show", "interface");
        } else {
            return Arrays.asList("ip", "addr", "show");
        }
    }

    private boolean isWindows() {
        return SystemUtils.getOsType() == OsType.WINDOWS;
    }

}
