package me.mrcavas.mcmessage;

import net.minecraft.client.MinecraftClient;

//import org.sqlite.JDBC;

import java.io.File;
import java.sql.*;
import java.util.UUID;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(url);
    }

    public static void addPlayer(String name) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate("INSERT INTO 'players' ('pname', 'pid') VALUES ('" + name + "', (SELECT IFNULL(MAX(pid), 0) + 1 FROM 'players'));");

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// --Commented out by Inspection START (11.08.2021 23:24):
//    public static void removePlayer(String name) {
//        try {
//
//            Connection connection = getConnection();
//            Statement statement = connection.createStatement();
//
//            statement.executeUpdate("DELETE FROM 'players' WHERE `pname` = '" + name + "';");
//
//            statement.close();
//            connection.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
// --Commented out by Inspection STOP (11.08.2021 23:24)

    public static boolean checkPlayer(String name) {
        try {
            boolean check = false;
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM 'players' WHERE `pname` = '" + name + "';");
            while (result.next()) {
                if (result.getInt(1) == 1)
                    check = true;
            }

            statement.close();
            connection.close();
            return check;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setServerUUID(UUID serverUUID) {
        Database.serverUUID = serverUUID;
    }
}
