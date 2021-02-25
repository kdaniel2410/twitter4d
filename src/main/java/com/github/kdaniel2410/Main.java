package com.github.kdaniel2410;

import com.github.kdaniel2410.commands.FollowCommand;
import com.github.kdaniel2410.commands.FollowingCommand;
import com.github.kdaniel2410.commands.InviteCommand;
import com.github.kdaniel2410.commands.UnfollowCommand;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.handlers.TwitterHandler;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.permission.Permissions;

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

        logger.info("Logged in as {}", api.getYourself().getName());
        logger.info("Use the following link to invite me to your server {}", api.createBotInvite(Permissions.fromBitmask(18496)));
        logger.info("Serving {} server(s)", api.getServers().size());

        api.getServers().forEach(server -> logger.info("Loaded server {} owned by {} with {} members", server.getName(), server.getOwner().map(Nameable::getName).orElse("unknown user"), server.getMemberCount()));

        api.addServerJoinListener(event -> {
            String server = event.getServer().getName();
            String owner = event.getServer().getOwner().map(Nameable::getName).orElse("unknown");
            logger.info("Joined server " + server + " owned by " + owner);
        });

        api.addServerLeaveListener(event -> {
            String server = event.getServer().getName();
            String owner = event.getServer().getOwner().map(Nameable::getName).orElse("unknown");
            logger.info("Left server " + server + " owned by " + owner);
        });

        DatabaseHandler databaseHandler = new DatabaseHandler();
        TwitterHandler twitterHandler = new TwitterHandler(api, databaseHandler);

        api.addServerChannelDeleteListener(event -> databaseHandler.deleteByChannelId(event.getChannel().getId()));
        api.addServerLeaveListener(event -> databaseHandler.deleteByServerId(event.getServer().getId()));

        CommandHandler commandHandler = new JavacordHandler(api);
        commandHandler.registerCommand(new FollowCommand(twitterHandler, databaseHandler));
        commandHandler.registerCommand(new FollowingCommand(databaseHandler));
        commandHandler.registerCommand(new InviteCommand());
        commandHandler.registerCommand(new UnfollowCommand(databaseHandler, twitterHandler));
    }
}
