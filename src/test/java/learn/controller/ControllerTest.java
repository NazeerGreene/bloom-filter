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
        String source = "./data/original/dict-sub.txt";
        String dest = "./data/test/dict-sub.txt";

        Files.copy(Path.of(source), Path.of(dest), StandardCopyOption.REPLACE_EXISTING);
    }
}