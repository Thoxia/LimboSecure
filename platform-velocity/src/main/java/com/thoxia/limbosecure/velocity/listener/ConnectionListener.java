package com.thoxia.limbosecure.velocity.listener;

import com.thoxia.limbosecure.velocity.LimboSecureVelocity;
import com.thoxia.limbosecure.velocity.util.ChatUtils;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;

@RequiredArgsConstructor
public class ConnectionListener {

    private final LimboSecureVelocity plugin;

    @Subscribe(order = PostOrder.LAST)
    public void onJoin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) return;

        String username = event.getUsername();
        String ip = event.getConnection().getRemoteAddress().getAddress().getHostAddress();
        if (plugin.getTwoFAManager().isIPBlocked(ip)) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                    ChatUtils.format(String.join("<newline>", plugin.getMessages().getIpBlocked()))
            ));
        } else if (!plugin.getTwoFAManager().isStaff(username) && plugin.getTwoFAManager().isBlocked(username.toLowerCase())) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                    ChatUtils.format(String.join("<newline>", plugin.getMessages().getOpBlocked()))
            ));
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onJoin(LoginLimboRegisterEvent event) {
        Player player = event.getPlayer();
        String ip = player.getRemoteAddress().getAddress().getHostAddress();
        if (!plugin.getTwoFAManager().shouldVerify(player.getUsername(), ip)) return;

        event.addOnJoinCallback(() -> plugin.connectPlayer(player));
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getTwoFAManager().isStaff(player.getUsername())) return;
        if (!plugin.getTwoFAManager().isVerified(player.getUsername())) return;

        String ip = player.getRemoteAddress().getAddress().getHostAddress();
        plugin.getTwoFAManager().setLastIP(player.getUsername(), ip);
        plugin.getTwoFAManager().markReconnect(player.getUsername());
    }

}
