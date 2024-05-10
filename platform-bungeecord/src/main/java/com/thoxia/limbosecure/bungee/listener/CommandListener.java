package com.thoxia.limbosecure.bungee.listener;

import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class CommandListener implements Listener {

    private final LimboSecureBungee plugin;

    @EventHandler
    public void onCommand(ChatEvent event) {
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        if (!plugin.getTwoFAManager().isStaff(sender.getName())) return;
        if (plugin.getTwoFAManager().isVerified(sender.getName())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onTab(TabCompleteEvent event) {
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        if (!plugin.getTwoFAManager().isStaff(sender.getName())) return;
        if (plugin.getTwoFAManager().isVerified(sender.getName())) return;

        event.getSuggestions().clear();
    }

}
