package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.handlers.TwitterHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;
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

    @Command(aliases = {">follow"}, async = true)
    public void onCommand(String[] args, DiscordApi api, TextChannel channel, Message message, Server server, User user) {
        if (!server.hasPermission(user, PermissionType.MANAGE_CHANNELS)) {
            channel.sendMessage("**Error** you do not have the correct permissions to do that.").exceptionally(ExceptionLogger.get());
            return;
        }
        if (args.length < 1) {
            channel.sendMessage("**Error** not enough arguments").exceptionally(ExceptionLogger.get());
            return;
        }
        long twitterId;
        try {
            twitterId = TwitterFactory.getSingleton().showUser(args[0]).getId();
            ResultSet resultSet = databaseHandler.getByChannelAndTwitterId(channel.getId(), twitterId);
            if (resultSet.next()) {
                channel.sendMessage("**Error** you are already following that account in this channel").exceptionally(ExceptionLogger.get());
                return;
            }
        } catch (TwitterException | SQLException e) {
            channel.sendMessage("**Error** twitter user not found").exceptionally(ExceptionLogger.get());
            return;
        }
        long finalTwitterId = twitterId;
        twitterHandler.addToFilterQuery(finalTwitterId);
        databaseHandler.insertNew(server.getId(), channel.getId(), finalTwitterId);
        message.addReaction("\u2705").exceptionally(ExceptionLogger.get());
        logger.info("Follow command executed by {} in {} on {}", user.getName(), channel, server.getName());
    }
}
