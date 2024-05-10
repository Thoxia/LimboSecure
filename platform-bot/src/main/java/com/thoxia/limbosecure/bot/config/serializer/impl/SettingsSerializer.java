package com.thoxia.limbosecure.bot.config.serializer.impl;

import com.thoxia.limbosecure.bot.BotConfig;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import net.dv8tion.jda.api.OnlineStatus;

public class SettingsSerializer implements ObjectSerializer<BotConfig> {

    @Override
    public boolean supports(@NonNull Class<? super BotConfig> type) {
        return BotConfig.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull BotConfig config, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("token", config.getToken());
        data.add("secret-key", config.getSecretKey());
        data.add("api-url", config.getApiUrl());
        data.add("status", config.getStatus().toString());
        data.add("thumbnail", config.getThumbnail());
        data.add("footer-icon", config.getFooterIcon());
        data.add("footer", config.getFooter());
        data.add("author", config.getAuthor());
    }

    @Override
    public BotConfig deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        String token = data.get("token", String.class);
        String secretKey = data.get("secret-key", String.class);
        String apiUrl = data.get("api-url", String.class);
        OnlineStatus status = OnlineStatus.valueOf(data.get("status", String.class));
        String thumbnail = data.get("thumbnail", String.class);
        String footerIcon = data.get("footer-icon", String.class);
        String footer = data.get("footer", String.class);
        String author = data.get("author", String.class);

        return new BotConfig(token, secretKey, apiUrl, status, author, thumbnail, footer, footerIcon);
    }
}
