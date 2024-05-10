package com.thoxia.limbosecure.bungee.command;

import com.thoxia.limbosecure.bungee.LimboSecureBungee;
import com.thoxia.limbosecure.bungee.util.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

// TODO: make this class pretty
public class MainCommand extends Command implements TabExecutor {

    private static final List<String> commandCompletions = List.of("reload", "help", "ipblacklist", "nameblacklist", "staff");
    private static final List<String> argsCompletions = List.of("add", "remove", "list");

    private final LimboSecureBungee plugin;

    public MainCommand(LimboSecureBungee plugin) {
        super("limbosecure", "limbosecure.admin", "lsecure");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getIncorrectUse())));
            return;
        }

        if (args[0].equalsIgnoreCase("help")) {
            for (Component c : ChatUtils.format(plugin.getMessages().getHelpMessage())) {
                player.sendMessage(ChatUtils.formatLegacy(c));
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            plugin.getConfig().load();
            plugin.getMessages().load();
            player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getReloadMessage())));
        }

        else if (args[0].equalsIgnoreCase("staff")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>Here is a list of the current staff:")));
                for (Map.Entry<String, String> entry : plugin.getTwoFAManager().getUserToId().entrySet()) {
                    player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(" <gray>- <yellow><name> | <id>",
                            Placeholder.unparsed("name", entry.getKey()),
                            Placeholder.unparsed("id", entry.getValue())
                    )));
                }
            } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
                String name = args[2];
                plugin.getTwoFAManager().getUserToId().remove(name);
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>User removed.")));
                plugin.getConfig().set("staff-accounts", plugin.getTwoFAManager().getUserToId());
                plugin.getConfig().save();
            } else if (args.length == 4 && args[1].equalsIgnoreCase("add")) {
                String name = args[2];
                String id = args[3];
                plugin.getTwoFAManager().getUserToId().put(name, id);
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>User added.")));
                plugin.getConfig().set("staff-accounts", plugin.getTwoFAManager().getUserToId());
                plugin.getConfig().save();
            } else {
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getIncorrectUse())));
            }
        }

        else if (args[0].equalsIgnoreCase("nameblacklist")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>Here is a list of the current blacklisted names:")));
                for (String user : plugin.getTwoFAManager().getBlockedUsers()) {
                    player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(" <gray>- <yellow><name>",
                            Placeholder.unparsed("name", user)
                    )));
                }
            } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
                String name = args[2];
                plugin.getTwoFAManager().getBlockedUsers().remove(name.toLowerCase());
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>User removed.")));
                plugin.getData().getConfig().set("blocked-users", plugin.getTwoFAManager().getBlockedUsers());
                plugin.getData().saveConfig();
            } else if (args.length == 3 && args[1].equalsIgnoreCase("add")) {
                String name = args[2];
                plugin.getTwoFAManager().getBlockedUsers().add(name.toLowerCase());
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>User added.")));
                plugin.getData().getConfig().set("blocked-users", plugin.getTwoFAManager().getBlockedUsers());
                plugin.getData().saveConfig();
            } else {
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getIncorrectUse())));
            }
        }

        else if (args[0].equalsIgnoreCase("ipblacklist")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>Here is a list of the current blacklisted IPs:")));
                for (String ip : plugin.getTwoFAManager().getBlockedIps()) {
                    player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(" <gray>- <yellow><ip>",
                            Placeholder.unparsed("ip", ip)
                    )));
                }
            } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
                String ip = args[2];
                plugin.getTwoFAManager().getBlockedIps().remove(ip);
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>User removed.")));
            } else if (args.length == 3 && args[1].equalsIgnoreCase("add")) {
                String ip = args[2];
                plugin.getTwoFAManager().getBlockedIps().add(ip);
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format("<green>User added.")));
            } else {
                player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getIncorrectUse())));
            }
        } else {
            player.sendMessage(ChatUtils.formatLegacy(ChatUtils.format(plugin.getMessages().getIncorrectUse())));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return commandCompletions;
        }

        if (args.length == 2) {
            String argument = args[1].toLowerCase();
            return argsCompletions.stream().filter(s -> s.startsWith(argument)).toList();
        }

        if (args.length > 2) {
            return Collections.emptyList();
        }

        String argument = args[0].toLowerCase();
        return commandCompletions.stream().filter(s -> s.startsWith(argument)).toList();
    }

}
