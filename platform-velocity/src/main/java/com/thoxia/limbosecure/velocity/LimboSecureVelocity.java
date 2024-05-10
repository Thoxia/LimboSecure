package com.thoxia.limbosecure.velocity;

import com.google.inject.Inject;
import com.thoxia.limbosecure.SecurePlugin;
import com.thoxia.limbosecure.backend.RequestHandler;
import com.thoxia.limbosecure.logging.Logger;
import com.thoxia.limbosecure.twofa.TwoFAManager;
import com.thoxia.limbosecure.velocity.command.MainCommand;
import com.thoxia.limbosecure.velocity.config.Config;
import com.thoxia.limbosecure.velocity.config.Data;
import com.thoxia.limbosecure.velocity.config.messages.MessagesEN;
import com.thoxia.limbosecure.velocity.listener.ConnectionListener;
import com.thoxia.limbosecure.velocity.listener.LuckPermsListener;
import com.thoxia.limbosecure.velocity.listener.PluginMessageListener;
import com.thoxia.limbosecure.velocity.logging.VelocityLogger;
import com.thoxia.limbosecure.velocity.session.SecureHandler;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;
import lombok.SneakyThrows;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.player.LimboPlayer;
import org.bstats.velocity.Metrics;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Plugin(
        id = "limbosecure",
        name = "LimboSecure",
        version = "1.0.0",
        description = "Ultimate 2FA plugin using LimboAPI.",
        url = "https://thoxia.com",
        authors = {"Thoxia", "hyperion"},
        dependencies = {
                @Dependency(id = "limboapi")
        }
)
@Getter
public class LimboSecureVelocity implements SecurePlugin {

    public static final MinecraftChannelIdentifier PERMISSIONS_UPDATED_CHANNEL = MinecraftChannelIdentifier.from(PERMISSIONS_UPDATED_CHANNEL_NAME);

    @Getter private static LimboSecureVelocity instance;

    private final Map<String, LimboPlayer> limboPlayerMap = new HashMap<>();

    private Data data;

    @Inject
    private org.slf4j.Logger logger;
    @Inject @DataDirectory
    private Path dataDirectory;
    @Inject
    private ProxyServer server;
    @Inject
    private Metrics.Factory metricsFactory;

    private TwoFAManager twoFAManager;

    private LimboFactory factory;
    private Limbo limboServer;

    private Config config;
    private MessagesEN messages;

    private RequestHandler requestHandler;
    private Logger secureLogger;

    @SneakyThrows
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        instance = this;

        setupConfig();

        this.secureLogger = new VelocityLogger(this);

        data = new Data(this, "data.yml", Paths.get(dataDirectory.toString(), "data.yml"));
        data.create();

        this.factory = (LimboFactory) this.server.getPluginManager().getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow();

        this.requestHandler = new RequestHandler(config.getUnsafeSettings().getApiUrl(),
                config.getUnsafeSettings().getServerId().toString(), secureLogger);

        HashSet<String> blocked = new HashSet<>(data.getRoot().node("blocked-users").getList(String.class, new ArrayList<>()));
        this.twoFAManager = new TwoFAManager(this, config.getStaffAccounts(),
                config.isSkipIfSameIP(), config.isSkipIfReconnect(), blocked);

        setupServer();

        server.getChannelRegistrar().register(PERMISSIONS_UPDATED_CHANNEL);

        server.getEventManager().register(this, new PluginMessageListener(this));
        server.getEventManager().register(this, new ConnectionListener(this));

        CommandMeta meta = server.getCommandManager().metaBuilder("limbosecure").aliases("lsecure").build();
        server.getCommandManager().register(meta, new MainCommand(this));

        if (!this.getServer().getPluginManager().getPlugin("luckperms").isEmpty()) {
            new LuckPermsListener(this).init();
        }

        metricsFactory.make(this, VELOCITY_METRIC_ID);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {

    }

    @SneakyThrows
    private void setupConfig() {
        config = ConfigManager.create(Config.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(new File(this.dataDirectory.toFile(), "config.yml"));
            it.withRemoveOrphans(false);
            it.saveDefaults();
            it.load(true);
        });

        Class<MessagesEN> langClass = (Class<MessagesEN>) Class.forName("com.thoxia.limbosecure.velocity.config.messages.Messages" + config.getLanguage().name());
        messages = ConfigManager.create(langClass, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(new File(this.dataDirectory.toFile(), config.getLanguage().getFileName()));
            it.withRemoveOrphans(false);
            it.saveDefaults();
            it.load(true);
        });
    }

    @Override
    public void setupServer() {
        VirtualWorld world = this.factory.createVirtualWorld(
                config.getLimboSettings().getDimension(),
                0, 256, 0, 0, 0
        );

        limboServer = factory.createLimbo(world)
                .setName("LimboSecure")
                .setWorldTime(0)
                .setGameMode(config.getLimboSettings().getGameMode());
    }

    @Override
    public void connectPlayer(Object obj) {
        Player player = (Player) obj;

        limboServer.spawnPlayer(player, new SecureHandler(this, player,
                twoFAManager.createCode(player.getUsername(), config.getCodeChars(), config.getCodeLength())));
    }

    @Override
    public void markVerified(String playerName) {
        LimboPlayer limboPlayer = limboPlayerMap.get(playerName);
        String ip = limboPlayer.getProxyPlayer().getRemoteAddress().getAddress().getHostAddress();
        twoFAManager.setLastIP(limboPlayer.getProxyPlayer().getUsername(), ip);
        twoFAManager.addVerified(limboPlayer.getProxyPlayer().getUsername());

        // disconnect player from limbo, let LimboAPI handle rest
        limboPlayer.disconnect();
    }

}
