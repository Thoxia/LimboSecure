package com.thoxia.limbosecure.bungee.util;

import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.stream.Collectors;

public class ChatUtils {

    private final static MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.standard())
            .preProcessor(s -> s.replace("<prefix>", LimboSecureBungee.getInstance().getMessages().getPrefix()))
            .postProcessor(component -> component.decoration(TextDecoration.ITALIC, false))
            .build();

    private final static LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer
            .builder().character('§').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static Component format(String string, TagResolver... placeholders) {
        return MINI_MESSAGE.deserialize(string, placeholders);
    }

    public static List<Component> format(List<String> list, TagResolver... placeholders) {
        return list.stream().map(s -> MINI_MESSAGE.deserialize(s, placeholders)).collect(Collectors.toList());
    }

    public static BaseComponent[] formatLegacy(Component component) {
        return TextComponent.fromLegacyText(LEGACY_COMPONENT_SERIALIZER.serialize(component));
    }

}
