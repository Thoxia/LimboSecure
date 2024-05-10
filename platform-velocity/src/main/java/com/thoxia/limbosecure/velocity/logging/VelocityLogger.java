package com.thoxia.limbosecure.velocity.logging;

import com.thoxia.limbosecure.logging.Logger;
import com.thoxia.limbosecure.velocity.LimboSecureVelocity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityLogger implements Logger {

    private final LimboSecureVelocity plugin;

    @Override
    public void info(String message) {
        plugin.getLogger().info(message);
    }

    @Override
    public void warn(String message) {
        plugin.getLogger().warn(message);
    }

    @Override
    public void severe(String message) {
        plugin.getLogger().error(message);
    }

}
