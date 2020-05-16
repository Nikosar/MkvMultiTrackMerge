package org.example.multimerge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    public static void start(String command) throws IOException {
        log.info("starting: " + command);
        //too big command without split?
        Process process = new ProcessBuilder(command.split(" ")).start();
        awaitProcess(process);
    }

    private static void awaitProcess(Process process) {
        try (Scanner scanner = new Scanner(process.getInputStream())) {
            while (scanner.hasNextLine()) {
                log.info(scanner.nextLine());
            }
        }
    }
}
