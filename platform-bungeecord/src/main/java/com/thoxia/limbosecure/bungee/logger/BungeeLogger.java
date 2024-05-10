package com.thoxia.limbosecure.bungee.logger;

import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import com.thoxia.limbosecure.logging.Logger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BungeeLogger implements Logger {

    private final LimboSecureBungee plugin;

    @Override
    public void info(String message) {
        plugin.getLogger().info(message);
    }

    @Override
    public void warn(String message) {
        plugin.getLogger().warning(message);
    }

    @Override
    public void severe(String message) {
        plugin.getLogger().severe(message);
    }

}
