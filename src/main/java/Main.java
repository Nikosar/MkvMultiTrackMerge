import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final String MKVMERGE = "\"mkvmerge\"";
    private static final String USER_DIRECTORY = System.getProperty("user.dir");
    private static final String OUTPUT = "\\output";
//    private static final String OUTPUT_DIR = USER_DIRECTORY + OUTPUT;
    private static int i = 0;

    public static void main(String[] args) throws IOException {
        String workDirectory = args[0];
        File[] files = new File(workDirectory).listFiles();

        List<File> externalTrackDirectories = Arrays.stream(files)
                .filter(File::isDirectory)
                .collect(Collectors.toList());

        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            String command = prepareCommand(externalTrackDirectories, file, workDirectory + OUTPUT);

            Process mkvtool = new ProcessBuilder(command).start();
            awaitProcess(args, command, mkvtool);


            Main.i++;
        }
        System.out.println("finished: " + i);


    }

    private static void awaitProcess(String[] args, String command, Process mkvtool) throws IOException {
        System.out.println("Start merge command:\n" + command);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(mkvtool.getInputStream()))) {
            String line;
            System.out.printf("Output of running %s is:", Arrays.toString(args));

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private static String prepareCommand(List<File> directories, File file, String outputDir) throws IOException {
        String mainTrackName = withoutExtension(file);
        List<String> mkvMergeConfig = new ArrayList<>();
        mkvMergeConfig.add("--language 1:jpn --default-track 1:no --track-name 1:Original \"" + file.getPath() + "\"");

        for (File dir : directories) {
            List<String> collect = Files.walk(Paths.get(dir.getPath()))
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String anotherTrackName = withoutExtension(path.toFile());
                        return anotherTrackName.contains(mainTrackName);
                    })
                    .map(p -> "--language 0:rus --default-track 0:yes --track-name \"0:" + p.getParent().toFile().getName() + "\" " + "\"" + p.toFile().getPath() + "\"")
                    .collect(Collectors.toList());

            mkvMergeConfig.addAll(collect);
        }


        String collect = mkvMergeConfig.stream()
                .collect(Collectors.joining(" "));
        return MKVMERGE + " -o \"" + outputDir + "\\" + mainTrackName + ".mkv\" " + collect;
    }

    private static String withoutExtension(File file) {
        String name = file.getName();
        int extensionStartPos = name.lastIndexOf(".");
        return name.substring(0, extensionStartPos);
    }


}
