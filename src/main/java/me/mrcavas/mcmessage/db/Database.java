package me.mrcavas.mcmessage.db;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

//import org.sqlite.JDBC;

import java.io.File;
import java.sql.*;
import java.util.*;

public class Database {
    public static String url;
    private static UUID serverUUID;

    public static void init() {
        File folder = new File(MinecraftClient.getInstance().runDirectory, "mcmessage");
        if (folder.exists() || folder.mkdir()) {
            url = "jdbc:sqlite:" + folder.getPath() + File.separator + "data.db";
            System.out.println("url = " + url);
        }
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS 'players' (" +
                    "'pname' TEXT NOT NULL," +
                    "'pid' INTEGER NOT NULL," +
                    "'lid' INTEGER NOT NULL," +
                    "PRIMARY KEY ('pname')" +
                    ");");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS 'messages' (" +
                    "'pname' TEXT NOT NULL, " +
                    "'mid' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "'client' INTEGER NOT NULL, " +
                    "'mtext' TEXT NULL, " +
                    "'suuid' TEXT NOT NULL, " +
                    "CONSTRAINT '1' FOREIGN KEY ('pname') REFERENCES 'players' ('pname') ON UPDATE NO ACTION ON DELETE NO ACTION " +
                    ");");
            statement.close();
            connection.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public static Connection getConnection() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(url);
    }

    public static void addPlayer(String pname) {
        ArrayList<DBPlayer> players = getPlayers();
        long pid = 0;

        if (players != null) {
            for (DBPlayer player : players) {
                pid = Math.max(pid, player.pid);
            }
        } else players = new ArrayList<>();
        players.add(0, new DBPlayer(pid + 1, 0, pname));

        setPlayers(players);
    }

    public static void removePlayer(String pname) {
        ArrayList<DBPlayer> players = getPlayers();
        if (players != null) {
            for (Iterator<DBPlayer> iter = players.iterator(); iter.hasNext(); ) {
                if (Objects.equals(iter.next().pname, pname)) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    public static boolean checkPlayer(String pname) {
        return getPlayer(pname) != null;
    }

    public static void addMessage(String pname, String text, boolean isClient) {
        if (!checkPlayer(pname)) {
            addPlayer(pname);
        }

        try {

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate("INSERT INTO 'messages' ('pname', 'client', 'mtext', 'suuid') VALUES ('" + pname + "', '" + (isClient ? 1 : 0) + "', '" + text + "', '" + serverUUID.toString() + "');");

            statement.close();
            connection.close();

        } catch (Exception e) {e.printStackTrace();}
    }

    public static void setPlayers(ArrayList<DBPlayer> plist) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate("DELETE FROM 'players';");
            statement.executeUpdate("VACUUM;");

            for (DBPlayer player : plist) {
                statement.executeUpdate("INSERT INTO 'players' ('pname', 'lid', 'pid') VALUES ('" + player.pname + "', " + player.lid + ", " + player.pid + ");");
            }

            statement.close();
            connection.close();

        } catch (Exception e) {e.printStackTrace();}
    }

    public static ArrayList<DBPlayer> getPlayers() {
        ArrayList<DBPlayer> plist = new ArrayList<>();

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM 'players';");
            while (result.next()) {
                plist.add(result.getInt("lid"), new DBPlayer(result.getInt("pid"), result.getInt("lid"), result.getString("pname")));
            }

            statement.close();
            connection.close();
        } catch (Exception e) {e.printStackTrace();}

        if (plist.isEmpty()) return null;
        else return plist;
    }

    public static DBPlayer getPlayer(String pname) {
        DBPlayer player = null;

        try {
            for (DBPlayer pl : getPlayers()) {
                if (Objects.equals(pl.pname, pname)) {
                    player = pl;
                }
            }
        } catch (Exception ignored) {}

        return player;
    }

    public static HashMap<Long, DBMessage> getMessages(String pname) {
        HashMap<Long, DBMessage> mlist = new HashMap<>();

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM 'messages' WHERE `pname` = '" + pname + "';");
            while (result.next()) {
                mlist.put(result.getLong("mid"), new DBMessage(result.getLong("mid"), result.getBoolean("client"), result.getString("mtext"), getPlayer(pname)));
            }

            statement.close();
            connection.close();
        } catch (Exception e) {e.printStackTrace();}

        if (mlist.isEmpty()) return null;
        else return mlist;
    }

    public static void setServerUUID(UUID serverUUID) {
        Database.serverUUID = serverUUID;
    }
}
