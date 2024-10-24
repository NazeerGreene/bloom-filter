package learn;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;

public class App {
    public static void main(String[] args) {
//        int nElementsInserted = 235_976;
        int nElementsInserted = 10_000;

        double DSF = 0.01; // desired false positive probability

        // for development
        outputAppRequirements(nElementsInserted, DSF);

        // STEP 1
        // create a bit array and set bits to zero
        int bitsRequired = Helpers.calculateBitArraySize(DSF, nElementsInserted);
        BitSet bitArray = new BitSet(bitsRequired);
        bitArray.clear();


        // hash items for bit array and set corresponding bits
        String test = "Hello";
        byte[] bytes = test.getBytes(StandardCharsets.UTF_8);


        // First pass - setting bits
        long[] hashes = hash_k_times(bytes, 6);
        System.out.println("Setting bits:");
        for (long hash: hashes) {
            // Proper modulo handling for long to int conversion
            int index = getIndexFromHash(hash, bitsRequired);
            bitArray.set(index, true);
            System.out.printf("Hash: %d, Index: %d, Bit: %b%n",
                    hash, index, bitArray.get(index));
        }

        // Second pass - checking bits
        System.out.println("\nChecking bits:");
        byte[] query = test.getBytes(StandardCharsets.UTF_8);
        long[] queryHashes = hash_k_times(query, 6);

        for (long hash: queryHashes) {
            int index = getIndexFromHash(hash, bitsRequired);
            System.out.printf("Hash: %d, Index: %d, Bit: %b%n",
                    hash, index, bitArray.get(index));
        }


    }

    public static long[] hash_k_times(byte[] data, int nFunctions) {
        long[] hashValues = new long[nFunctions];

        for (int i = 0; i < nFunctions; i++) {
            hashValues[i] = getHashWithIdentifier(data, i);
        }

        return hashValues;
    }
    /* Helper Function
    * The FNV hashing algorithm doesn't use seeds to create its hash values,
    * so we will instead create a hash, write the identifier into the hash,
    * and then re-hash; this creates a unique hash value.
    * */
    public static long getHashWithIdentifier(byte[] data, int identifier) {
        // XORing the identifier is enough to make it enough
        long hashValue = FNVHash.fnv1a64(data) ^ identifier;
        return FNVHash.fnv1a64(longToByteArray(hashValue));
    }

    public static byte[] longToByteArray(long value) {
        // java uses big-endian order by default
        // allocation overhead could be costly for 100s of 1000s of calls.
        return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
    }

    private static int getIndexFromHash(long hash, int bitsRequired) {
        // Handle potential integer overflow and negative numbers
        return Math.abs((int) ((hash & 0x7FFFFFFFFFFFFFFFL) % bitsRequired));
    }

    public static void outputAppRequirements(int nElements, double DSF) {
        /* Outputs basic memory requirements for bloom filter
        * nElements - number of elements in dictionary for filter
        * DSF - 'Desired False Positive' probability for bloom filter
        * */
        double bitsPerByte = 8.0;     // bits per Mb
        double bytesPerMb = 1024.0; // bytes per Mb

        int bitsRequired = Helpers.calculateBitArraySize(DSF, nElements);
        int bytesRequired = (int) Math.ceil(bitsRequired / bitsPerByte);
        int mbRequired = (int) Math.ceil(bytesRequired / bytesPerMb);

        int nHashFunctions = Helpers.calculateNumOfHashFunctions(bitsRequired, nElements);

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
