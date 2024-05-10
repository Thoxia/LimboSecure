package com.thoxia.limbosecure.spigot.listener;

import com.thoxia.limbosecure.spigot.LimboSecureSpigot;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;

@RequiredArgsConstructor
public class LuckPermsListener {

    private final LimboSecureSpigot plugin;

    public void init() {
        EventBus eventBus = LuckPermsProvider.get().getEventBus();
        eventBus.subscribe(plugin, NodeAddEvent.class, this::onNodeAdd);
    }

    private void onNodeAdd(NodeAddEvent event) {
        if (event.getNode().getType() != NodeType.PERMISSION) return;
        if (event.isUser()) {
            String permission = ((PermissionNode) event.getNode()).getPermission();
            User target = (User) event.getTarget();
            plugin.updatePermission(target.getUsername(), permission);
        }
    }

}
