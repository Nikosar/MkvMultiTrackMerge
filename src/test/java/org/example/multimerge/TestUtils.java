package org.example.multimerge;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
    public static Path INPUT_PATH = Paths.get("src", "test", "resources", "mkvmergesample");
    public static final String INPUT_DIR = INPUT_PATH.toAbsolutePath().toString();
}
