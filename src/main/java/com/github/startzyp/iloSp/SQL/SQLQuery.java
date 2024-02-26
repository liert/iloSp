package com.github.startzyp.iloSp.SQL;

import com.github.startzyp.iloSp.Config.Settings;
import com.github.startzyp.iloSp.Data.PackData;
import com.github.startzyp.iloSp.Utils.ItemSync;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SQLQuery {
    public static String ip = Settings.I.MySQL_IP;
    public static String port = Settings.I.MySQL_port;
    public static String database = Settings.I.MySQL_database;
    public static String table = Settings.I.MySQL_Table;
    public static String user = Settings.I.MySQL_user;
    public static String password = Settings.I.MySQL_password;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database, user, password);
    }

    public static void free(ResultSet rs, Statement st, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (st != null) {
                st.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = SQLQuery.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (" + "`player` varchar(255) NOT NULL," + "`name` varchar(255) NOT NULL," + "`inventory` longtext," + "PRIMARY KEY (`player`)" + ");");
            ps.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
            SQLQuery.free(rs, ps, conn);
            return;
        }
        SQLQuery.free(rs, ps, conn);
    }

    public static PackData getPlayerData(Player p) {
        ArrayList<ItemStack> list = new ArrayList<>();
        block8: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = SQLQuery.getConnection();
                ps = conn.prepareStatement("SELECT * FROM `" + table + "`WHERE `player` = ?");
                ps.setString(1, p.getUniqueId().toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    String raw = rs.getString("inventory");
                    JsonArray json = new JsonParser().parse(raw).getAsJsonArray();
                    ItemStack[] items = ItemSync.getStringData(json);
                    int i = 0;
                    while (i < items.length) {
                        list.add(items[i]);
                        ++i;
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                SQLQuery.free(rs, ps, conn);
                break block8;
            }
            SQLQuery.free(rs, ps, conn);
        }
        return new PackData(Settings.I.Max, list);
    }

    public static List<ItemStack> getPlayerItem(Player p) {
        ArrayList<ItemStack> list = new ArrayList<>();
        block8: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = SQLQuery.getConnection();
                ps = conn.prepareStatement("SELECT * FROM `" + table + "`WHERE `player` = ?");
                ps.setString(1, p.getUniqueId().toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    String raw = rs.getString("inventory");
                    JsonArray json = new JsonParser().parse(raw).getAsJsonArray();
                    ItemStack[] items = ItemSync.getStringData(json);
                    int i = 0;
                    while (i < items.length) {
                        list.add(items[i]);
                        ++i;
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                SQLQuery.free(rs, ps, conn);
                break block8;
            }
            SQLQuery.free(rs, ps, conn);
        }
        return list;
    }

    public static void savePlayerData(Player p, PackData pd) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = SQLQuery.getConnection();
            ps = conn.prepareStatement("SELECT * FROM `" + table + "`WHERE `player` = ?");
            ps.setString(1, p.getUniqueId().toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                ps = conn.prepareStatement("UPDATE `" + table + "` SET `name`=?, `inventory`=? WHERE (`player`=?)");
                ps.setString(1, p.getName());
                ps.setString(2, ItemSync.getDataString(pd));
                ps.setString(3, p.getUniqueId().toString());
                ps.execute();
            } else {
                ps.close();
                ps = conn.prepareStatement("INSERT INTO `" + table + "` (`player`, `name`, `inventory`) VALUES (?, ?, ?)");
                ps.setString(1, p.getUniqueId().toString());
                ps.setString(2, p.getName());
                ps.setString(3, ItemSync.getDataString(pd));
                ps.execute();
            }
            SQLQuery.free(rs, ps, conn);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            SQLQuery.free(rs, ps, conn);
        }
    }
}
