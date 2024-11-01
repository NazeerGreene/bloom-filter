package learn.hash;

/**
* The interface for hashing algorithms to implement
* for the bloom filter.
*/
public interface QuickHash {
    /**
     * Produce a hash value for stream of bytes
     * @param data The stream of bytes to be hashed
     * @param seed The seed for the underlying hash value
     * @return The hashed value for the byte stream
     */
    long hash(byte[] data, int seed);
    /**
     * Returns a list of hashed values for the given byte stream after the use of each seed
     * @param data The stream of bytes to be hashed
     * @param seeds The list of seeds to be used at each application of the hash
     * @return The list of hashed values equal to the size of the seeds list
     */
    long[] hash_k_times(byte[] data, int[] seeds);
}
