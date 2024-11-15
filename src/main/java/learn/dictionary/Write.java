package learn.dictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The interface to write the bloom filter bit array to memory.
 */
public class Write {
    /**
     * Method to store bit array to a binary file.
     * @param filename The file to store the bit array in
     * @param data The bit array to store
     * @throws IOException If problems occur creating or writing to file
     */
    public static void dictToBinaryFile(String filename, byte[] data) throws IOException {
        Files.write(Paths.get(filename), data);
    }

    public static void seedsToCsvFile(String filename, int[] seeds) throws IOException {
        String out = Arrays.stream(seeds).mapToObj(Objects::toString).collect(Collectors.joining(","));
        Files.write(Paths.get(filename), out.getBytes());
    }
}
