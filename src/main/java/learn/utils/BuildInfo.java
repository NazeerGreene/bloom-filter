package learn.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class BuildInfo {
    // CONSTANTS
    public static final String IDENTIFIER = "SCBF"; // must remain 4 bytes
    public static final short VERSION = 1;

    // The bytes required to hold (IDENTIFIER + VERSION + HASH FUNCTIONS + BITS REQUIRED)
    public static final int HEADER_SIZE = 12; // size of (

    // IMPLEMENTATION-DEPENDENT
    private short nHashFunctions;
    private int bloomFilterBitsRequired;

    public BuildInfo(short nHashFunctions, int bitsRequired) {
        this.nHashFunctions = nHashFunctions;
        this.bloomFilterBitsRequired = bitsRequired;
    }

    public void setHashFunctions(short hashFunctions) {
        this.nHashFunctions = hashFunctions;
    }

    public void setBloomFilterBitsRequired(int bloomFilterBitsRequired) {
        this.bloomFilterBitsRequired = bloomFilterBitsRequired;
    }

    /**
     * Generates a sequence of bytes encoded with the build info regarding this bloom filter implementation.
     * The sequence is as follows:
     * bytes 1-4: Program Identifier
     * bytes 5-6: Version Number
     * bytes 7-8: Number of Hash Functions Used by Filter
     * bytes 9-12: Number of Bits Required for Filter
     * @return byte[] byte-encoded header
     */
    public byte[] generateByteHeader() {
        ByteBuffer header = ByteBuffer.allocate(BuildInfo.HEADER_SIZE);

        // write
        header.put(BuildInfo.IDENTIFIER.getBytes(StandardCharsets.UTF_8));
        header.putShort(BuildInfo.VERSION);
        header.putShort(this.nHashFunctions);
        header.putInt(this.bloomFilterBitsRequired);


        return header.array();
    }

    public static BuildInfo readBuildInfo(byte[] header) {
        return null;
    }

}
