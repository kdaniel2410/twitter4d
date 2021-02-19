package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.Constants;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.handlers.TwitterHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.logging.ExceptionLogger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InfoCommand implements CommandExecutor {

    private final TwitterHandler twitterHandler;
    private final DatabaseHandler databaseHandler;
    private static final Logger logger = LogManager.getLogger();

    public InfoCommand(TwitterHandler twitterHandler, DatabaseHandler databaseHandler) {
        this.twitterHandler = twitterHandler;
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">info"})
    public void onCommand(DiscordApi api, TextChannel channel, MessageAuthor author) {
        if (!author.isBotOwner()) return;
        String description = "Currently a member of " + api.getServers().size() + " servers \n" +
                "Serving " + api.getCachedUsers().size() + " cached users \n" +
                "Following " + twitterHandler.getFollowingCount() + " unique twitter accounts";
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Info Command")
                .setColor(Constants.EMBED_COLOR)
                .setThumbnail(api.getYourself().getAvatar())
                .setTimestampToNow()
                .setDescription(description);
        for (Server server : api.getServers()) {
            StringBuilder string = new StringBuilder();
            ResultSet resultSet = databaseHandler.getByServerId(server.getId());
            try {
                Twitter twitter = TwitterFactory.getSingleton();
                while (resultSet.next()) {
                    User user = twitter.showUser(resultSet.getLong("twitterId"));
                    string.append("Following @")
                            .append(user.getScreenName())
                            .append(" (")
                            .append(user.getName())
                            .append(") ")
                            .append("\n");
                }
            } catch (SQLException | TwitterException e) {
                logger.error(e);
            }
            if (string.length() == 0) {
                string.append("Not following anyone");
            }
            String owner = server.getOwner().map(Nameable::getName).orElse("unknown");
            embed.addField(server.getName() + " owned by " + owner, string.toString());
        }
        channel.sendMessage(embed).exceptionally(ExceptionLogger.get());
    }
}
