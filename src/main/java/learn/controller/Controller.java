package learn.controller;

import learn.dictionary.DictionaryData;
import learn.dictionary.Read;
import learn.dictionary.Write;
import learn.utils.BloomFilter;
import learn.utils.BuildInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static learn.dictionary.Read.countNewlines;

/**
 * The interface to manage the control flow between the program and the user.
 * Do not modify files saved by the controller.
 *
 * Seeds for hashing algorithm should be placed in './data/seeds.csv' for initial dictionary build.
 * After initial build, no need to modify 'seeds.csv'.
 */
public class Controller {
    // Related to Bloom filter
    private BloomFilter filter;
    private final BuildInfo versionInfo;

    // File locations
    private static final String DATA_DIRECTORY = "./data/production/";
    private static final String COMPILED_DICTIONARY_PATH = DATA_DIRECTORY + "dict-compiled.bf";

    // Constructors
    public Controller() throws IOException {
        this.filter = null;
        this.versionInfo = new BuildInfo()
                .setVersion((short) 1);
    }

    // either we build the filter or we check the filter for members
    public void run(List<String> args) throws IOException {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Missing command: build <filename>, check [arg...]");
        }

        List<String> notFound;
        String command = args.removeFirst();

        switch (command) {
            case "build", "-b":
                buildFilter(args.removeFirst());
                return;
            case "check", "-c":
                notFound = checkFilterFor(args);
                break;
            default:
                throw new IllegalArgumentException("Missing command: build <filename>, check [arg...]");
        }

        System.out.println("Not found in dictionary:");
        notFound.forEach(System.out::println);
    }

    /**
     * The method to compile raw text into a bloom filter
     * @param rawDictionary The text file containing the elements to compile into a Bloom filter
     * @return true If Bloom filter was successfully built and saved to disk
     *         false If Bloom filter was not successfully built
     * @throws IOException If problems occur reading file provided by rawDictionary
     */
    private boolean buildFilter(String rawDictionary) throws IOException {
        if (null == rawDictionary) {
            return false;
        }

        // first we should verify that we have all the necessary components
        int nElements = countNewlines(rawDictionary);
        int nBits = BloomFilter.calculateBitArraySize(BloomFilter.DFP_DEFAULT, nElements);
        int nHashes = BloomFilter.calculateNumOfHashFunctions(nBits, nElements);

        // build the filter
        filter = BloomFilter.build(BloomFilter.DFP_DEFAULT, nElements);

        // add elements to filter
        for(String element: Read.dictFromRawSource(rawDictionary)) {
            filter.add(element);
        }

        // save filter to memory
        byte[] header = versionInfo
                .setHashFunctions((short) nHashes)
                .setBloomFilterBitsRequired((short) nBits)
                .generateByteHeader();

        byte[] dictionary = filter.getBitArray().toByteArray();

        Write.dictToBinaryFile(COMPILED_DICTIONARY_PATH, header, dictionary);

        // finished
        return true;
    }

    /**
     * The method to check elements in a compiled filter. Assumes buildFilter() has already been invoked and
     * the filter has been compiled separately.
     * @param elementsToCheck The list of Strings to check against the filter
     * @return The elements not present in the filter
     * @throws IOException If problems occur reading filter binary file generated by buildFilter()
     */
    private List<String> checkFilterFor(List<String> elementsToCheck) throws IOException {
        // read the dictionary from memory
        DictionaryData dData = Read.dictFromCompiledSource(COMPILED_DICTIONARY_PATH);

        verifyHeader(dData.header);

        // build the filter
        filter = BloomFilter.build(dData.dictionary, dData.header.getNHashFunctions());

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

    private void verifyHeader(BuildInfo other) {
        if (null == other) {
            throw new Error("Incorrect binary file passed to program.");
        }
        if (!isCorrectVersion(other)) {
            throw new Error("Incorrect program version from compiled dictionary.");
        }
    }

    private boolean isCorrectVersion(BuildInfo other) {
        return other.getVersion() == this.versionInfo.getVersion();
    }
}
