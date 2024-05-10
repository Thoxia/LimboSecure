package com.thoxia.limbosecure.velocity.config;

import com.google.common.io.ByteStreams;
import com.thoxia.limbosecure.velocity.LimboSecureVelocity;
import lombok.SneakyThrows;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Data {

    private final YamlConfigurationLoader loader;
    private final LimboSecureVelocity plugin;
    private final String fileName;

    private ConfigurationNode root;

    public Data(LimboSecureVelocity plugin, String fileName, Path path) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.loader = YamlConfigurationLoader.builder().path(path).build();
    }

    public void create() {
        if (!plugin.getDataDirectory().toFile().exists()) {
            plugin.getDataDirectory().toFile().mkdirs();
        }

        File configFile = new File(plugin.getDataDirectory().toFile(), fileName);
        if (!configFile.exists()) {
            try (InputStream is = plugin.getClass().getClassLoader().getResourceAsStream(fileName);
                 OutputStream os = Files.newOutputStream(configFile.toPath())) {
                configFile.createNewFile();
                ByteStreams.copy(is, os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.root = loader.load();
        } catch (IOException e) {
            plugin.getLogger().error("An error occurred while loading this configuration!");
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void save() {
        loader.save(root);
    }

    @SneakyThrows
    public void reload() {
        loader.load();
    }

    public ConfigurationNode getRoot() {
        return root;
    }

}