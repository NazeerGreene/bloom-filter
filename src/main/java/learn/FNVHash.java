/*
*   Fowler-Noll-Vo hash function
*   Version FNV-1a
*
*   Fast computation but
*   vulnerable to brute-force collisions.
* */

package learn;

public class FNVHash {
    private static final long FNV_OFFSET_BASIS_64 = 0xcbf29ce484222325L;
    private static final long FNV_PRIME_64 = 0x100000001b3L;

    public static long fnv1a64(byte[] data) {
        long hash = FNV_OFFSET_BASIS_64;

        for (byte b: data) {
            // conversion necessary since java has no unsigned modifiers
            hash = hash ^ (b & 0xFF);
            hash = hash * FNV_PRIME_64;
        }

        return hash;
    }
}
