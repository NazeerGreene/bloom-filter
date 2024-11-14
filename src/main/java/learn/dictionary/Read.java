package learn.dictionary;

import learn.utils.BloomFilter;

import java.io.*;
import java.util.Locale;

/**
 * The interface to build a bloom filter either:
 * from a text file (raw)
 * from a binary file (compiled)
 */
public class Read {
    /**
     * Read and add elements to a bloom filter. This method assumes the filter is already built
     * (the bit array allocated) before calling this.
     * @param filename Where to locate the raw source text file
     * @param filter The filter to add elements to
     * @throws IOException If problems occur reading the text file
     */
    public static void fromRawSource(String filename, BloomFilter filter) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // no need to skip any line
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                filter.add(line.toLowerCase(Locale.ROOT));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Could not open file: " + filename);
        }
    }

    /**
     * Returns the bit array already compiled by a bloom filter.
     * @param filename The binary file to read from
     * @return The already-compiled bit array
     * @throws IOException If problems occur reading the binary file
     */
    public static byte[] fromCompiledSource(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        }
    }
}
