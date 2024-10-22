package learn;

public class Helpers {
    // calculate bit array size
    // using this equation
    // size = - n * ln(p) / (ln 2)^2
    // where:
    // double p = desired false positive
    // int n = number of elements inserted
    public static int calculateBitArraySize(double p, int n) {
        double bitArraySize = - n * Math.log(p) / Math.pow(Math.log(2), 2);
        return (int) Math.ceil(bitArraySize);
    }
    // calculate optimal number of hash functions
    // using this equation
    // num of hash = m * ln(2) / n
    // where:
    // int m = bit array size
    // int n = number of elements inserted
    public static int calculateNumOfHashFunctions(int m, int n) {
        double optimalHashes = m * Math.log(2) / n;
        return (int) Math.floor(optimalHashes);
    }
}
