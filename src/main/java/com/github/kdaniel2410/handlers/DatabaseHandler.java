package com.github.kdaniel2410.handlers;

import com.github.kdaniel2410.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DatabaseHandler {

    private static final Logger logger = LogManager.getLogger();
    private Connection connection = null;

    public DatabaseHandler() {
        try {
            this.connection = DriverManager.getConnection(Constants.SQL_CONNECTION_STRING);
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists streams (serverId integer, channelId integer, twitterId integer)");
        } catch (SQLException e) {
            e.printStackTrace();
         }
    }

    public ResultSet getAll() {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("select * from streams");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getAllFromServer(long serverId) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("select * from streams where serverId = " + serverId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertNew(long serverId, long channelId, long twitterId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("insert into streams values (%d, %d, %d)", serverId, channelId, twitterId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByServerId(long serverId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from streams where serverId = " + serverId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByChannelId(long channelId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from streams where channelId = " + channelId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByTwitterId(long twitterId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from streams where twitterId = " + twitterId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
