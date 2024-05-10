package com.thoxia.limbosecure.bot.listener;

import com.thoxia.limbosecure.bot.Bot;
import com.thoxia.limbosecure.bot.BotMessages;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

@RequiredArgsConstructor
public class CommandListener extends ListenerAdapter {

    private final List<String> languages = List.of("TR", "EN", "ES");

    private final Bot bot;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String m = event.getInteraction().getName();
        if (!m.startsWith("setup")) return;

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.getInteraction().deferReply(true).setContent(":x: You can not run this command!").queue();
            return;
        }

        String serverID = event.getInteraction().getOption("server-id").getAsString();
        String language = event.getInteraction().getOption("language").getAsString();
        if (!languages.contains(language)) {
            event.getInteraction().deferReply(true).setContent(":x: Provided language is not supported!").queue();
            return;
        }

        Bot.Language lang = Bot.Language.getLanguage(language);
        BotMessages messages = bot.getMessages().get(lang);

        MessageEmbed embed = new EmbedBuilder().setColor(Color.CYAN)
                .setAuthor(bot.getConfig().getAuthor(), messages.getUrl())
                .setDescription(messages.getEmbedContent())
                .setThumbnail(bot.getConfig().getThumbnail())
                .setFooter(bot.getConfig().getFooter(), bot.getConfig().getFooterIcon())
                .build();
        MessageCreateData message = new MessageCreateBuilder()
                .addEmbeds(embed)
                .addActionRow(
                        Button.primary("limbosecure-verify;" + serverID + ";" + language, messages.getVerifyButton())
                ).build();

        event.getInteraction().deferReply(true).setContent("Embed sent!").queue();

        event.getChannel().sendMessage(message).complete();
    }

}
