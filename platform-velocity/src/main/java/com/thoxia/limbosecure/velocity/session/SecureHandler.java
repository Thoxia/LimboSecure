package com.thoxia.limbosecure.velocity.session;

import com.thoxia.limbosecure.velocity.LimboSecureVelocity;
import com.thoxia.limbosecure.velocity.util.ChatUtils;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SecureHandler implements LimboSessionHandler {

    private final LimboSecureVelocity plugin;
    private final Player proxyPlayer;
    private final String code;

    private final long joinTime = System.currentTimeMillis();

    private BossBar bossBar;
    private Title title;

    private int time = 1;

    private ScheduledFuture task;

    @Override
    public void onSpawn(Limbo server, LimboPlayer player) {
        player.disableFalling();

        plugin.getLimboPlayerMap().put(proxyPlayer.getUsername(), player);

        bossBar = BossBar.bossBar(
                Component.empty(),
                1.0F,
                plugin.getMessages().getBossBarColor(),
                plugin.getMessages().getBossBarOverlay()
        );

        title = Title.title(
                ChatUtils.format(plugin.getMessages().getTitle()),
                ChatUtils.format(plugin.getMessages().getSubtitle(), Placeholder.unparsed("code", code)),
                Title.Times.times(Duration.ZERO, Duration.of(2, ChronoUnit.SECONDS), Duration.ZERO)
        );

        proxyPlayer.showBossBar(bossBar);

        task = player.getScheduledExecutor().scheduleAtFixedRate(() -> {
            if (time >= plugin.getConfig().getTimer()) {
                String ip = proxyPlayer.getRemoteAddress().getAddress().getHostAddress();
                plugin.getTwoFAManager().addTries(ip);

                if (plugin.getConfig().getMaxKicksBan() != -1 &&
                        plugin.getTwoFAManager().getTries(ip) >= plugin.getConfig().getMaxKicksBan()) {
                    plugin.getTwoFAManager().addBlockedIP(ip);
                }

                proxyPlayer.disconnect(ChatUtils.format(String.join("<newline>", plugin.getMessages().getKick())));
                return;
            }

            if (time % 3 == 0) {
                if (plugin.getRequestHandler().isVerified(code, plugin.getTwoFAManager().getDiscordId(proxyPlayer.getUsername()))) {
                    plugin.markVerified(proxyPlayer.getUsername());
                    return;
                }
            }

            proxyPlayer.showTitle(title);

            if (time <= 1 || time % 5 == 0) {
                proxyPlayer.sendMessage(ChatUtils.format(plugin.getMessages().getMessage(), Placeholder.unparsed("code", code)));
            }

            int timer = plugin.getConfig().getTimer() * 1000;
            float multiplier = 1000.0F / timer;
            float secondsLeft = (timer - (System.currentTimeMillis() - this.joinTime)) / 1000.0F;
            this.bossBar.name(ChatUtils.format(plugin.getMessages().getBossBar(), Placeholder.unparsed("remaining", String.valueOf((int) secondsLeft))));
            this.bossBar.progress(Math.min(1.0F, secondsLeft * multiplier));

            time++;
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDisconnect() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }

        proxyPlayer.hideBossBar(bossBar);
        plugin.getLimboPlayerMap().remove(proxyPlayer.getUsername());
    }
}
