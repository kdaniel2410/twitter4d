package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.handlers.TwitterHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class UnfollowCommand implements CommandExecutor {

    private final TwitterHandler twitterHandler;
    private final DatabaseHandler databaseHandler;
    private static final Logger logger = LogManager.getLogger();

    public UnfollowCommand(TwitterHandler twitterHandler, DatabaseHandler databaseHandler) {
        this.twitterHandler = twitterHandler;
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">unfollow"}, privateMessages = false, async = true)
    public String onCommand(String[] args, DiscordApi api, ServerTextChannel channel, Message message, Server server, User user) {
        channel.type();
        if (!server.hasPermission(user, PermissionType.MANAGE_CHANNELS)) {
            return ":warning: You do not have the correct permissions to do that";
        }
        if (args.length != 1) {
            channel.sendMessage("**Error** invalid arguments").exceptionally(ExceptionLogger.get());
            return ":warning: Invalid arguments";
        }
        api.getThreadPool().getExecutorService().execute(() -> {
            long twitterId = 0;
            try {
                twitterId = TwitterFactory.getSingleton().showUser(args[0]).getId();
            } catch (TwitterException e) {
                logger.error(e);
            }
            databaseHandler.deleteByChannelAndTwitterId(channel.getId(), twitterId);
        });
        return ":wastebasket: No longer following *@" + args[0] + "*";
    }
}
