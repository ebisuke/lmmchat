package jp.mochisuke.lmmchat.sounds;

public class TTSResult {
    public String text;
    public String filename;
    public byte[] raw_data;
    public TTSResult(String text, String filename, byte[] raw_data) {
        this.text = text;
        this.filename = filename;
        this.raw_data = raw_data;
    }
}
