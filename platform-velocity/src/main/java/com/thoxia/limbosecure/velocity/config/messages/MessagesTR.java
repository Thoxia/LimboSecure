package com.thoxia.limbosecure.velocity.config.messages;

import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;

import java.util.Arrays;
import java.util.List;

@Header("################################################################")
@Header("#                                                              #")
@Header("#                    -      LimboSecure      -                 #")
@Header("#                                                              #")
@Header("#     Bir sorun mu yaşıyorsun? Discord sunucumuza katıl!       #")
@Header("#                                                              #")
@Header("#           İngilizce: https://discord.gg/9vcAHQnZsg           #")
@Header("#            Türkçe: https://discord.gg/TNQWKySmry             #")
@Header("#                                                              #")
@Header("################################################################")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
@Getter @Setter
public class MessagesTR extends MessagesEN {

    private String prefix = "<aqua>LimboSecure <dark_gray>»";

    private String incorrectUse = "<prefix> <red>Bilinmeyen komut. /limbosecure help ile yardım alabilirsin.";

    private String reloadMessage = "<prefix> <yellow>Eklenti yenilendi.";

    private List<String> helpMessage = Arrays.asList(
            "<gold>LimboSecure <gray>- <yellow>2FA Plugin",
            "",
            "<gold>Komutlar:",
            "<yellow>/limbosecure reload",
            "",
            "<yellow>/limbosecure staff add <name> <discordId>",
            "<yellow>/limbosecure staff remove <name>",
            "<yellow>/limbosecure staff list",
            "",
            "<yellow>/limbosecure nameblacklist add <name>",
            "<yellow>/limbosecure nameblacklist remove <name>",
            "<yellow>/limbosecure nameblacklist list",
            "",
            "<yellow>/limbosecure ipblacklist remove <ip>",
            "<yellow>/limbosecure ipblacklist add <ip>",
            "<yellow>/limbosecure ipblacklist list",
            "",
            "<yellow>/limbosecure help",
            "",
            "<gray><click:open_url:https://discord.gg/9vcAHQnZsg>Eklentide bir hata mı var? Discordumuza katıl! (tıkla)"
    );

    private List<String> kick = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>Sana verilen süre içinde doğrulamayı tamamlamadın.",
            "",
            "<red>Birkaç defa daha denersen yasaklanacaksın."
    );

    private List<String> ipBlocked = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>IP Adresin bloklanmış."
    );

    @Comment("Yetki kontrolü yalnızca spigot eklentisi yüklüyken çalışır.")
    private List<String> opBlocked = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>Hmm. Yetkili listesinde olmadan tehlikeli yetkiler aldın.",
            "<red>Eğer bir hata olduğunu düşünüyorsan yönetici ile konuş."
    );

    private String bossBar = "<aqua>Lütfen kendini doğrula! Kalan süre: <light_purple><remaining>";

    private BossBar.Color bossBarColor = BossBar.Color.WHITE;

    private BossBar.Overlay bossBarOverlay = BossBar.Overlay.PROGRESS;

    private String title = "<gold>LimboSecure";

    private String subtitle = "<yellow>Kod: <gold><code>";

    private String message = "<prefix> <gold>Lütfen kendini doğrula! Kod: <yellow><code>";

}
