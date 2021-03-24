package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.Constants;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class AnnounceCommand implements CommandExecutor {

    @Command(aliases = {">announce"})
    public String onCommand(String[] args, DiscordApi api, MessageAuthor author, TextChannel channel) {
        if (!author.isBotOwner()) return null;
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription(String.join(" ", args))
                .setColor(Constants.EMBED_COLOR)
                .setTimestampToNow();
        api.getServers().forEach(server -> server.getOwner().ifPresent(owner -> owner.sendMessage(embed)));
        return null;
    }
}
