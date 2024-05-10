package com.thoxia.limbosecure.bungee.listener;

import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import com.thoxia.limbosecure.bungee.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@RequiredArgsConstructor
public class LuckPermsListener {

    private final LimboSecureBungee plugin;

    public void init() {
        EventBus eventBus = LuckPermsProvider.get().getEventBus();
        eventBus.subscribe(plugin, NodeAddEvent.class, this::onNodeAdd);
    }

    private void onNodeAdd(NodeAddEvent event) {
        if (!plugin.getConfig().isBlockOPs()) return;
        if (event.getNode().getType() != NodeType.PERMISSION) return;

        if (event.isUser()) {
            String permission = ((PermissionNode) event.getNode()).getPermission();
            User target = (User) event.getTarget();
            ProxiedPlayer player = plugin.getProxy().getPlayer(target.getUniqueId());
            if (plugin.getConfig().getPermissionToListen().contains(permission)
                    && !plugin.getTwoFAManager().isStaff(target.getFriendlyName())) {
                if (player != null)
                    player.disconnect(ChatUtils.formatLegacy(ChatUtils.format(String.join("<newline>", plugin.getMessages().getOpBlocked()))));

                plugin.getTwoFAManager().addBlocked(target.getUsername());

                target.data().remove(event.getNode());
                plugin.getData().getConfig().set("blocked-users", plugin.getTwoFAManager().getBlockedUsers());
                plugin.getData().saveConfig();
            }
        }
    }

}
