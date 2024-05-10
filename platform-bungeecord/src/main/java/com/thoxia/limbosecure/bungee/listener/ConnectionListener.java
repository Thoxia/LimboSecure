package com.thoxia.limbosecure.bungee.listener;

import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import com.thoxia.limbosecure.bungee.session.BungeeSecureHandler;
import com.thoxia.limbosecure.bungee.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ConnectionListener implements Listener {

    private final LimboSecureBungee plugin;

    @EventHandler
    public void onJoin(PreLoginEvent event) {
        String name = event.getConnection().getName();
        String ip = event.getConnection().getSocketAddress().toString().split("/")[1].split(":")[0];

        if (plugin.getTwoFAManager().isIPBlocked(ip)) {
            event.setCancelled(true);
            event.setCancelReason(ChatUtils.formatLegacy(
                    ChatUtils.format(String.join("<newline>", plugin.getMessages().getIpBlocked()))
            ));
        } else if (!plugin.getTwoFAManager().isStaff(name) && plugin.getTwoFAManager().isBlocked(name.toLowerCase())) {
            event.setCancelled(true);
            event.setCancelReason(ChatUtils.formatLegacy(
                    ChatUtils.format(String.join("<newline>", plugin.getMessages().getOpBlocked()))
            ));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String ip = player.getSocketAddress().toString().split("/")[1].split(":")[0];
        if (!plugin.getTwoFAManager().shouldVerify(player.getName(), ip)) return;

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            plugin.connectPlayer(player);
        }, 500, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (!plugin.getTwoFAManager().isStaff(player.getName())) return;

        BungeeSecureHandler remove = plugin.getHandlerMap().remove(player.getName());
        if (remove != null)
            remove.stop();

        if (!plugin.getTwoFAManager().isVerified(player.getName())) return;

        String ip = player.getSocketAddress().toString().split("/")[1].split(":")[0];
        plugin.getTwoFAManager().setLastIP(player.getName(), ip);
        plugin.getTwoFAManager().markReconnect(player.getName());
    }

}
