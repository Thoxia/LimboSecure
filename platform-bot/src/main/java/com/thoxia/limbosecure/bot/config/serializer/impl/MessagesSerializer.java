package com.thoxia.limbosecure.bot.config.serializer.impl;

import com.thoxia.limbosecure.bot.BotMessages;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;

public class MessagesSerializer implements ObjectSerializer<BotMessages> {

    @Override
    public boolean supports(@NonNull Class<? super BotMessages> type) {
        return BotMessages.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull BotMessages messages, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("incorrect-code", messages.getIncorrectCodeMessage());
        data.add("rate-limited", messages.getRateLimitedMessage());
        data.add("url", messages.getUrl());
        data.add("verified", messages.getVerifiedMessage());
        data.add("verify-button", messages.getVerifyButton());
        data.add("input-placeholder", messages.getInputPlaceholder());
        data.add("input-label", messages.getInputLabel());
        data.add("embed-content", messages.getEmbedContent());
        data.add("input-top-gui-label", messages.getInputTopGuiLabel());
    }

    @Override
    public BotMessages deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        String incorrectCode = data.get("incorrect-code", String.class);
        String url = data.get("url", String.class);
        String rateLimited = data.get("rate-limited", String.class);
        String verified = data.get("verified", String.class);
        String verifyButton = data.get("verify-button", String.class);
        String inputPlaceholder = data.get("input-placeholder", String.class);
        String inputLabel = data.get("input-label", String.class);
        String embedContent = data.get("embed-content", String.class);
        String topGui = data.get("input-top-gui-label", String.class);

        return new BotMessages(embedContent, url, verifyButton, inputLabel,
                inputPlaceholder, topGui, rateLimited, incorrectCode, verified);
    }
}
