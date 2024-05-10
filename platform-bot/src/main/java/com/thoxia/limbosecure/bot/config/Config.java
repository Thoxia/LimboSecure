package com.thoxia.limbosecure.bot.config;

import com.thoxia.limbosecure.bot.Bot;
import com.thoxia.limbosecure.bot.BotConfig;
import com.thoxia.limbosecure.bot.BotMessages;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
@Getter
@Setter
public class Config extends OkaeriConfig {

    @Exclude
    private static final Map<Bot.Language, BotMessages> MESSAGES_MAP = new HashMap<>();

    static {
        MESSAGES_MAP.put(Bot.Language.EN, new BotMessages());
        MESSAGES_MAP.put(Bot.Language.TR, new BotMessages());
        MESSAGES_MAP.put(Bot.Language.ES, new BotMessages());
    }

    private BotConfig botConfig = new BotConfig();

    private Map<Bot.Language, BotMessages> messages = new HashMap<>(MESSAGES_MAP);

}
