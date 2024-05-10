package com.thoxia.limbosecure.velocity.listener;

import com.thoxia.limbosecure.velocity.LimboSecureVelocity;
import com.thoxia.limbosecure.velocity.util.ChatUtils;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.spongepowered.configurate.serialize.SerializationException;

@RequiredArgsConstructor
public class LuckPermsListener {

    private final LimboSecureVelocity plugin;

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
            Player player = plugin.getServer().getPlayer(target.getUniqueId()).orElse(null);
            if (plugin.getConfig().getPermissionToListen().contains(permission)
                    && !plugin.getTwoFAManager().isStaff(target.getFriendlyName())) {
                if (player != null)
                    player.disconnect(ChatUtils.format(String.join("<newline>", plugin.getMessages().getOpBlocked())));

                plugin.getTwoFAManager().addBlocked(target.getUsername());

                target.data().remove(event.getNode());
                try {
                    plugin.getData().getRoot().node("blocked-users").set(plugin.getTwoFAManager().getBlockedUsers());
                    plugin.getData().save();
                } catch (SerializationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

}
