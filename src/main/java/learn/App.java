package learn;

import java.util.BitSet;

public class App {
    public static void main(String[] args) {
        int nElementsInserted = 235_976;
        double DSF = 0.01; // desired false positive probability

        // for development
        outputAppRequirements(nElementsInserted, DSF);

        // STEP 1
        // create a bit array and set bits to zero
        int bitsRequired = Helpers.calculateBitArraySize(DSF, nElementsInserted);
        BitSet bitArray = new BitSet(bitsRequired);
        bitArray.clear();


        // hash items for bit array and set corresponding bits
        String test = "Hello World!";
        byte[] bytes = test.getBytes();

        long hashed = FNVHash.fnv1a64(bytes);
        System.out.printf("Hash is %x%n", hashed);

        // query for item to determine if query is working
    }

    public static void outputAppRequirements(int nElements, double DSF) {
        /* Outputs basic memory requirements for bloom filter
        * nElements - number of elements in dictionary for filter
        * DSF - 'Desired False Positive' probability for bloom filter
        * */
        double bitsPerByte = 8.0;     // bits per Mb
        double bytesPerMb = 1024.0; // bytes per Mb

        int bitsRequired = Helpers.calculateBitArraySize(DSF, nElements);
        int bytesRequired = (int) Math.ceil(bitsRequired / bitsPerByte);
        int mbRequired = (int) Math.ceil(bytesRequired / bytesPerMb);

        int nHashFunctions = Helpers.calculateNumOfHashFunctions(bitsRequired, nElements);

        String out = String.format("""
                        Application Requirements
                        \tElements inserted:  %d
                        \tDesired False P:    %.2f
                        \tHash functions:     %d
                        \tNumber of bits:     %d
                        \t\t... %d Bytes
                        \t\t... %d Mb
                        """,
                nElements, DSF, nHashFunctions, bitsRequired, bytesRequired, mbRequired);

        System.out.println(out);
    }
}
