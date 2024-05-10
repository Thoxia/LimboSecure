package com.thoxia.limbosecure.bungee.session;

import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import com.thoxia.limbosecure.bungee.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BungeeSecureHandler {

    private final LimboSecureBungee plugin;
    private final ProxiedPlayer player;
    private final String code;

    private int time = 1;

    private ScheduledTask task;

    public void start() {
        task = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if (time >= plugin.getConfig().getTimer()) {
                String ip = player.getSocketAddress().toString().split("/")[1].split(":")[0];
                plugin.getTwoFAManager().addTries(ip);

                if (plugin.getConfig().getMaxKicksBan() != -1 &&
                        plugin.getTwoFAManager().getTries(ip) >= plugin.getConfig().getMaxKicksBan()) {
                    plugin.getTwoFAManager().addBlockedIP(ip);
                }

                player.disconnect(ChatUtils.formatLegacy(
                        ChatUtils.format(String.join("<newline>", plugin.getMessages().getKick()))));

                stop();
                return;
            }

            if (time % 3 == 0) {
                if (plugin.getRequestHandler().isVerified(code, plugin.getTwoFAManager().getDiscordId(player.getName()))) {
                    plugin.markVerified(player.getName());
                    return;
                }
            }

            Title title = plugin.getProxy().createTitle();
            title.title(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getTitle(), Placeholder.unparsed("code", code))));
            title.subTitle(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getSubtitle(), Placeholder.unparsed("code", code))));
            title.fadeIn(0);
            title.fadeOut(0);
            title.stay(40);
            title.send(player);

            if (time <= 1 || time % 5 == 0) {
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getMessage(), Placeholder.unparsed("code", code))));
            }

            time++;
        }, 0, 1, TimeUnit.SECONDS);

    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


}
