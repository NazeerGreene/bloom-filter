package learn.controller;

import learn.dictionary.Read;
import learn.dictionary.Write;
import learn.hash.FNV1A64;
import learn.hash.QuickHash;
import learn.utils.BloomFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static learn.dictionary.Read.countNewlines;

public class Controller {
    // Related to Bloom filter
    private BloomFilter filter;
    private double desiredFP; // false positivity probability
    private QuickHash hash;
    private int[] seeds;

    // File locations
    private static final String DATA_DIRECTORY = "./data/test/";
    private static final String COMPILED_DICTIONARY_PATH = DATA_DIRECTORY + "dict-compiled.bf";
    private static final String SEEDS_FILE = DATA_DIRECTORY + "seeds.csv";

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
        this.seeds = Read.getSeedsFromCSV(SEEDS_FILE);
    }

    // either we build the filter or we check the filter for members
    public void run(List<String> args) throws IOException {
        //build("./data/test/dict-sub.txt");
        List<String> notFound = check(List.of("aardvark", "abduction", "absconce", "zoo"));
        System.out.println("Not Found");
        if (notFound != null) {
            notFound.stream().forEach(element -> System.out.println(element));
        }
    }

    private boolean build(String rawDictionary) throws IOException {
        if (null == rawDictionary) {
            return false;
        }

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
        Read.dictFromRawSource(rawDictionary, filter);

        // save filter to memory
        Write.dictToBinaryFile(COMPILED_DICTIONARY_PATH, filter.getBitArray().toByteArray());
        Write.seedsToCsvFile(SEEDS_FILE, filter.getSeeds());

        // finished
        return true;
    };

    private List<String> check(List<String> elementsToCheck) throws IOException {
        // read the dictionary from memory
        byte[] dictionaryBinaryData = Read.dictFromCompiledSource(COMPILED_DICTIONARY_PATH);

        // build the filter
        BitSet data = BitSet.valueOf(dictionaryBinaryData);
        filter = new BloomFilter(desiredFP, hash, seeds);
        filter.build(data);

        if (null == filter) {
            return null;
        }

        // for each element to check, run through filter
        ArrayList<String> notFound = new ArrayList<>();

        for(String element: elementsToCheck) {
            if (!filter.contains(element)) {
                notFound.add(element);
            }
        }

        // return list
        return notFound;
    }

}
