package learn.utils;

import learn.hash.FNV1A64;
import learn.hash.QuickHash;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

public class BloomFilter {
    private BitSet bitArray;
    private final double DFP; // desired false positive probability
    private int[] seeds;
    private QuickHash quickHash;

    public static double DFP_MAX = 1.0;
    public static double DFP_MIN =  0.0;
    public static double DFP_DEFAULT = 0.01;

    private BloomFilter(double dfp, int[] seeds, BitSet bitArray, QuickHash quickHash) {
        this.DFP = isValidDfp(dfp) ? dfp : DFP_DEFAULT;
        this.seeds = seeds;
        this.bitArray = bitArray;
        this.quickHash = quickHash;
    }

    public BloomFilter build(double dfp, int nElements) {
        int nBits = calculateBitArraySize(dfp, nElements);
        int nHashes = calculateNumOfHashFunctions(nBits, nElements);

        BitSet bitArray = new BitSet(nBits);
        bitArray.clear();

        int[] seeds = IntStream.rangeClosed(1, nHashes).toArray();

        return new BloomFilter(dfp, seeds, bitArray, FNV1A64::hash);
    }

    public BloomFilter build(byte[] data, int nSeeds) {
        nSeeds = Math.max(nSeeds, 2);
        int[] seeds = IntStream.rangeClosed(1, nSeeds).toArray();

        BitSet bitArray = BitSet.valueOf(data);

        return new BloomFilter(DFP_DEFAULT, seeds, bitArray, FNV1A64::hash);
    }

    /**
     * Returns the desired false positive probability
     */
    public double getDFP() {
        return DFP;
    }

    /**
     * Returns the seeds used for the hashing algorithm implemented by QuickHash
     */
    public int[] getSeeds() {
        return Arrays.copyOf(seeds, seeds.length);
    }

    public void withSeeds(int[] newSeeds) {
        if (newSeeds != null && newSeeds.length > 1) {
            this.seeds = Arrays.copyOf(newSeeds, newSeeds.length);
        }
    }

    /**
     * Returns the underlying bit array
     */
    public BitSet getBitArray() {
        return this.bitArray;
    }

    /**
     * Adds a new member to the member set
     * @param element The new member to add
     */
    public void add(String element) {
        verifyState(element);

        long[] hashes = this.hash_k_times(element.getBytes(StandardCharsets.UTF_8), seeds);

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
        verifyState(element);

        long[] hashes = this.hash_k_times(element.getBytes(StandardCharsets.UTF_8), seeds);

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

    private boolean isValidDfp(double dfp) {
        return dfp >= DFP_MIN && dfp <= DFP_MAX;
    }

    private void verifyState(String element) {
        if (null == this.bitArray) {
            throw new IllegalStateException("Bloom filter not initialized. Call build() first.");
        }

        if (null == element) {
            throw new IllegalArgumentException("element cannot be null");
        }
    }

    private int getIndexFromHash(long hash) {
        // Handle potential integer overflow and negative numbers
        return Math.abs((int) ((hash & 0x7FFFFFFFFFFFFFFFL) % bitArray.size()));
    }

    private long[] hash_k_times(byte[] data, int[] seeds) {
        long[] hashValues = new long[seeds.length];

        for (int i = 0; i < seeds.length; i++) {
            hashValues[i] = this.quickHash.hash(data, seeds[i]);
        }

        return hashValues;
    }

    /**
     * Calculate the bit array size according to the following equation: -nElements * ln(dfp) / (ln 2)^2
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
     * @param DFP The desired false positive probability for a bloom filter
     */
    public static void outputAppRequirements(int nElements, double DFP) {

        double bitsPerByte = 8.0;     // bits per Byte
        double bytesPerKb = 1024.0; // bytes per Kilobyte

        int bitsRequired = calculateBitArraySize(DFP, nElements);
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
                            \t\t... %d KB
                            """,
                nElements, DFP, nHashFunctions, bitsRequired, bytesRequired, mbRequired);

        System.out.println(out);
    }
}