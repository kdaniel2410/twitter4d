package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.Constants;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Mentionable;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
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

    @Command(aliases = {">following"})
    public void onCommand(DiscordApi api, Server server, TextChannel channel) throws SQLException, TwitterException {
        StringBuilder description = new StringBuilder();
        ResultSet resultSet = databaseHandler.getByServerId(server.getId());
        while (resultSet.next()) {
            description.append("Following @");
            description.append(TwitterFactory.getSingleton().showUser(resultSet.getLong("twitterId")).getScreenName());
            description.append(" (");
            description.append(TwitterFactory.getSingleton().showUser(resultSet.getLong("twitterId")).getName());
            description.append(" ) ");
            description.append("in ");
            description.append(api.getChannelById(resultSet.getLong("channelId")).flatMap(Channel::asServerTextChannel).map(Mentionable::getMentionTag).orElse("missing channel"));
            description.append("\n");
        }
        if (description.length() > 0) {
            channel.sendMessage(new EmbedBuilder()
                    .setColor(Constants.EMBED_COLOR)
                    .setDescription(description.toString())
            );
        } else {
            channel.sendMessage("You are not following any twitter account(s) on this discord server").exceptionally(ExceptionLogger.get());
        }
    }
}
