package learn.dictionary;

import learn.utils.BuildInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * The interface to build a bloom filter either:
 * from a text file (raw)
 * from a binary file (compiled)
 */
public class Read {
    /**
     * Reads and returns lines from file provided by filename
     * @param filename Where to locate the raw source text file
     * @throws IOException If problems occur reading the text file
     */
    public static List<String> dictFromRawSource(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename), StandardCharsets.US_ASCII);
    }

    /**
     * Returns the bit array already compiled by a bloom filter.
     * @param filename The binary file to read from
     * @return The already-compiled bit array
     * @throws IOException If problems occur reading the binary file
     */
    public static DictionaryData dictFromCompiledSource(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // reading the header
            byte[] headerBytes = fis.readNBytes(BuildInfo.headerByteSize());

            if (headerBytes.length != BuildInfo.headerByteSize()) {
                throw new IOException("Unexpected read error while scanning dictionary file for version information");
            }

            BuildInfo header = BuildInfo.readBuildInfo(headerBytes);

            // reading the dictionary
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return new DictionaryData(header, baos.toByteArray());
        }
    }

    /**
     * Counts the newlines in a file, where each newline is a unique element in the dictionary
     * @param filePath Where to locate the file
     * @return The number of newlines in the file
     * @throws IOException If problems occur reading the file
     */
    public static int countNewlines(String filePath) throws IOException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                count++;
            }
        }
        return count > 0 ? count - 1 : 0; // Subtract 1 because last line doesn't end with newline
    }

    /**
     * Returns a list of integers found in a csv file. Assumes 1 line in file that contains the values.
     * @param filePath Where to locate the file
     * @return Array of integers parsed from the file
     * @throws IOException If problems occur reading the file
     */
    public static int[] getSeedsFromCSV(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line == null) {
                return null;  // empty file
            }

            String[] values = line.split(",");
            int[] result = new int[values.length];

            for (int i = 0; i < values.length; i++) {
                result[i] = Integer.parseInt(values[i].trim());
            }

            return result;
        }
    }
}
