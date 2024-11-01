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
    public static double DSF_MIN =  Double.MIN_VALUE;
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

    // Returns the seeds used for the hashing algorithm
    // implemented by QuickHash
    public int[] getSeeds() {
        return Arrays.copyOf(seeds, seeds.length);
    }

    // Takes size of element list (nElements) and builds the
    // bit array based on the DSF and number of elements expected.
    // true - if the bit array was successfully allocated and cleared
    // false - if the bit array was unsuccessfully allocated
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

    // Takes a pre-built bit array and sets this filter's bit array
    // to the new set. Does not allocate new space. The new set
    // should not be modified after passing to build method.
    public boolean build(BitSet set) {
        this.bitArray = set;
        return true;
    }

    // Adds a new member to the member set.
    // The member set must be built first,
    // no element rejected otherwise.
    public boolean add(String element) {
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

        return true;
    }

    // Queries member set to check for element.
    // true - element exists in set
    // false - element does not exist in set
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

    // calculate bit array size
    // using this equation
    // size = - n * ln(p) / (ln 2)^2
    // where:
    // double p = desired false positive
    // int n = number of elements inserted
    public static int calculateBitArraySize(double p, int n) {
        // Input validation
        if (p <= 0 || p >= 1) {
            throw new IllegalArgumentException("False positive rate must be between 0 and 1");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Number of elements must be positive");
        }

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
        if (m <= 0) {
            throw new IllegalArgumentException("Bit array size must be positive");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Number of elements must be positive");
        }

        double optimalHashes = (double) m / n * Math.log(2);
        return (int) Math.ceil(optimalHashes);
    }
//    NOTE
//    The actual number of hash functions greatly affects the Bloom filter's performance:
//    Too few: More false positives
//    Too many: Slower performance and potentially more bits set than necessary

    // Outputs basic memory requirements for bloom filter
    // nElements - number of elements in dictionary for filter
    // DSF - 'Desired False Positive' probability for bloom filter
    public static void outputAppRequirements(int nElements, double DSF) {

        double bitsPerByte = 8.0;     // bits per Mb
        double bytesPerMb = 1024.0; // bytes per Mb

        int bitsRequired = calculateBitArraySize(DSF, nElements);
        int bytesRequired = (int) Math.ceil(bitsRequired / bitsPerByte);
        int mbRequired = (int) Math.ceil(bytesRequired / bytesPerMb);

        int nHashFunctions = calculateNumOfHashFunctions(bitsRequired, nElements);

        String out = String.format("""
                            Application Requirements
                            \tElements inserted:  %d
                            \tDesired False P:    %.2f
                            \tHash functions:     %d
                            \tNumber of bits:     %d
                            \t\t... %d Bytes
                            \t\t... %d Mb
                            """,
                nElements, DSF, nHashFunctions, bitsRequired, bytesRequired, mbRequired);

        System.out.println(out);
    }
}