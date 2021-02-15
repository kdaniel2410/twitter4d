package com.github.kdaniel2410;

import com.github.kdaniel2410.commands.FollowCommand;
import com.github.kdaniel2410.commands.FollowingCommand;
import com.github.kdaniel2410.commands.UnfollowCommand;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.handlers.TwitterHandler;
import com.github.kdaniel2410.listeners.MyServerChannelDeleteListener;
import com.github.kdaniel2410.listeners.MyServerLeaveListener;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.server.Server;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        if ((System.getenv("token")) == null) {
            logger.error("Please provide the bot token as an environment variable (e.g export token=your-bot-token-here)");
            System.exit(-1);
        }

        DiscordApi api = new DiscordApiBuilder()
                .setToken(System.getenv("token"))
                .setAllIntents()
                .login()
                .join();

        logger.info("Logged in as " + api.getYourself().getName());
        logger.info("Serving " + api.getServers().size() + " server(s) and " + api.getCachedUsers().size() + " cached user(s)");

        for (Server server : api.getServers()) {
            String owner = server.getOwner().map(Nameable::getName).orElse("unknown user");
            logger.info(String.format("Loaded server %s owned by %s which has %d members", server.getName(), owner, server.getMembers().size()));
        }

        DatabaseHandler databaseHandler = new DatabaseHandler();
        TwitterHandler twitterHandler = new TwitterHandler(api, databaseHandler);
        twitterHandler.loadTwitterListeners();

        api.addServerChannelDeleteListener(new MyServerChannelDeleteListener(databaseHandler));
        api.addServerLeaveListener(new MyServerLeaveListener(databaseHandler));

        CommandHandler commandHandler = new JavacordHandler(api);
        commandHandler.registerCommand(new FollowCommand(twitterHandler, databaseHandler));
        commandHandler.registerCommand(new UnfollowCommand(twitterHandler, databaseHandler));
        commandHandler.registerCommand(new FollowingCommand(databaseHandler));
    }
}
