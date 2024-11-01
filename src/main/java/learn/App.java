package learn;

import learn.hash.FNV1A64;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class App {
    public static void main(String[] args) throws IOException {
        String filePath = "./data/dict-sub.txt";
        int[] seeds = new int[]{1,2,3,4,5,6,7,8,9,10};

        int nElements = (int) countNewlines(filePath);
        double dsf = 0.01;

        int bitsRequired = BloomFilter.calculateBitArraySize(dsf, nElements);
        int nHashFunctions = BloomFilter.calculateNumOfHashFunctions(bitsRequired, nElements);

        BloomFilter filter = new BloomFilter(dsf, new FNV1A64(), Arrays.copyOf(seeds, nHashFunctions));
        filter.build(nElements);

        // first pass - reading all elements in dict to bloom filter

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // no need to skip any line
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                filter.add(line.toLowerCase(Locale.ROOT));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            throw new RuntimeException("Could not open file: " + filePath, e);
        }

        // now check for some values we know to be present
        List<String> to_test = List.of("aardvark", "abduction", "absconce");

        for(String element: to_test) {
            System.out.println("Querying for '" + element + "' [true]: " + filter.contains(element));
        }


        System.out.println("Querying for 'zoo' [false]: " + filter.contains("zoo"));


    }

    public static long countNewlines(String filePath) throws IOException {
        long count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                count++;
            }
        }
        return count > 0 ? count - 1 : 0; // Subtract 1 because last line doesn't end with newline
    }


}
