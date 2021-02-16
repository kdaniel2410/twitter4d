package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.Constants;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

public class InviteCommand implements CommandExecutor {

    private static final Logger logger = LogManager.getLogger();

    @Command(aliases = {">invite"})
    public void onCommand(DiscordApi api, TextChannel channel, User user, Server server) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Constants.EMBED_COLOR)
                .setThumbnail(api.getYourself().getAvatar())
                .setDescription("Click [here](" + api.createBotInvite(Permissions.fromBitmask(18496)) + ") to invite me to your discord server.");
        channel.sendMessage(embed).exceptionally(ExceptionLogger.get());
        logger.info("Following command executed by {} in {} on {}", user.getName(), channel, server.getName());
    }
}
