package learn;

import learn.controller.Controller;
import learn.dictionary.Read;
import learn.dictionary.Write;
import learn.hash.FNV1A64;
import learn.utils.BloomFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        controllerRun();
    }

    public static void controllerRun() {
        try{
            Controller controller = new Controller();
            controller.run(List.of());
        } catch (IOException e) {
            System.out.println("Error while running application: " + e.getMessage());
        }
    }

    public static void manualTesting() throws IOException {
        String filePath = "./data/test/dict-sub.txt";
        String compiledFilePath = "./data/test/dict-compiled-a.bf";
        int[] seeds = new int[]{1,2,3,4,5,6,7,8,9,10};

        // build the filter
        int nElements = Read.countNewlines(filePath);
        double dsf = 0.01;
        int bitsRequired = BloomFilter.calculateBitArraySize(dsf, nElements);
        int nHashFunctions = BloomFilter.calculateNumOfHashFunctions(bitsRequired, nElements);

        BloomFilter filter = new BloomFilter(dsf, FNV1A64::hash, Arrays.copyOf(seeds, nHashFunctions));
        filter.build(nElements);

        // first pass - reading all elements in dict to bloom filter
        Read.dictFromRawSource(filePath, filter);

        // now check for some values we know to be present
        List<String> to_test = List.of("aardvark", "abduction", "absconce", "zoo");

        for(String element: to_test) {
            System.out.println("Querying for '" + element + ": " + filter.contains(element));
        }

        // now we need to save it to a file
        Write.dictToBinaryFile(compiledFilePath, null, filter.getBitArray().toByteArray());

        // now we need to read from file to make sure save is working properly
        BloomFilter newFilter = new BloomFilter(dsf, FNV1A64::hash, Arrays.copyOf(seeds, nHashFunctions));
        byte[] data = Read.dictFromCompiledSource(compiledFilePath);
        newFilter.build(BitSet.valueOf(data));

        // now check for some values we know to be present
        for(String element: to_test) {
            System.out.println("Querying for '" + element + ": " + filter.contains(element));
        }
    }


}
