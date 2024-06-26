package com.thoxia.limbosecure.bot;

import com.thoxia.limbosecure.bot.backend.RequestHandler;
import com.thoxia.limbosecure.bot.listener.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Bot {

    public static final String HYPERION_ID = "749999019480055939";
    public static final List<String> ACTIVITIES = new ArrayList<>();

    static {
        ACTIVITIES.add("THOXIA | Minecraft Plugins");
        ACTIVITIES.add("Trusted by {servers} servers");
        ACTIVITIES.add("Go check our products!");
    }

    @Getter private final BotConfig config;
    @Getter private final Map<Language, BotMessages> messages;

    @Getter private JDA jda;

    @Getter private RequestHandler handler;

    @SneakyThrows
    public void startBot() {
        handler = new RequestHandler(config.getSecretKey(), config.getApiUrl());

        jda = JDABuilder.createDefault(config.getToken())
                .addEventListeners(
                        new ButtonListener(this),
                        new CommandListener(this),
                        new InputListener(this),
                        new AutoCompleteListener(),
                        new PremiumCommandListener(this)
                )
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .setStatus(config.getStatus())
                .build().awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("setup", "Sends the verify embed.")
                        .addOption(OptionType.STRING, "server-id", "Your server's id. Can be found in config.yml", true)
                        .addOption(OptionType.STRING, "language", "Preferred language (EN, ES, TR)", true, true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .setGuildOnly(true),
                Commands.slash("premium", "Upgrade a server to premium plan.")
                        .addOption(OptionType.STRING, "server-id", "Server's ID", true)
                        .addOption(OptionType.STRING, "server-ip", "Server's IP", true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .setGuildOnly(true)
        ).complete();

        Runtime.getRuntime().addShutdownHook(new Thread(jda::shutdownNow));

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            int i = ACTIVITIES.size() - 1;

            @Override
            public void run() {
                if (i < 0) i = ACTIVITIES.size() - 1;

                String s = ACTIVITIES.get(i);

                int servers = jda.getGuilds().size();

                jda.getPresence().setActivity(Activity.playing(
                        s.replace("{servers}", String.valueOf(servers))));

                i--;
            }
        };

        timer.schedule(task, 1000, 30_000);
    }

    public enum Language {
        EN,
        ES,
        TR
        ;

        public static final Set<Language> LANGUAGES = Arrays.stream(values()).collect(Collectors.toSet());

        public static Language getLanguage(String s) {
            for (Language language : LANGUAGES) {
                if (language.name().equals(s)) {
                    return language;
                }
            }

            // how did we end up here
            throw new RuntimeException("unknown language found");
        }

    }

}
