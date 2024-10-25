package learn;

import learn.hash.QuickHash;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

public class BloomFilter {
    private BitSet bitArray;
    private final double DSF; // desired false positive probability
    private final int numHashFunctions;
    private final QuickHash quickHash;

    public static double DSF_MAX = 1.0;
    public static double DSF_MIN =  Double.MIN_VALUE;
    public static double DSF_DEFAULT = 0.01;
    public static int N_HASHFUNCTION_DEFAULT = 1;

    public BloomFilter(double DSF, int numHashFunctions) {
        this.DSF = isValidDsf(DSF) ?  DSF : DSF_DEFAULT;
        this.numHashFunctions = Math.max(N_HASHFUNCTION_DEFAULT, numHashFunctions);
        this.bitArray = null;
    }

    public BloomFilter setDsfTo(double newDsf) {

    }

    public double getDsf() {
        return 0.0;
    }

    public BloomFilter setNumHashFunctionsTo(int newNum) {

    }

    public int getNumHashFunctions() {
        return 0;
    }

    public BloomFilter build(List<String> elements) {
        return null;
    }

    public BloomFilter build(Path path_to_elements) {
        return null;
    }

    public void add(String element) {

    }

    public boolean contains(String element) {
        return false;
    }


    // ----------------------------- HELPERS -----------------------------

    private boolean isValidDsf(double dsf) {
        return dsf >= DSF_MIN && dsf <= DSF_MAX;
    }

    // calculate bit array size
    // using this equation
    // size = - n * ln(p) / (ln 2)^2
    // where:
    // double p = desired false positive
    // int n = number of elements inserted
    public static int calculateBitArraySize(double p, int n) {
        double bitArraySize = - n * Math.log(p) / Math.pow(Math.log(2), 2);
        return (int) Math.ceil(bitArraySize);
    }
    // calculate optimal number of hash functions
    // using this equation
    // num of hash = m * ln(2) / n
    // where:
    // int m = bit array size
    // int n = number of elements inserted
    public static int calculateNumOfHashFunctions(int m, int n) {
        double optimalHashes = m * Math.log(2) / n;
        return (int) Math.floor(optimalHashes);
    }
}


//public static void main(String[] args) {
////        int nElementsInserted = 235_976;
//    int nElementsInserted = 10_000;
//
//    // for development
//    outputAppRequirements(nElementsInserted, DSF);
//
//    // STEP 1
//    // create a bit array and set bits to zero
//    int bitsRequired = Helpers.calculateBitArraySize(DSF, nElementsInserted);
//    BitSet bitArray = new BitSet(bitsRequired);
//    bitArray.clear();
//
//
//    // hash items for bit array and set corresponding bits
//    String test = "Hello";
//    byte[] bytes = test.getBytes(StandardCharsets.UTF_8);
//
//
//    // First pass - setting bits
//    long[] hashes = hash_k_times(bytes, 6);
//    System.out.println("Setting bits:");
//    for (long hash: hashes) {
//        // Proper modulo handling for long to int conversion
//        int index = getIndexFromHash(hash, bitsRequired);
//        bitArray.set(index, true);
//        System.out.printf("Hash: %d, Index: %d, Bit: %b%n",
//                hash, index, bitArray.get(index));
//    }
//
//    // Second pass - checking bits
//    System.out.println("\nChecking bits:");
//    byte[] query = test.getBytes(StandardCharsets.UTF_8);
//    long[] queryHashes = hash_k_times(query, 6);
//
//    for (long hash: queryHashes) {
//        int index = getIndexFromHash(hash, bitsRequired);
//        System.out.printf("Hash: %d, Index: %d, Bit: %b%n",
//                hash, index, bitArray.get(index));
//    }
//
//
//}
//

//
//public static void outputAppRequirements(int nElements, double DSF) {
//    /* Outputs basic memory requirements for bloom filter
//     * nElements - number of elements in dictionary for filter
//     * DSF - 'Desired False Positive' probability for bloom filter
//     * */
//    double bitsPerByte = 8.0;     // bits per Mb
//    double bytesPerMb = 1024.0; // bytes per Mb
//
//    int bitsRequired = Helpers.calculateBitArraySize(DSF, nElements);
//    int bytesRequired = (int) Math.ceil(bitsRequired / bitsPerByte);
//    int mbRequired = (int) Math.ceil(bytesRequired / bytesPerMb);
//
//    int nHashFunctions = Helpers.calculateNumOfHashFunctions(bitsRequired, nElements);
//
//    String out = String.format("""
//                        Application Requirements
//                        \tElements inserted:  %d
//                        \tDesired False P:    %.2f
//                        \tHash functions:     %d
//                        \tNumber of bits:     %d
//                        \t\t... %d Bytes
//                        \t\t... %d Mb
//                        """,
//            nElements, DSF, nHashFunctions, bitsRequired, bytesRequired, mbRequired);
//
//    System.out.println(out);
//}
