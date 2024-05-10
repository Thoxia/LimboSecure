package com.thoxia.limbosecure.bot;

import com.thoxia.limbosecure.bot.config.Config;
import com.thoxia.limbosecure.bot.config.serializer.CustomSerdes;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.SneakyThrows;

import java.io.File;

public class Main {

    private static Config config;

    public static void main(String[] args) {
        setupConfig();

        new Bot(config.getBotConfig(), config.getMessages()).startBot();
    }

    @SneakyThrows
    private static void setupConfig() {
        config = ConfigManager.create(Config.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer(), new CustomSerdes());
            it.withBindFile(new File("config.yml"));
            it.withRemoveOrphans(false);
            it.saveDefaults();
            it.load(true);
        });
    }

}
