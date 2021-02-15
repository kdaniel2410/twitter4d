package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.handlers.TwitterHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
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

    public FollowCommand(TwitterHandler twitterHandler, DatabaseHandler databaseHandler) {
        this.twitterHandler = twitterHandler;
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">follow"})
    public void onCommand(String[] args, DiscordApi api, TextChannel channel, Message message, Server server, User user) {
        if (!server.hasPermission(user, PermissionType.MANAGE_CHANNELS)) {
            channel.sendMessage("**Error** you do not have the correct permissions to do that.");
            return;
        }
        if (args.length < 1) {
            channel.sendMessage("**Error** not enough arguments");
            return;
        }
        api.getThreadPool().getExecutorService().execute(() -> {
            long twitterId = 0;
            try {
                twitterId = TwitterFactory.getSingleton().showUser(args[0]).getId();
                ResultSet resultSet = databaseHandler.getByTwitterId(twitterId);
                if (resultSet.next()) return;
            } catch (TwitterException | SQLException e) {
                e.printStackTrace();
            }
            databaseHandler.insertNew(server.getId(), channel.getId(), twitterId);
            twitterHandler.addTweetListener(channel.getId(), twitterId);
        });
        message.addReaction("\u2705");
    }
}
