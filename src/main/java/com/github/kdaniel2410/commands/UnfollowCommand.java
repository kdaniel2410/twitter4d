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

public class UnfollowCommand implements CommandExecutor {

    private final DatabaseHandler databaseHandler;
    private final TwitterHandler twitterHandler;
    private static final Logger logger = LogManager.getLogger();

    public UnfollowCommand(DatabaseHandler databaseHandler, TwitterHandler twitterHandler) {
        this.databaseHandler = databaseHandler;
        this.twitterHandler = twitterHandler;
    }

    @Command(aliases = {">unfollow"}, privateMessages = false)
    public String onCommand(String[] args, ServerTextChannel channel, Server server, User user) {
        if (!server.hasPermission(user, PermissionType.MANAGE_CHANNELS)) {
            return ":warning: You do not have the correct permissions to do that";
        }
        if (args.length != 1) {
            return ":warning: Invalid arguments";
        }
        logger.info("Unfollow command executed by {} on {} in {}", user.getName(), server.getName(), channel.getName());
        NonThrowingAutoCloseable closeable = channel.typeContinuously();
        twitter4j.User twitterUser;
        try {
            if (args[0].equalsIgnoreCase("all")) {
                ResultSet resultSet = databaseHandler.getByServerId(server.getId());
                if (!resultSet.next()) {
                    closeable.close();
                    resultSet.close();
                    return ":warning: You aren't following anyone";
                }
                databaseHandler.deleteByServerId(server.getId());
                while (resultSet.next()) {
                    twitterHandler.removeFromFilterQuery(resultSet.getLong("twitterId"));
                }
                resultSet.close();
                closeable.close();
                return ":wastebasket: No longer following anyone";
            } else {
                twitterUser = TwitterFactory.getSingleton().showUser(args[0]);
                ResultSet resultSet = databaseHandler.getByChannelAndTwitterId(channel.getId(), twitterUser.getId());
                if (!resultSet.next()) {
                    closeable.close();
                    resultSet.close();
                    return ":warning: You aren't following ``@" + twitterUser.getScreenName() + " (" + twitterUser.getName() + ")``";
                }
                resultSet.close();
                databaseHandler.deleteByChannelAndTwitterId(channel.getId(), twitterUser.getId());
                twitterHandler.removeFromFilterQuery(twitterUser.getId());
                closeable.close();
                return ":wastebasket: No longer following ``@" + twitterUser.getScreenName() + " (" + twitterUser.getName() + ")``";
            }

        } catch (TwitterException | SQLException e) {
            logger.error(e);
            closeable.close();
            return ":warning: There was an error executing that command" +
                    "```" +
                    e +
                    "```";
        }
    }
}
