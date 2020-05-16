package org.example.multimerge;

import lombok.SneakyThrows;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Paths;

import static picocli.CommandLine.Parameters;

@Command
public class Main implements Runnable {
    private static final String OUTPUT = "output";
    @Option(names = {"-o", "--output"}, description = "in this directory will be created new folder")
    private String outputDir;
    @Parameters
    private String workDirectory;

    public static void main(String... args) {
        new CommandLine(new Main()).execute(args);
    }

    @SneakyThrows
    @Override
    public void run() {
        if (outputDir == null) outputDir = Paths.get(workDirectory, OUTPUT).toString();
        MultipleMkvMerge multipleMkvMerge = new MultipleMkvMerge(workDirectory, outputDir);
        multipleMkvMerge.mergeAll();
    }
}
