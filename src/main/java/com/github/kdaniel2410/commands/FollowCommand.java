package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.handlers.TwitterHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowCommand implements CommandExecutor {

    private final TwitterHandler twitterHandler;
    private final DatabaseHandler databaseHandler;
    private static final Logger logger = LogManager.getLogger();

    public FollowCommand(TwitterHandler twitterHandler, DatabaseHandler databaseHandler) {
        this.twitterHandler = twitterHandler;
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">follow"}, privateMessages = false, async = true)
    public String onCommand(String[] args, ServerTextChannel channel, Server server, User user) {
        channel.type();
        if (!server.hasPermission(user, PermissionType.MANAGE_CHANNELS)) {
            return ":warning: Missing required permissions";
        }
        if (args.length != 1) {
            return ":warning: Invalid arguments";
        }
        long twitterId = 0;
        try {
            twitterId = TwitterFactory.getSingleton().showUser(args[0]).getId();
            ResultSet resultSet = databaseHandler.getByChannelAndTwitterId(channel.getId(), twitterId);
            if (resultSet.next()) {
                return ":warning: You are already following that account in this channel";
            }
        } catch (TwitterException e) {
            return ":warning: Twitter user not found";
        } catch (SQLException e) {
            logger.error(e);
        }
        twitterHandler.addToFilterQuery(twitterId);
        databaseHandler.insertNew(server.getId(), channel.getId(), twitterId);
        return ":bird: Now following *@" + args[0] + "*";
    }
}
