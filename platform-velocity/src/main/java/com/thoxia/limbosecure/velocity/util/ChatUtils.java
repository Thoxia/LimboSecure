package com.thoxia.limbosecure.velocity.util;

import com.thoxia.limbosecure.velocity.LimboSecureVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;
import java.util.stream.Collectors;

public class ChatUtils {

    private final static MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.standard())
            .preProcessor(s -> s.replace("<prefix>", LimboSecureVelocity.getInstance().getMessages().getPrefix()))
            .postProcessor(component -> component.decoration(TextDecoration.ITALIC, false))
            .build();

    public static Component format(String string, TagResolver... placeholders) {
        return MINI_MESSAGE.deserialize(string, placeholders);
    }

    public static List<Component> format(List<String> list, TagResolver... placeholders) {
        return list.stream().map(s -> MINI_MESSAGE.deserialize(s, placeholders)).collect(Collectors.toList());
    }

}
