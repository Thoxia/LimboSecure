package com.thoxia.limbosecure.bungee.config;

import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class Data {

    private final LimboSecureBungee plugin;

    @Getter
    private Configuration config;

    private void createFile() throws IOException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getLogger().info("Created config folder: " + plugin.getDataFolder().mkdir());
        }

        File configFile = new File(plugin.getDataFolder(), "data.yml");

        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile);
            InputStream in = plugin.getResourceAsStream("data.yml");
            in.transferTo(outputStream);
        }
    }

    public void loadConfig() {
        try {
            createFile();
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "data.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(plugin.getDataFolder(), "data.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
