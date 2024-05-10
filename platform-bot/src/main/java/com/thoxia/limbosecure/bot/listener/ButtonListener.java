package com.thoxia.limbosecure.bot.listener;

import com.thoxia.limbosecure.bot.Bot;
import com.thoxia.limbosecure.bot.BotMessages;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@RequiredArgsConstructor
public class ButtonListener extends ListenerAdapter {

    private final Bot bot;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getButton().getId();
        if (id == null) return;
        if (!id.startsWith("limbosecure-verify")) return;

        String serverId = id.split(";")[1];
        String language = id.split(";")[2];
        ButtonInteraction interaction = event.getInteraction();

        BotMessages messages = bot.getMessages().get(Bot.Language.getLanguage(language));

        TextInput input = TextInput.create("limbosecure-input", messages.getInputLabel(), TextInputStyle.SHORT)
                .setPlaceholder(messages.getInputPlaceholder())
                .setMinLength(1)
                .setRequired(true)
                .setMaxLength(100).build();

        Modal modal = Modal.create("limbosecure-input-modal;" + serverId + ";" + language, messages.getInputTopGuiLabel())
                .addComponents(ActionRow.of(input)).build();

        interaction.replyModal(modal).queue();
    }
}
