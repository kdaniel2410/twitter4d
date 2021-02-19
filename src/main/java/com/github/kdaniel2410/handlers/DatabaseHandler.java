package com.github.kdaniel2410.handlers;

import com.github.kdaniel2410.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DatabaseHandler {

    private Connection connection = null;
    private static final Logger logger = LogManager.getLogger();

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
            logger.error(e);
        }
        return null;
    }

    public ResultSet getAllUniqueTwitterIds() {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("select distinct twitterId from streams");
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    public ResultSet getByTwitterId(long twitterId) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("select * from streams where twitterId = " + twitterId);
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    public ResultSet getByServerId(long serverId) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("select * from streams where serverId = " + serverId);
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    public ResultSet getByChannelAndTwitterId(long channelId, long twitterId) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("select * from streams where channelId = " + channelId + " and twitterId = " + twitterId);
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    public void insertNew(long serverId, long channelId, long twitterId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("insert into streams values (%d, %d, %d)", serverId, channelId, twitterId));
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public void deleteByServerId(long serverId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from streams where serverId = " + serverId);
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public void deleteByChannelAndTwitterId(long channelId, long twitterId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from streams where channelId = " + channelId + " and twitterId = " + twitterId);
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public void deleteByChannelId(long channelId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from streams where channelId = " + channelId);
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public void deleteByTwitterId(long twitterId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from streams where twitterId = " + twitterId);
        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
