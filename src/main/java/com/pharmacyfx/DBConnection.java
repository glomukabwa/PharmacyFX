package com.pharmacyfx;

import java.sql.*;

public class DBConnection {
    private static final String url = "jdbc:mysql://localhost:3306/pharmacydb";
    private static final String user = "root";
    private static final String password = "L@ur3nceangelina";

    public static Connection getConnection(){
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(url,user,password);
            System.out.println("Successful Connection");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unsuccessful connection");
        }

        return conn;
    }

    /*public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
    }*/
}
