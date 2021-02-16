package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.Constants;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.util.logging.ExceptionLogger;

public class InviteCommand implements CommandExecutor {

    @Command(aliases = {">invite"})
    public void onCommand(DiscordApi api, TextChannel channel) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Constants.EMBED_COLOR)
                .setThumbnail(api.getYourself().getAvatar())
                .setDescription("Click [here](" + api.createBotInvite(Permissions.fromBitmask(18496)) + ") to invite me to your discord server.");
        channel.sendMessage(embed).exceptionally(ExceptionLogger.get());
    }
}
