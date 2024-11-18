package learn.dictionary;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The interface to write the bloom filter bit array to memory.
 */
public class Write {
    /**
     * Method to store bit array to a binary file, first prefaced with an optional program header.
     * @param filename The file to store the bit array in
     * @param data The bit array to store
     * @throws IOException If problems occur creating or writing to file
     */
    public static void dictToBinaryFile(String filename, byte[] header, byte[] data) throws IOException {
        final Path path = Paths.get(filename);
        if (header != null) {
            Files.write(path, header);
        }
        Files.write(path, data, StandardOpenOption.APPEND);
    }

    /**
     * Saves an array of seed values to a CSV file.
     * @param filename Where to save the CSV file
     * @param seeds The array of seeds
     * @throws IOException If problems occur opening the file
     */
    public static void seedsToCsvFile(String filename, int[] seeds) throws IOException {
        String out = Arrays.stream(seeds).mapToObj(Objects::toString).collect(Collectors.joining(","));
        Files.write(Paths.get(filename), out.getBytes());
    }
}
