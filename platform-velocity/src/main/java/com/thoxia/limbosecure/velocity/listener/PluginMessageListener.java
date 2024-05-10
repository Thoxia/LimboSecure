package com.thoxia.limbosecure.velocity.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.thoxia.limbosecure.velocity.LimboSecureVelocity;
import com.thoxia.limbosecure.velocity.util.ChatUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import org.spongepowered.configurate.serialize.SerializationException;

@RequiredArgsConstructor
public class PluginMessageListener {

    private final LimboSecureVelocity plugin;

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().getId().equals(LimboSecureVelocity.PERMISSIONS_UPDATED_CHANNEL.getId())) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String name = in.readUTF();
        String permission = in.readUTF();
        Player player = plugin.getServer().getPlayer(name).orElse(null);
        if (player == null) return;

        if ((permission.equals("op") || plugin.getConfig().getPermissionToListen().contains(permission))
                && !plugin.getTwoFAManager().isStaff(player.getUsername())) {
            player.disconnect(ChatUtils.format(String.join("<newline>", plugin.getMessages().getOpBlocked())));
            plugin.getTwoFAManager().addBlocked(player.getUsername().toLowerCase());
            try {
                plugin.getData().getRoot().node("blocked-users").set(plugin.getTwoFAManager().getBlockedUsers());
                plugin.getData().save();
            } catch (SerializationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
