package learn.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public final class BuildInfo {
    public static final String DEFAULT_IDENTIFIER = "BFSC";

    // IMPLEMENTATION-DEPENDENT
    private short version;
    private short nHashFunctions;
    private int bloomFilterBitsRequired;

    public BuildInfo() {}

    public BuildInfo setVersion(short version) {
        this.version = version;
        return this;
    }

    public BuildInfo setHashFunctions(short nHashFunctions) {
        this.nHashFunctions = nHashFunctions;
        return this;
    }

    public BuildInfo setBloomFilterBitsRequired(int bloomFilterBitsRequired) {
        this.bloomFilterBitsRequired = bloomFilterBitsRequired;
        return this;
    }

    public short getNHashFunctions() {
        return nHashFunctions;
    }

    public short getVersion() {
        return version;
    }

    public int getBloomFilterBitsRequired() {
        return bloomFilterBitsRequired;
    }

    public static int headerByteSize() {
        return BuildInfo.DEFAULT_IDENTIFIER.length() + 2 * Short.BYTES + Integer.BYTES;
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
        ByteBuffer header = ByteBuffer.allocate(BuildInfo.headerByteSize());

        // write
        header.put(BuildInfo.DEFAULT_IDENTIFIER.getBytes(StandardCharsets.US_ASCII));
        header.putShort(version);
        header.putShort(nHashFunctions);
        header.putInt(bloomFilterBitsRequired);


        return header.array();
    }

    /**
     * Returns a BuildInfo containing the data for a bloom filter from a byte header
     * @param data The byte header to read
     * @return BuildInfo If read is successful
     *         null If first four bytes don't match the program identifier (BuildInfo.DEFAULT_IDENTIFIER)
     */
    public static BuildInfo readBuildInfo(byte[] data) {
        BuildInfo header = new BuildInfo();

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

        // read the data
        String iden = "" +
                (char)buffer.get() +
                (char)buffer.get() +
                (char)buffer.get() +
                (char)buffer.get();

        short version = buffer.getShort();
        short nHashes = buffer.getShort();
        int bitsRequired = buffer.getInt();

        if (!iden.equals(BuildInfo.DEFAULT_IDENTIFIER)) {
            return null;
        }

        header.setVersion(version);
        header.setHashFunctions(nHashes);
        header.setBloomFilterBitsRequired(bitsRequired);

        return header;
    }
}
