package learn;

import learn.hash.QuickHash;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;

public class BloomFilter {
    private BitSet bitArray;
    private final double DSF; // desired false positive probability
    private final int[] seeds;
    private final QuickHash quickHash;

    public static double DSF_MAX = 1.0;
    public static double DSF_MIN =  0.0;
    public static double DSF_DEFAULT = 0.01;

    public BloomFilter(double DSF, QuickHash quickHash, int[] seeds) {
        this.DSF = isValidDsf(DSF) ?  DSF : DSF_DEFAULT;
        this.seeds = seeds;
        this.quickHash = quickHash;
        this.bitArray = null;
    }

    public BloomFilter(BloomFilter other) {
        this.DSF = other.DSF;
        this.seeds = other.getSeeds();
        this.quickHash = other.quickHash;
        this.bitArray = null;
    }

    // Returns the desired false positive probability
    public double getDSF() {
        return DSF;
    }

    /**
     * Returns the seeds used for the hashing algorithm implemented by QuickHash
     */
    public int[] getSeeds() {
        return Arrays.copyOf(seeds, seeds.length);
    }

    /**
     * Builds the underlying bit array for the Bloom filter
     * @param nElements The number of total elements expected in the member set
     * @return true If the bit array was successfully allocated and cleared
     *         false If the bit array was not successfully allocated
     */
    public boolean build(int nElements) {
        int bitsRequired = calculateBitArraySize(DSF, nElements);
        try {
            this.bitArray = new BitSet(bitsRequired);
            bitArray.clear();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Sets the underlying bit array to the new bit array without occupying new memory
     * @param set The new BitSet
     */
    public boolean build(BitSet set) {
        this.bitArray = set;
        return true;
    }

    /**
     * Adds a new member to the member set
     * @param element The new member to add
     */
    public void add(String element) {
        if (bitArray == null) {
            throw new IllegalStateException("Bloom filter not initialized. Call build() first.");
        }

        long[] hashes = quickHash.hash_k_times(element.getBytes(StandardCharsets.UTF_8), seeds);

        for (long hash: hashes) {
            int index = getIndexFromHash(hash);
            bitArray.set(index, true);
//            System.out.printf("Hash: %d, Index: %d, Bit: %b%n",
//                hash, index, bitArray.get(index));
        }
    }


    /**
     * Queries the member set to check for an element
     * @param element The element to query
     * @return true The element exists in the set
     *         false The element does not exist in set
     */
    public boolean contains(String element) {
        if (bitArray == null) {
            throw new IllegalStateException("Bloom filter not initialized. Call build() first.");
        }

        long[] hashes = quickHash.hash_k_times(element.getBytes(StandardCharsets.UTF_8), seeds);

        for (long hash: hashes) {
            int index = getIndexFromHash(hash);
//            System.out.printf("Hash: %d, Index: %d, Bit: %b%n",
//                hash, index, bitArray.get(index));
            if (!bitArray.get(index)) {
               return false;
            }
        }

        return true;
    }

    // ----------------------------- HELPERS -----------------------------

    private boolean isValidDsf(double dsf) {
        return dsf >= DSF_MIN && dsf <= DSF_MAX;
    }

    private int getIndexFromHash(long hash) {
        // Handle potential integer overflow and negative numbers
        return Math.abs((int) ((hash & 0x7FFFFFFFFFFFFFFFL) % bitArray.size()));
    }

    /**
     * Calculate the bit array size according to the following equation: -nElements * ln(dsf) / (ln 2)^2
     * @param desiredFalsePositive The desired false positive probability
     * @param nElements The number of elements inserted
     * @return The suggested size of the bit array for the Bloom filter
     */
    public static int calculateBitArraySize(double desiredFalsePositive, int nElements) {
        // Input validation
        if (desiredFalsePositive <= 0 || desiredFalsePositive >= 1) {
            throw new IllegalArgumentException("False positive rate must be between 0 and 1");
        }
        if (nElements <= 0) {
            throw new IllegalArgumentException("Number of elements must be positive");
        }

        double bitArraySize = - nElements * Math.log(desiredFalsePositive) / Math.pow(Math.log(2), 2);
        return (int) Math.ceil(bitArraySize);
    }

    /**
     * Calculate the optimal number of hash functions using the equation: bitArraySize * ln(2) / nElements
     * @param bitArraySize The size of the bit array for the Bloom filter
     * @param nElements The number of elements inserted
     */
    public static int calculateNumOfHashFunctions(int bitArraySize, int nElements) {
        if (bitArraySize <= 0) {
            throw new IllegalArgumentException("Bit array size must be positive");
        }
        if (nElements <= 0) {
            throw new IllegalArgumentException("Number of elements must be positive");
        }

        double optimalHashes = (double) bitArraySize / nElements * Math.log(2);
        return (int) Math.ceil(optimalHashes);
    }
//    NOTE
//    The actual number of hash functions greatly affects the Bloom filter's performance:
//    Too few: More false positives
//    Too many: Slower performance and potentially more bits set than necessary

    /**
     * Outputs basic memory requirements for bloom filter to stdin
     * @param nElements The number of elements inserted
     * @param DSF  The desired false positive probability for a bloom filter
     */
    public static void outputAppRequirements(int nElements, double DSF) {

        double bitsPerByte = 8.0;     // bits per Byte
        double bytesPerKb = 1024.0; // bytes per Kilobyte

        int bitsRequired = calculateBitArraySize(DSF, nElements);
        int bytesRequired = (int) Math.ceil(bitsRequired / bitsPerByte);
        int mbRequired = (int) Math.ceil(bytesRequired / bytesPerKb);

        int nHashFunctions = calculateNumOfHashFunctions(bitsRequired, nElements);

        String out = String.format("""
                            Application Requirements
                            \tElements inserted:  %d
                            \tDesired False P:    %.2f
                            \tHash functions:     %d
                            \tNumber of bits:     %d
                            \t\t... %d Bytes
                            \t\t... %d Kb
                            """,
                nElements, DSF, nHashFunctions, bitsRequired, bytesRequired, mbRequired);

        System.out.println(out);
    }
}