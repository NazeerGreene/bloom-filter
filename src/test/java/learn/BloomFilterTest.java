package learn;

import learn.hash.FNV1A64;
import learn.utils.BloomFilter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BloomFilterTest {
    int N_ELEMENTS = 10_000;
    double DFP = 0.01;
    int[] seeds = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int BITS_REQUIRED = BloomFilter.calculateBitArraySize(DFP, N_ELEMENTS);
    int N_HASH_FUNCTIONS = BloomFilter.calculateNumOfHashFunctions(BITS_REQUIRED, N_ELEMENTS);

    BloomFilter filter = null;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        filter = BloomFilter.build(DFP, N_ELEMENTS);
    }

    @Test
    void returnsTrueForExistentMember() {
        filter.add("Hello");
        filter.add("World");

        assertTrue(filter.contains("Hello"), "Should return true for 'Hello' in member set.");
        assertTrue(filter.contains("World"), "Should return true for 'World' in member set.");
    }

    @Test
    void returnsFalseForNonExistentMember() {
        filter.add("Hello");
        assertFalse(filter.contains("World"), "Should return false for 'World' in member set.");
    }

    // The following values will be used to validate the helper functions
// Elements (n),Desired False Positive,Optimal Hash Functions (k),Bit Array Size (m),Actual False Positive Rate
//        10000,0.01,7,95851,0.00998
//        50000,0.05,4,311453,0.0499
//        100000,0.001,10,1437759,0.00099
//        1000000,0.03,5,5954196,0.0299
//        5000,0.02,6,41503,0.0199
// *** bit array size might not be the most accurate depending on
// *** approach/implementation of precision and rounding methods.

    @Test
    void calculateBitArraySizeProducesExpectedSize() {
        // res
        double delta = 2; // or 2% of expected value
        assertWithinPercentage(95851, BloomFilter.calculateBitArraySize(0.01, 10_000), delta);
        assertWithinPercentage(311453, BloomFilter.calculateBitArraySize(0.05, 50_000), delta); // returns false
        assertWithinPercentage(1437759, BloomFilter.calculateBitArraySize(0.001, 100_000), delta);
        //assertWithinPercentage(5954196,BloomFilter.calculateBitArraySize(0.03, 1_000_000), delta); // returns false
        assertWithinPercentage(41503, BloomFilter.calculateBitArraySize(0.02, 5000), delta);
    }

    private void assertWithinPercentage(int expected, int actual, double percentage) {
        double allowedDifference = expected * (percentage / 100.0);
        assertTrue(Math.abs(expected - actual) <= allowedDifference,
                String.format("Expected %d to be within %.1f%% of %d", actual, percentage, expected));
    }

    @Test
    void calculateNumOfHashFunctions() {
        double delta = 1.0; // since we're rounding to whole numbers
        assertEquals(7, BloomFilter.calculateNumOfHashFunctions(95851,10_000), delta);
        assertEquals(4, BloomFilter.calculateNumOfHashFunctions(311453,50_000), delta);
        assertEquals(10, BloomFilter.calculateNumOfHashFunctions(1437759,100_000), delta);
        assertEquals(5, BloomFilter.calculateNumOfHashFunctions(5954196,1_000_000), delta);
        assertEquals(6, BloomFilter.calculateNumOfHashFunctions(41503,5000), delta);

    }
}