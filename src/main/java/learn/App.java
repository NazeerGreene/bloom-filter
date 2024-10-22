package learn;

public class App {
    public static void main(String[] args) {
        long nElementsInserted = 1000;
        double DSF = 0.01; // desired false positivity probability
        double bPerB = 8.0; // bits per Mb

        long sBitArray = Helpers.calculateBitArraySize(DSF, nElementsInserted);
        System.out.println("The bit array size for " + nElementsInserted + " at " + DSF + ": " + sBitArray + " bits.");
        System.out.println("Or... " + (long) Math.ceil(sBitArray/bPerB) + " bytes of memory");

        long nHashFunctions = Helpers.calculateNumOfHashFunctions(sBitArray, nElementsInserted);
        System.out.println("The optimal hash functions for " + nHashFunctions + " with a bit array size of " + sBitArray + ": " + nHashFunctions + " hashes.");
    }
}
