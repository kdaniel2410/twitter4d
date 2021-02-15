package com.github.kdaniel2410.listeners;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import org.javacord.api.event.channel.server.ServerChannelDeleteEvent;
import org.javacord.api.listener.channel.server.ServerChannelDeleteListener;

public class MyServerChannelDeleteListener implements ServerChannelDeleteListener {

    private final DatabaseHandler databaseHandler;

    public MyServerChannelDeleteListener(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Override
    public void onServerChannelDelete(ServerChannelDeleteEvent event) {
        databaseHandler.deleteByChannelId(event.getChannel().getId());
    }
}
