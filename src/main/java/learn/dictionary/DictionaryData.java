package learn.dictionary;

import learn.utils.BuildInfo;

public class DictionaryData {
    public BuildInfo header;
    public byte[] dictionary;

    public DictionaryData(BuildInfo header, byte[] dictionary) {
        this.header = header;
        this.dictionary = dictionary;
    }
}
