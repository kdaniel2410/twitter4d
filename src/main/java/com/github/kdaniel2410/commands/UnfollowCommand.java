package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.handlers.TwitterHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class UnfollowCommand implements CommandExecutor {

    private final TwitterHandler twitterHandler;
    private final DatabaseHandler databaseHandler;

    public UnfollowCommand(TwitterHandler twitterHandler, DatabaseHandler databaseHandler) {
        this.twitterHandler = twitterHandler;
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">unfollow"})
    public void onCommand(String[] args, DiscordApi api, TextChannel channel, Message message, Server server) {
        if (args.length < 1) {
            channel.sendMessage("**Error** not enough arguments");
            return;
        }
        api.getThreadPool().getExecutorService().execute(() -> {
            long twitterId = 0;
            try {
                twitterId = TwitterFactory.getSingleton().showUser(args[0]).getId();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            databaseHandler.deleteByTwitterId(twitterId);
            twitterHandler.reloadTwitterListeners();
        });
        message.addReaction("\u2705");
    }
}