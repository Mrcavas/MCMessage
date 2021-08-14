package me.mrcavas.mcmessage.db;

public class DBMessage {
    public long id;
    public boolean isClient;
    public String text;
    public DBPlayer player;

    public DBMessage(long id, boolean isClient, String text, DBPlayer player) {
        this.id = id;
        this.isClient = isClient;
        this.text = text;
        this.player = player;
    }
}
