package com.github.kdaniel2410.listeners;

import com.github.kdaniel2410.Constants;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TwitterStatusListener implements StatusListener {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;
    private final DiscordApi api;

    public TwitterStatusListener(DiscordApi api, DatabaseHandler databaseHandler) {
        this.api = api;
        this.databaseHandler = databaseHandler;
    }

    @Override
    public void onStatus(Status status) {
        if (status.isRetweet()) return;
        if (status.getInReplyToScreenName() != null) return;
        String url = String.format("https://twitter.com/%s/status/%d", status.getUser().getName(), status.getId());
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Constants.EMBED_COLOR)
                .setAuthor(status.getUser().getScreenName(), status.getUser().getURL(), status.getUser().getProfileImageURL())
                .setDescription(String.format("%s Click [here](%s) to go to open this tweet in twitter", status.getText(), url))
                .setTimestamp(status.getCreatedAt().toInstant());
        ResultSet resultSet = databaseHandler.getByTwitterId(status.getUser().getId());
        try {
            while (resultSet.next()) {
                api.getChannelById(resultSet.getLong("channelId")).flatMap(Channel::asServerTextChannel).ifPresent(channel -> channel.sendMessage(embed).exceptionally(ExceptionLogger.get()));
            }
            resultSet.close();
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        logger.warn(statusDeletionNotice);
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    public void onStallWarning(StallWarning warning) {
        logger.warn(warning);
    }

    @Override
    public void onException(Exception ex) {
        logger.error(ex);
    }
}