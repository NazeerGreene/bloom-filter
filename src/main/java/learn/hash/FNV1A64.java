package learn.hash;

import java.nio.ByteBuffer;

/**
 *   Fowler-Noll-Vo hash function
 *   Version FNV-1a
 *
 *   Fast computation but
 *   vulnerable to brute-force collisions.
 */
public class FNV1A64 {
    // these constants are mathematically derived for 64-bit length hashes
    private static final long FNV_OFFSET_BASIS_64 = 0xcbf29ce484222325L;
    private static final long FNV_PRIME_64 = 0x100000001b3L;

    private FNV1A64() {};

    public static long hash(byte[] data, int seed) throws IllegalArgumentException {
        if (null == data) {
            throw new IllegalArgumentException("byte stream cannot be null");
        }

        long firstPass = hashByteStream(data) ^ seed;

        return hashByteStream(longToByteArray(firstPass));
    }

    private static long hashByteStream(byte[] data) {
        long hash = FNV_OFFSET_BASIS_64;

        for (byte b: data) {
            // conversion necessary since java has no unsigned modifiers
            hash = hash ^ (b & 0xFF);
            hash = hash * FNV_PRIME_64;
        }

        return hash;
    }

    // Helper function: long to byte array converter
    private static byte[] longToByteArray(long value) {
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) ((value >> (56 - (i * 8))) & 0xFF);
        }
        return result;
    }
}