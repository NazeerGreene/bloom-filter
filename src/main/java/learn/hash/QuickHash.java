package learn.hash;

/*
* The interface for hashing algorithms to implement
* for the bloom filter.
* */
public interface QuickHash {
    // hash() should return a singular hash value for a byte stream
    long hash(byte[] data, int seed);
    // hash_k_times() should return a list of hashed values for a byte stream where
    // each hash will the result of applying the seed.
    long[] hash_k_times(byte[] data, int[] seeds);
}
