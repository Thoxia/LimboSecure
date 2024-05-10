package com.thoxia.limbosecure.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.thoxia.limbosecure.SecurePlugin;
import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import com.thoxia.limbosecure.bungee.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class PluginMessageListener implements Listener {

    private final LimboSecureBungee plugin;

    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(SecurePlugin.PERMISSIONS_UPDATED_CHANNEL_NAME)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String name = in.readUTF();
        String permission = in.readUTF();
        ProxiedPlayer player = plugin.getProxy().getPlayer(name);
        if (player == null) return;

        if ((permission.equals("op") || plugin.getConfig().getPermissionToListen().contains(permission))
                && !plugin.getTwoFAManager().isStaff(player.getName())) {

            player.disconnect(ChatUtils.formatLegacy(ChatUtils.format(String.join("<newline>", plugin.getMessages().getOpBlocked()))));

            plugin.getTwoFAManager().addBlocked(player.getName().toLowerCase());

            plugin.getData().getConfig().set("blocked-users", plugin.getTwoFAManager().getBlockedUsers());
            plugin.getData().saveConfig();

        }
    }

}
