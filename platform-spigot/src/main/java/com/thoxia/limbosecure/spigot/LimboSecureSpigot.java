package com.thoxia.limbosecure.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.thoxia.limbosecure.SecurePlugin;
import com.thoxia.limbosecure.spigot.listener.LuckPermsListener;
import com.thoxia.limbosecure.spigot.task.PermissionCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LimboSecureSpigot extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, SecurePlugin.PERMISSIONS_UPDATED_CHANNEL_NAME);

        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            new LuckPermsListener(this).init();
        }

        new PermissionCheckTask(this).runTaskTimer(this, 200, 200);
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void updatePermission(String name, String permission) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(name);
        out.writeUTF(permission);

        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) {
            // okay, we are officially fucked if permission does not come from a trusted source.
            return;
        }

        player.sendPluginMessage(this, SecurePlugin.PERMISSIONS_UPDATED_CHANNEL_NAME, out.toByteArray());
    }

}
