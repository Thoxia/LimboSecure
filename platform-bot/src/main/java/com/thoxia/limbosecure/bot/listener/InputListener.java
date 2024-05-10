package com.thoxia.limbosecure.bot.listener;

import com.thoxia.limbosecure.bot.Bot;
import com.thoxia.limbosecure.bot.BotMessages;
import com.thoxia.limbosecure.bot.backend.RequestHandler;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

@RequiredArgsConstructor
public class InputListener extends ListenerAdapter {

    private final Bot bot;

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().startsWith("limbosecure-input-modal")) return;

        String[] split = event.getModalId().split(";");
        String serverId = split[1];
        String language = split[2];
        ModalInteraction interaction = event.getInteraction();

        String input = event.getValue("limbosecure-input").getAsString();
        BotMessages messages = bot.getMessages().get(Bot.Language.getLanguage(language));

        interaction.deferReply(true).queue(hook -> {
            String discordId = event.getUser().getId();
            RequestHandler.Status status = bot.getHandler().isValid(input, discordId, serverId);
            if (status == RequestHandler.Status.RATE_LIMITED) {
                hook.editOriginal(messages.getRateLimitedMessage()).queue();
                return;
            } else if (status == RequestHandler.Status.INVALID) {
                hook.editOriginal(messages.getIncorrectCodeMessage()).queue();
                return;
            }

            bot.getHandler().verify(input, serverId);
            hook.editOriginal(messages.getVerifiedMessage()).queue();
        });
    }

}
