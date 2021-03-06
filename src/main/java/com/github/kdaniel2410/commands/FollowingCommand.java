package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.Constants;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Mentionable;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.NonThrowingAutoCloseable;
import org.javacord.api.util.logging.ExceptionLogger;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowingCommand implements CommandExecutor {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;

    public FollowingCommand(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">following"}, privateMessages = false)
    public String onCommand(DiscordApi api, Server server, ServerTextChannel channel, User user) {
        logger.info("Following command executed by {} on {} in {}", user.getName(), server.getName(), channel.getName());
        NonThrowingAutoCloseable closeable = channel.typeContinuously();
        StringBuilder description = new StringBuilder();
        ResultSet resultSet = databaseHandler.getByServerId(server.getId());
        try {
            while (resultSet.next()) {
                twitter4j.User twitterUser = TwitterFactory.getSingleton().showUser(resultSet.getLong("twitterId"));
                String channelMention = api.getServerTextChannelById(resultSet.getLong("channelId")).map(Mentionable::getMentionTag).orElse("missing channel");
                description.append(String.format("Following @%s (%s) in %s\n", twitterUser.getScreenName(), twitterUser.getName(), channelMention));
            }
            resultSet.close();
            if (description.length() > 0) {
                closeable.close();
                channel.sendMessage(new EmbedBuilder()
                        .setColor(Constants.EMBED_COLOR)
                        .setDescription(description.toString())
                ).exceptionally(ExceptionLogger.get());
            } else {
                closeable.close();
                return ":thinking: You are not following any twitter account(s) on this discord server";
            }
        } catch (SQLException | TwitterException e) {
            closeable.close();
            return ":warning: There was an error executing that command" +
                    "```" +
                    e +
                    "```";
        }
        return null;
    }
}
