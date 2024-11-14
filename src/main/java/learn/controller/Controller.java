package learn.controller;

import learn.dictionary.Read;
import learn.dictionary.Write;
import learn.hash.FNV1A64;
import learn.hash.QuickHash;
import learn.utils.BloomFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Controller {
    View view = new View();

    // Related to Bloom filter
    private BloomFilter filter;
    private double desiredFP; // false positivity probability
    private QuickHash hash;
    private int[] seeds;

    // File locations
    private static final String dataDirectory = "./data/test/";
    private static final String compiledDictionary = "dict-compiled.bf";
    private static final String seedsFile = "seeds.csv";

    // setters
    public void setDesiredFP(double newValue) {
        this.desiredFP = newValue;
    }

    public void setHash(QuickHash hash) {
        this.hash = hash;
    }

    public void setSeeds(int[] seeds) {
        this.seeds = seeds;
    }

    // Constructors
    public Controller() throws IOException {
        this.filter = null;
        this.desiredFP = BloomFilter.DFP_DEFAULT;
        this.hash = new FNV1A64();
        this.seeds = readSeedsFromCSV();
    }

    // either we build the filter or we check the filter for members

    public void run(List<String> args) throws IOException {
        build("./data/test/dict-sub.txt");
    }

    boolean build(String rawDictionary) throws IOException {
        String compiledFilePath = dataDirectory + compiledDictionary;

        // first we should verify that we have all the necessary components
        int nElementsInDictionary = countNewlines(rawDictionary);
        int bitsRequired = BloomFilter.calculateBitArraySize(desiredFP, nElementsInDictionary);
        int nHashFunctions = BloomFilter.calculateNumOfHashFunctions(bitsRequired, nElementsInDictionary);

        if (null == seeds || seeds.length < nHashFunctions) {
            return false;
        }

        // build the filter
        filter = new BloomFilter(desiredFP, hash, Arrays.copyOf(seeds, nHashFunctions));
        filter.build(nElementsInDictionary);

        // add elements to filter
        Read.fromRawSource(rawDictionary, filter);

        // save filter to memory
        Write.toBinaryFile(compiledFilePath, filter.getBitArray().toByteArray());

        // finished
        return true;
    };

    void check(List<String> toCheck) {

    }

    // helpers

    private int[] readSeedsFromCSV() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataDirectory + seedsFile))) {
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

    private boolean checkForCompiledSource(String filename) {
        return false;
    }

    private static int countNewlines(String filePath) throws IOException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                count++;
            }
        }
        return count > 0 ? count - 1 : 0; // Subtract 1 because last line doesn't end with newline
    }
}
