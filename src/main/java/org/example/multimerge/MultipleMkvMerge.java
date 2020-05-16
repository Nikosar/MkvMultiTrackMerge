package org.example.multimerge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultipleMkvMerge {
    private static final Logger log = LoggerFactory.getLogger(MultipleMkvMerge.class);
    private static final String MKVMERGE = "\"mkvmerge\"";
    private final String outputDir;
    private List<File> mainTrackFiles;
    private List<File> externalTrackDirs;
    private int i = 0;

    //region Initialization
    public MultipleMkvMerge(String workDir, String outputDir) {
        this.outputDir = resolveOutputDir(workDir, outputDir);
        File[] inputFilesAndDirs = new File(workDir).listFiles();
        initFileLists(inputFilesAndDirs);
    }

    private String resolveOutputDir(String workDir, String outputDir) {
        String dirName = new File(workDir).getName();
        Path newOutputDir = Paths.get(outputDir, dirName);
        return toAbsolutePathString(newOutputDir);
    }

    private String toAbsolutePathString(Path path) {
        return path.toAbsolutePath().toString();
    }

    private void initFileLists(File[] inputFilesAndDirs) {
        mainTrackFiles = Arrays.stream(inputFilesAndDirs)
                .filter(File::isFile)
                .collect(Collectors.toList());

        externalTrackDirs = Arrays.stream(inputFilesAndDirs)
                .filter(File::isDirectory)
                .collect(Collectors.toList());
    }
    //endregion

    public void mergeAll() throws IOException {
        for (File file : mainTrackFiles) {
            String command = prepareCommand(file);
            CommandExecutor.start(command);
            i++;
        }
        log.info("Task complete. {} files handled", i);
    }

    private String prepareCommand(File file) throws IOException {
        String mainTrackName = withoutExtension(file);
        List<String> mkvMergeConfig = new ArrayList<>();
        mkvMergeConfig.add("--language 1:jpn --track-name 1:Original \"" + file.getPath() + "\"");

        for (File externalTrackDir : externalTrackDirs) {
            List<String> commands = externalTrackCommands(mainTrackName, externalTrackDir);
            mkvMergeConfig.addAll(commands);
        }
        String tracksCommand = String.join(" ", mkvMergeConfig);
        String orderCommand = orderCommand(mkvMergeConfig);
        return MKVMERGE + " -o \"" + outputDir + "\\" + mainTrackName + ".mkv\" " + tracksCommand + orderCommand;
    }

    private List<String> externalTrackCommands(String mainTrackName, File externalTrackDir) throws IOException {
        return traverse(externalTrackDir)
                        .filter(Files::isRegularFile)
                        .filter(externalTrackNameContain(mainTrackName))
                        .map(path -> "--language 0:rus --track-name \"0:" + parentNameFor(path) + "\" " + "\"" + toAbsolutePathString(path) + "\"")
                        .collect(Collectors.toList());
    }

    private Stream<Path> traverse(File externalTrackDir) throws IOException {
        return Files.walk(Paths.get(externalTrackDir.getPath()));
    }

    private String parentNameFor(Path path) {
        return path.getParent().toFile().getName();
    }

    private Predicate<Path> externalTrackNameContain(String mainTrackName) {
        return path -> {
            String anotherTrackName = withoutExtension(path.toFile());
            return anotherTrackName.contains(mainTrackName);
        };
    }

    private String orderCommand(List<String> mkvMergeConfig) {
        if (mkvMergeConfig.size() < 2) return "";
        String orderCommand = " --track-order ";
        for (int j = 1; j < mkvMergeConfig.size(); j++) {
            orderCommand += j + ":0,";
        }
        return orderCommand.substring(0, orderCommand.length() - 1);
    }

    private String withoutExtension(File file) {
        String name = file.getName();
        int extensionStartPos = name.lastIndexOf(".");
        return name.substring(0, extensionStartPos);
    }
}
