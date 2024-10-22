package learn;

public class App {
    public static void main(String[] args) {
        outputAppRequirements();
    }

    public static void outputAppRequirements() {
        long nElementsInserted = 235_976;
        double DSF = 0.01;      // desired false positive probability
        double bitsPerByte = 8.0;     // bits per Mb
        double bytesPerMb = 1024.0; // bytes per Mb

        long bitsRequired = Helpers.calculateBitArraySize(DSF, nElementsInserted);
        int bytesRequired = (int) Math.ceil(bitsRequired / bitsPerByte);
        int mbRequired = (int) Math.ceil(bytesRequired / bytesPerMb);

        long nHashFunctions = Helpers.calculateNumOfHashFunctions(bitsRequired, nElementsInserted);


        String out = String.format("Application Requirements\n" +
                "\tElements inserted:  %d\n" +
                "\tDesired False P:    %.2f\n" +
                "\tHash functions:     %d\n" +
                "\tNumber of bits:     %d\n" +
                "\t\t... %d Bytes\n" +
                "\t\t... %d Mb\n",
                nElementsInserted, DSF, nHashFunctions, bitsRequired, bytesRequired, mbRequired);

        System.out.println(out);
    }

    // create a bit array and set bits to zero



    // hash items for bit array and set corresponding bits

    // query for item to determine if query is working
}
