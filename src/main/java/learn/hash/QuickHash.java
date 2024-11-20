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
}
