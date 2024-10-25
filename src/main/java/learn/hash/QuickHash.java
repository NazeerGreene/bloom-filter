package learn.hash;

/*
* The interface that all hashing algorithms for the bloom
* filter should implement.
*
* hash() should return a singular hash value for a byte stream
* hash_k_times() should return a list of hashed values for a byte stream where
* each hash will the result of applying the seed.
* */
public interface QuickHash {
    long hash(byte[] data, int seed);
    long[] hash_k_times(byte[] data, int[] seeds);
}
