package com.thoxia.limbosecure.velocity.command;

import com.thoxia.limbosecure.velocity.LimboSecureVelocity;
import com.thoxia.limbosecure.velocity.util.ChatUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

// TODO: make this class pretty
@RequiredArgsConstructor
public class MainCommand implements SimpleCommand {

    private static final List<String> commandCompletions = List.of("reload", "help", "ipblacklist", "nameblacklist", "staff");
    private static final List<String> argsCompletions = List.of("add", "remove", "list");

    private final LimboSecureVelocity plugin;

    @SneakyThrows
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource player = invocation.source();
        if (args.length == 0) {
            player.sendMessage(ChatUtils.format(plugin.getMessages().getIncorrectUse()));
            return;
        }

        if (args[0].equalsIgnoreCase("help")) {
            for (Component c : ChatUtils.format(plugin.getMessages().getHelpMessage())) {
                player.sendMessage(c);
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            plugin.getConfig().load();
            plugin.getMessages().load();
            player.sendMessage(ChatUtils.format(plugin.getMessages().getReloadMessage()));
        }

        else if (args[0].equalsIgnoreCase("staff")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                player.sendMessage(ChatUtils.format("<green>Here is a list of the current staff:"));
                for (Map.Entry<String, String> entry : plugin.getTwoFAManager().getUserToId().entrySet()) {
                    player.sendMessage(ChatUtils.format(" <gray>- <yellow><name> | <id>",
                            Placeholder.unparsed("name", entry.getKey()),
                            Placeholder.unparsed("id", entry.getValue())
                    ));
                }
            } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
                String name = args[2];
                plugin.getTwoFAManager().getUserToId().remove(name);
                player.sendMessage(ChatUtils.format("<green>User removed."));
                plugin.getConfig().set("staff-accounts", plugin.getTwoFAManager().getUserToId());
                plugin.getConfig().save();
            } else if (args.length == 4 && args[1].equalsIgnoreCase("add")) {
                String name = args[2];
                String id = args[3];
                plugin.getTwoFAManager().getUserToId().put(name, id);
                player.sendMessage(ChatUtils.format("<green>User added."));
                plugin.getConfig().set("staff-accounts", plugin.getTwoFAManager().getUserToId());
                plugin.getConfig().save();
            } else {
                player.sendMessage(ChatUtils.format(plugin.getMessages().getIncorrectUse()));
            }
        }

        else if (args[0].equalsIgnoreCase("nameblacklist")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                player.sendMessage(ChatUtils.format("<green>Here is a list of the current blacklisted names:"));
                for (String user : plugin.getTwoFAManager().getBlockedUsers()) {
                    player.sendMessage(ChatUtils.format(" <gray>- <yellow><name>",
                            Placeholder.unparsed("name", user)
                    ));
                }
            } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
                String name = args[2];
                plugin.getTwoFAManager().getBlockedUsers().remove(name.toLowerCase());
                player.sendMessage(ChatUtils.format("<green>User removed."));
                plugin.getData().getRoot().node("blocked-users").set(plugin.getTwoFAManager().getBlockedUsers());
                plugin.getData().save();
            } else if (args.length == 3 && args[1].equalsIgnoreCase("add")) {
                String name = args[2];
                plugin.getTwoFAManager().getBlockedUsers().add(name.toLowerCase());
                player.sendMessage(ChatUtils.format("<green>User added."));
                plugin.getData().getRoot().node("blocked-users").set(plugin.getTwoFAManager().getBlockedUsers());
                plugin.getData().save();
            } else {
                player.sendMessage(ChatUtils.format(plugin.getMessages().getIncorrectUse()));
            }
        }

        else if (args[0].equalsIgnoreCase("ipblacklist")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                player.sendMessage(ChatUtils.format("<green>Here is a list of the current blacklisted IPs:"));
                for (String ip : plugin.getTwoFAManager().getBlockedIps()) {
                    player.sendMessage(ChatUtils.format(" <gray>- <yellow><ip>",
                            Placeholder.unparsed("ip", ip)
                    ));
                }
            } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
                String ip = args[2];
                plugin.getTwoFAManager().getBlockedIps().remove(ip);
                player.sendMessage(ChatUtils.format("<green>User removed."));
            } else if (args.length == 3 && args[1].equalsIgnoreCase("add")) {
                String ip = args[2];
                plugin.getTwoFAManager().getBlockedIps().add(ip);
                player.sendMessage(ChatUtils.format("<green>User added."));
            } else {
                player.sendMessage(ChatUtils.format(plugin.getMessages().getIncorrectUse()));
            }
        } else {
            player.sendMessage(ChatUtils.format(plugin.getMessages().getIncorrectUse()));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] arguments = invocation.arguments();
        if (arguments.length == 0) {
            return commandCompletions;
        }

        if (arguments.length == 2) {
            String argument = arguments[1].toLowerCase();
            return argsCompletions.stream().filter(s -> s.startsWith(argument)).toList();
        }

        if (arguments.length > 2) {
            return Collections.emptyList();
        }

        String argument = arguments[0].toLowerCase();
        return commandCompletions.stream().filter(s -> s.startsWith(argument)).toList();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("limbosecure.admin");
    }

}
