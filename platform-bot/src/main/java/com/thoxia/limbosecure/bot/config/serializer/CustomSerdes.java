package com.thoxia.limbosecure.bot.config.serializer;

import com.thoxia.limbosecure.bot.config.serializer.impl.MessagesSerializer;
import com.thoxia.limbosecure.bot.config.serializer.impl.SettingsSerializer;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import lombok.NonNull;

public class CustomSerdes implements OkaeriSerdesPack {

    @Override
    public void register(@NonNull SerdesRegistry registry) {
        registry.register(new MessagesSerializer());
        registry.register(new SettingsSerializer());
    }

}
