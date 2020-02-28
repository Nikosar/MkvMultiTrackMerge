import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class MainTest {
    private static Path RESOURCES_SAMPLE = Paths.get("src", "test", "resources", "mkvmergesample");

    @Before
    public void before() throws IOException {
        Path output = RESOURCES_SAMPLE.resolve("output");
        Files.deleteIfExists(output.resolve("sample.mkv"));
    }

    @Test
    public void main() throws IOException {
        String[] args = new String[1];
        args[0] = RESOURCES_SAMPLE.toRealPath(NOFOLLOW_LINKS).toString();

        System.out.println(args[0]);
        Main.main(args);
    }
}