package learn;

import learn.controller.Controller;
import learn.dictionary.Read;
import learn.dictionary.Write;
import learn.hash.FNV1A64;
import learn.utils.BloomFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {

        try{
            Controller controller = new Controller();
            controller.run(List.of(args));
        } catch (IOException e) {
            System.out.println("Error while running application: " + e.getMessage());
        }

    }

    public static void manualTesting() throws IOException {
        String filePath = "./data/dict-sub.txt";
        String compiledFilePath = "./data/dict-compiled.bf";
        int[] seeds = new int[]{1,2,3,4,5,6,7,8,9,10};

        int nElements = (int) countNewlines(filePath);
        double dsf = 0.01;

        int bitsRequired = BloomFilter.calculateBitArraySize(dsf, nElements);
        int nHashFunctions = BloomFilter.calculateNumOfHashFunctions(bitsRequired, nElements);

        BloomFilter filter = new BloomFilter(dsf, new FNV1A64(), Arrays.copyOf(seeds, nHashFunctions));
        filter.build(nElements);

        // first pass - reading all elements in dict to bloom filter

        Read.fromRawSource(filePath, filter);

        // now check for some values we know to be present
        List<String> to_test = List.of("aardvark", "abduction", "absconce");

        for(String element: to_test) {
            System.out.println("Querying for '" + element + "' [true]: " + filter.contains(element));
        }

        System.out.println("Querying for 'zoo' [false]: " + filter.contains("zoo"));

        // now we need to save it to a file
        Write.toBinaryFile(compiledFilePath, filter.getBitArray().toByteArray());
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
