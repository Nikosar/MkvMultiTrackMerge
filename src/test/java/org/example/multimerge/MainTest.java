package org.example.multimerge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static org.example.multimerge.TestUtils.INPUT_PATH;

public class MainTest {
    private String TEST_SAMPLE_PATH;
    private Path outputFile;

    @BeforeEach
    public void before() throws IOException {
        TEST_SAMPLE_PATH = INPUT_PATH.toRealPath(NOFOLLOW_LINKS).toString();
        outputFile = INPUT_PATH.resolve("output").resolve("mkvmergesample").resolve("sample.mkv");
        Files.deleteIfExists(outputFile);

        Main.main(TEST_SAMPLE_PATH);
    }

    @Test
    public void main() throws IOException {
    }
}