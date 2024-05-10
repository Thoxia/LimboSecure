package com.thoxia.limbosecure.bot.listener;

import com.thoxia.limbosecure.bot.Bot;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PremiumCommandListener extends ListenerAdapter {

    private final Bot bot;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String m = event.getInteraction().getName();
        if (!m.startsWith("premium")) return;

        if (!event.getUser().getId().equals(Bot.HYPERION_ID)) {
            event.getInteraction().deferReply(true).setContent(":x: You can not run this command!").queue();
            return;
        }

        String serverID = event.getInteraction().getOption("server-id").getAsString();
        String serverIP = event.getInteraction().getOption("server-ip").getAsString();

        bot.getHandler().makeServerPremium(serverID, serverIP);

        event.getInteraction().deferReply(true).setContent("Server is now premium!").queue();
    }

}
