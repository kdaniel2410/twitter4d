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
import org.javacord.api.util.NonThrowingAutoCloseable;
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

    @Command(aliases = {">follow"}, privateMessages = false)
    public String onCommand(String[] args, ServerTextChannel channel, Server server, User user) {
        if (!server.hasPermission(user, PermissionType.MANAGE_CHANNELS)) {
            return ":warning: Missing required permissions";
        }
        if (args.length != 1) {
            return ":warning: Invalid arguments";
        }
        NonThrowingAutoCloseable closeable = channel.typeContinuously();
        twitter4j.User twitterUser;
        try {
            twitterUser = TwitterFactory.getSingleton().showUser(args[0]);
            ResultSet resultSet = databaseHandler.getByChannelAndTwitterId(channel.getId(), twitterUser.getId());
            if (resultSet.next()) {
                resultSet.close();
                closeable.close();
                return ":warning: You are already following ``@" + twitterUser.getScreenName() + " (" + twitterUser.getName() + ")``";
            }
            resultSet.close();
        } catch (TwitterException | SQLException e) {
            logger.error(e);
            closeable.close();
            return ":warning: There was an error executing that command" +
                    "```" +
                    e +
                    "```";
        }
        logger.info("Follow command executed by {} on {} in {}", user.getName(), server.getName(), channel.getName());
        twitterHandler.addToFilterQuery(twitterUser.getId());
        databaseHandler.insertNew(server.getId(), channel.getId(), twitterUser.getId());
        closeable.close();
        return ":bird: Now following ``@" + twitterUser.getScreenName() + " (" + twitterUser.getName() + ")``";
    }
}
