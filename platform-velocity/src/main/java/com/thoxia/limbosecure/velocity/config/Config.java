package com.thoxia.limbosecure.velocity.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.player.GameMode;

import java.util.*;

@Header("################################################################")
@Header("#                                                              #")
@Header("#                    -      LimboSecure      -                 #")
@Header("#                                                              #")
@Header("#    Need help configuring the plugin? Join our discord!       #")
@Header("#                                                              #")
@Header("#      English Speakers: https://discord.gg/9vcAHQnZsg         #")
@Header("#      Turkish Speakers: https://discord.gg/TNQWKySmry         #")
@Header("#                                                              #")
@Header("################################################################")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
@Getter @Setter
public class Config extends OkaeriConfig {

    @Exclude
    private static final Map<String, String> DEFAULT_STAFFS = new HashMap<>();

    static {
        DEFAULT_STAFFS.put("HyperionDev", "749999019480055939");
    }

    @Comment("Currently supports: EN, ES, TR")
    private Language language = Language.EN;

    private Map<String, String> staffAccounts = new HashMap<>(DEFAULT_STAFFS);

    private Limbo limboSettings = new Limbo();

    @Comment("Do not change these settings!!!")
    private Unsafe unsafeSettings = new Unsafe();

    @Comment({"Should player skip verify session if their IP is the same?", "We store IPs only for 24 hours. (in-memory)"})
    @CustomKey("skip-if-same-ip")
    private boolean skipIfSameIP = true;

    @Comment({"Should player skip verify session if they reconnect within 15 minutes?"})
    @CustomKey("skip-if-reconnected")
    private boolean skipIfReconnect = true;

    @Comment({"If set to true, we will check for new OPs every few seconds.",
            "Spigot plugin needs to be installed.",
            "If luckperms is installed in any of the servers (including proxy) we will listen for a set of permissions too."
    })
    @CustomKey("block-ops")
    private boolean blockOPs = true;

    @Comment("Related to the setting above.")
    private List<String> permissionToListen = Arrays.asList(
            "*",
            "limbosecure.admin"
    );

    @Comment({"Player's IP will get be blocked after X tries.", "Stored only in-memory. Meaning it will get deleted after a restart.", "Can be disabled using -1"})
    private int maxKicksBan = 3;

    @Comment("Players will have X seconds to verify themselves.")
    private int timer = 90;

    @Comment({"We will create a random text out of these characters."})
    private String codeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    @Comment({"We will create a random text with this length."})
    private int codeLength = 7;

    @Getter @Setter
    public static class Limbo extends OkaeriConfig {

        private Dimension dimension = Dimension.THE_END;
        private GameMode gameMode = GameMode.ADVENTURE;

    }

    @Getter @Setter
    public static class Unsafe extends OkaeriConfig {

        private String apiUrl = "https://limbosecure.thoxia.com";

        @Comment("Use this id to setup the bot.")
        private UUID serverId = UUID.randomUUID();

    }

    @RequiredArgsConstructor
    @Getter
    public static enum Language {
        EN("messages_en.yml"),
        TR("messages_tr.yml"),
        ES("messages_es.yml"),
        ;

        private final String fileName;

    }

}
