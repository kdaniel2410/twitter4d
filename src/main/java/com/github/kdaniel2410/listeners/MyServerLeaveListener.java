package com.github.kdaniel2410.listeners;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;

public class MyServerLeaveListener implements ServerLeaveListener {

    public DatabaseHandler databaseHandler;

    public MyServerLeaveListener(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Override
    public void onServerLeave(ServerLeaveEvent event) {
        databaseHandler.deleteByServerId(event.getServer().getId());
    }
}
