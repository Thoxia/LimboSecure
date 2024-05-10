package com.thoxia.limbosecure.bungee.config.messages;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

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
public class MessagesEN extends OkaeriConfig {

    private String prefix = "<aqua>LimboSecure <dark_gray>»";

    private String incorrectUse = "<prefix> <red>Incorrect command. Use /limbosecure help.";

    private String reloadMessage = "<prefix> <yellow>Plugin reloaded.";

    private List<String> helpMessage = Arrays.asList(
            "<gold>LimboSecure <gray>- <yellow>2FA Plugin",
            "",
            "<gold>Commands:",
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
            "<gray><click:open_url:https://discord.gg/9vcAHQnZsg>Need help configuring the plugin? Join our discord. (click)"
    );

    private List<String> kick = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>You didn't verify yourself within the provided time.",
            "",
            "<red>Your IP will get blocked if you try a few more times."
    );

    private List<String> ipBlocked = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>Your IP is blocked."
    );

    @Comment("Only works when spigot plugin is installed.")
    private List<String> opBlocked = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>You got admin permissions without being in the staff list.",
            "<red>Ask an admin to add you to the list and re-join."
    );

    private String title = "<gold>LimboSecure";

    private String subtitle = "<yellow>Code: <gold><code>";

    private String message = "<prefix> <gold>Please verify yourself! Code: <yellow><code>";

}
