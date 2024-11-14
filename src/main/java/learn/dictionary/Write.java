package learn.dictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    public static void toBinaryFile(String filename, byte[] data) throws IOException {
        Files.write(Paths.get(filename), data);
    }
}
