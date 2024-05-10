package com.thoxia.limbosecure.bungee.config.messages;

import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Header("################################################################")
@Header("#                                                              #")
@Header("#                    -      LimboSecure      -                 #")
@Header("#                                                              #")
@Header("# ¿Necesitas ayuda configurando este plugin? ¡Unete a discord! #")
@Header("#                                                              #")
@Header("#      Soporte inglés: https://discord.gg/9vcAHQnZsg           #")
@Header("#      Soporte turco: https://discord.gg/TNQWKySmry            #")
@Header("#                                          Traducido por M16X2 #")
@Header("################################################################")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
@Getter @Setter
public class MessagesES extends MessagesEN {

    private String prefix = "<aqua>LimboSecure <dark_gray>»";

    private String incorrectUse = "<prefix> <red>Comando incorrecto. Usa /limbosecure help.";

    private String reloadMessage = "<prefix> <yellow>Plugin recargado.";

    private List<String> helpMessage = Arrays.asList(
            "<gold>LimboSecure <gray>- <yellow>2FA Plugin",
            "",
            "<gold>Comandos:",
            "<yellow>/limbosecure reload",
            "",
            "<yellow>/limbosecure staff add <nombre> <discordId>",
            "<yellow>/limbosecure staff remove <nombre>",
            "<yellow>/limbosecure staff list",
            "",
            "<yellow>/limbosecure nameblacklist add <nombre>",
            "<yellow>/limbosecure nameblacklist remove <nombre>",
            "<yellow>/limbosecure nameblacklist list",
            "",
            "<yellow>/limbosecure ipblacklist remove <ip>",
            "<yellow>/limbosecure ipblacklist add <ip>",
            "<yellow>/limbosecure ipblacklist list",
            "",
            "<yellow>/limbosecure help",
            "",
            "<gray><click:open_url:https://discord.gg/9vcAHQnZsg>¿Necesitas ayuda configurando este plugin? Unete a discord. (clic)"
    );

    private List<String> kick = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>No verificaste tu identidad dentro del tiempo proporcionado.",
            "",
            "<red>Tu IP será bloqueada si intentas algunas veces más."
    );

    private List<String> ipBlocked = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>Tu IP está bloqueada."
    );

    @Comment("Solo funciona cuando el plugin de Spigot está instalado.")
    private List<String> opBlocked = Arrays.asList(
            "<aqua>LimboSecure",
            "",
            "<red>Obtuviste permisos de administrador sin estar en la lista del personal.",
            "<red>Pide a un administrador que te añada a la lista y vuelve a unirte"
    );

    @Comment("Se utilizará en Discord, así que no uses códigos de color.")
    private String incorrectCode = "El código proporcionado es incorrecto.";

    @Comment("Se utilizará en Discord, así que no uses códigos de color.")
    private String notYourCode = "¡Este no es tu código!";

    @Comment("Se utilizará en Discord, así que no uses códigos de color.")
    private String verified = "¡Verificado correctamente!";

    private String title = "<gold>LimboSecure";

    private String subtitle = "<yellow>Código: <gold><code>";

    private String message = "<prefix> <gold>¡Por favor, verifícate! Código: <yellow><code>";

}
