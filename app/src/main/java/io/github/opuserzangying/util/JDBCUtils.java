package io.github.opuserzangying.util;

import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by root on 16-12-12.
 */

public final class JDBCUtils {
    private static Connection conn = null;

    public static void connect(String host, String port, String dbName, String dbArgument,String dbUsername, String dbPassword,int dbKind) {
        if(dbArgument.equals("")){
            dbArgument="?useUnicode=true&characterEncoding=utf8&useSSL=false";
        }
        String connString = "jdbc:mysql://" + host + ":" + port + "/" + dbName + dbArgument;
        String driverName = "com.mysql.jdbc.Driver";
        //switch with dbKind
        switch (dbKind) {
            default://for MySQL
                break;
            case 1://TODO for SQL Server
                break;
            case 2://TODO for Oracle
                break;
        }
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFound");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet select(String sql) {
        ResultSet rs = null;
        try {
            Statement stmt=conn.createStatement();
            rs = stmt.executeQuery(sql);
            System.out.println(sql);
            return rs;
        } catch (SQLException e) {
            rollback();
            e.printStackTrace();
        }
        return rs;
    }

    public static int update(String sql) {
        int rows = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            rows = ps.executeUpdate();
        } catch (SQLException e) {
            rollback();
            e.printStackTrace();
        }
        return rows;
    }

    public  static void rollback(){
        try {
            conn.rollback();
        } catch (SQLException e) {
            System.out.print("database rollback err");
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if(conn!=null) conn.close();
        } catch (SQLException e) {
            System.out.print("database close err");
            e.printStackTrace();
        }
    }
}
