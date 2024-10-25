/*
*   Fowler-Noll-Vo hash function
*   Version FNV-1a
*
*   Fast computation but
*   vulnerable to brute-force collisions.
* */

package learn.hash;

import java.nio.ByteBuffer;

public class FNV1A64 implements QuickHash {
    // these constants are mathematically derived for 64-bit length hashes
    private static final long FNV_OFFSET_BASIS_64 = 0xcbf29ce484222325L;
    private static final long FNV_PRIME_64 = 0x100000001b3L;

    @Override
    public long hash(byte[] data, int seed) {
        // the seed doesn't matter in this case
        long hash = FNV_OFFSET_BASIS_64;

        for (byte b: data) {
            // conversion necessary since java has no unsigned modifiers
            hash = hash ^ (b & 0xFF);
            hash = hash * FNV_PRIME_64;
        }

        return hash;
    }

    @Override
    public long[] hash_k_times(byte[] data, int[] seeds) {
        long[] hashValues = new long[seeds.length];

        for (int i = 0; i < seeds.length; i++) {
            hashValues[i] = getHashWithIdentifier(data, i);
        }

        return hashValues;
    }

    /* Helper Function
     * The FNV hashing algorithm doesn't use seeds to create its hash values,
     * so we will instead create a hash, write the identifier into the hash,
     * and then re-hash; this creates a unique hash value.
     * */
    public long getHashWithIdentifier(byte[] data, int identifier) {
        // XORing the identifier is enough to make it enough
        long hashValue = this.hash(data, 0) ^ identifier;
        return this.hash(longToByteArray(hashValue), 0);
    }

    public static byte[] longToByteArray(long value) {
        // java uses big-endian order by default
        // allocation overhead could be costly for 100s of 1000s of calls.
        return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
    }
}