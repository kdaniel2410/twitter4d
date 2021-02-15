package com.github.kdaniel2410.handlers;

import com.github.kdaniel2410.Constants;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import twitter4j.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TwitterHandler {

    private final DiscordApi api;
    private final TwitterStream twitterStreamFactory;
    private final DatabaseHandler databaseHandler;

    public TwitterHandler(DiscordApi api, DatabaseHandler databaseHandler) {
        this.api = api;
        this.twitterStreamFactory = new TwitterStreamFactory().getInstance();
        this.databaseHandler = databaseHandler;
    }

    public void addTweetListener(long channelId, long twitterId) {
        twitterStreamFactory.addListener(new StatusListener() {
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
                api.getChannelById(channelId).flatMap(Channel::asTextChannel).ifPresent(channel -> channel.sendMessage(embed));
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        }).filter(new FilterQuery().follow(twitterId));
    }

    public void loadTwitterListeners() {
        try {
            ResultSet all = databaseHandler.getAll();
            while (all.next()) {
                addTweetListener(all.getInt("channelId"), all.getInt("twitterId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reloadTwitterListeners() {
        twitterStreamFactory.clearListeners();
        loadTwitterListeners();
    }
}
