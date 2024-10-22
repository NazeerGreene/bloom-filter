package learn;

public class Helpers {
    // calculate bit array size
    // using this equation
    // size = - n * ln(p) / (ln 2)^2
    // where:
    // double p = desired false positive
    // long n = number of elements inserted
    public static long calculateBitArraySize(double p, long n) {
        double bitArraySize = - n * Math.log(p) / Math.pow(Math.log(2), 2);
        return (long) Math.floor(bitArraySize);
    }
    // calculate optimal number of hash functions
    // using this equation
    // num of hash = m * ln(2) / n
    // where:
    // long m = bit array size
    // long n = number of elements inserted
    public static long calculateNumOfHashFunctions(long m, long n) {
        double optimalHashes = m * Math.log(2) / n;
        return (long) Math.floor(optimalHashes);
    }
}
