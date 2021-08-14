package me.mrcavas.mcmessage.db;

public class DBPlayer {
    public long pid;
    public long lid;
    public String pname;

    public DBPlayer(long pid, long lid, String pname) {
        this.lid = lid;
        this.pid = pid;
        this.pname = pname;
    }
}
