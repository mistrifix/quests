package net.mistrifix.main.data;

import net.mistrifix.main.QuestPlugin;

import java.sql.*;


public class Database {

    private Connection connection;
    private static Database instance;

    private final MySQL mysql = QuestPlugin.getInstance().mysql;

    public Database()
    {
        instance = this;
        firstConnection();
    }

    public static Database getInstance()
    {
        return(instance != null ? instance : new Database());
    }

    public boolean isConnected()
    {
        try {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection != null;
    }


    public Connection openConnection()
    {
        if (isConnected()) {
            return this.connection;
        }
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + mysql.host + ":" + mysql.port + "/" + mysql.database;
            connection = DriverManager.getConnection(url, mysql.username, mysql.password);
            connection.setAutoCommit(false);
            return connection;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private void firstConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + mysql.host + ":" + mysql.port + "/?user=" + mysql.username + "&password=" + mysql.password;
            Connection conn = DriverManager.getConnection(url);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + mysql.database);
            conn.close();
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    public void executeQuery(String query) {
        try
        {
            Connection connection = this.openConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeQuery();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public void executeUpdate(String query) {
        try {
            Connection connection = this.openConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            if (statement == null) {
                return;
            }
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

