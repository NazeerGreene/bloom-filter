package learn.controller;

import learn.hash.FNV1A64;
import learn.hash.QuickHash;
import learn.utils.BloomFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    private static final String rawDictionary = "dict-sub.txt";
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

    // either we build the filter or we check the filter

    public void run(List<String> args) {}

    void build(String textFilePath) {};

    void check(List<String> toCheck) {

    }

    // helpers

    private int[] readSeedsFromCSV() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(seedsFile))) {
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

    private static long countNewlines(String filePath) throws IOException {
        long count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                count++;
            }
        }
        return count > 0 ? count - 1 : 0; // Subtract 1 because last line doesn't end with newline
    }
}
