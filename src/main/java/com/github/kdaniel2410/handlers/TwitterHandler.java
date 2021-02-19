package com.github.kdaniel2410.handlers;

import com.github.kdaniel2410.listeners.TwitterStatusListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TwitterHandler {

    private final DatabaseHandler databaseHandler;
    private final DiscordApi api;
    private TwitterStream twitterStream;
    private static final Logger logger = LogManager.getLogger();
    private final ArrayList<Long> follows;

    public TwitterHandler(DiscordApi api, DatabaseHandler databaseHandler) {
        this.api = api;
        this.databaseHandler = databaseHandler;
        ResultSet all = databaseHandler.getAllUniqueTwitterIds();
        this.follows = new ArrayList<>();
        try {
            while (all.next()) {
                follows.add(all.getLong("twitterId"));
            }
            all.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        startStream();
    }

    public void startStream() {
        twitterStream = new TwitterStreamFactory().getInstance().addListener(new TwitterStatusListener(api, databaseHandler));
        if (follows.size() < 1) return;
        long[] following = new long[follows.size()];
        for (int i = 0; i < follows.size(); i++) {
            following[i] = follows.get(i);
        }
        logger.info("Starting stream following {} unique twitter accounts, the limit is 5000", following.length);
        twitterStream.filter(new FilterQuery().follow(following));
    }

    public void addToFilterQuery(long twitterId) {
        if (follows.contains(twitterId)) return;
        follows.add(twitterId);
        logger.info("Added twitter id {} to  the filter query", twitterId);
        long[] following = new long[follows.size()];
        for (int i = 0; i < follows.size(); i++) {
            following[i] = follows.get(i);
        }
        twitterStream.filter(new FilterQuery().follow(following));
    }

    public void removeFromFilterQuery(long twitterId) {
        if (!follows.contains(twitterId)) return;
        follows.remove(twitterId);
        logger.info("Removed twitter id {} from the filter query", twitterId);
        long[] following = new long[follows.size()];
        for (int i = 0; i < follows.size(); i++) {
            following[i] = follows.get(i);
        }
        twitterStream.filter(new FilterQuery().follow(following));
    }

    public int getFollowingCount() {
        return follows.size();
    }
}
