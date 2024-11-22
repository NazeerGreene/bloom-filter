package learn.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

class ControllerTest {

    @Test
    void BuildDictionary() throws IOException {
        setup();
        Controller controller = new Controller();
        controller.run(new ArrayList<>(List.of("build", "./data/test/dict-sub.txt")));
    }

    @Test
    void RunDictionary() throws IOException {
        Controller controller = new Controller();
        controller.run(new ArrayList<>(List.of("check", "Aaronic", "abduction", "absconce", "zoo")));
    }

    private void setup() throws IOException {
        String sourceDir = "./data/original/";
        String destDir = "./data/test/";

        Files.copy(Path.of(sourceDir + "dict-sub.txt"), Path.of(destDir + "dict-sub.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Path.of(sourceDir + "seeds.csv"), Path.of(destDir + "seeds.csv"), StandardCopyOption.REPLACE_EXISTING);

    }
}