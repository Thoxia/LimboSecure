package com.thoxia.limbosecure.spigot.task;

import com.thoxia.limbosecure.spigot.LimboSecureSpigot;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class PermissionCheckTask extends BukkitRunnable {

    private final LimboSecureSpigot plugin;

    private Set<OfflinePlayer> lastOps = Bukkit.getOperators();

    @Override
    public void run() {
        Set<OfflinePlayer> one = new HashSet<>(lastOps);
        Set<OfflinePlayer> two = new HashSet<>(Bukkit.getOperators());
        two.removeAll(one);

        for (OfflinePlayer offlinePlayer : two) {
            String name = offlinePlayer.getName();
            if (name == null) continue;

            plugin.updatePermission(name.toLowerCase(), "op");
        }

        lastOps = Bukkit.getOperators();
    }

}
