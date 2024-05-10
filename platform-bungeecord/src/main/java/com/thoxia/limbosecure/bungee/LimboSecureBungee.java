package com.thoxia.limbosecure.bungee;

import com.bivashy.limbo.NanoLimboBungee;
import com.thoxia.limbosecure.SecurePlugin;
import com.thoxia.limbosecure.backend.RequestHandler;
import com.thoxia.limbosecure.bungee.command.MainCommand;
import com.thoxia.limbosecure.bungee.config.Config;
import com.thoxia.limbosecure.bungee.config.Data;
import com.thoxia.limbosecure.bungee.config.messages.MessagesEN;
import com.thoxia.limbosecure.bungee.listener.CommandListener;
import com.thoxia.limbosecure.bungee.listener.ConnectionListener;
import com.thoxia.limbosecure.bungee.listener.LuckPermsListener;
import com.thoxia.limbosecure.bungee.listener.PluginMessageListener;
import com.thoxia.limbosecure.bungee.logger.BungeeLogger;
import com.thoxia.limbosecure.bungee.session.BungeeSecureHandler;
import com.thoxia.limbosecure.logging.Logger;
import com.thoxia.limbosecure.twofa.TwoFAManager;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bungee.YamlBungeeConfigurer;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import ua.nanit.limbo.server.LimboServer;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Getter
public class LimboSecureBungee extends Plugin implements SecurePlugin {

    @Getter private static LimboSecureBungee instance;

    private final Map<String, BungeeSecureHandler> handlerMap = new HashMap<>();
    private final Data data = new Data(this);

    private TwoFAManager twoFAManager;

    private Config config;
    private MessagesEN messages;

    private LimboServer limboServer;

    private RequestHandler requestHandler;
    private Logger secureLogger;

    @Override
    public void onEnable() {
        instance = this;

        setupConfig();
        data.loadConfig();

        this.secureLogger = new BungeeLogger(this);

        this.requestHandler = new RequestHandler(config.getUnsafeSettings().getApiUrl(),
                config.getUnsafeSettings().getServerId().toString(), secureLogger);

        this.twoFAManager = new TwoFAManager(this, config.getStaffAccounts(),
                config.isSkipIfSameIP(), config.isSkipIfReconnect(),
                new HashSet<>(data.getConfig().getStringList("blocked-users")));

        this.getProxy().registerChannel(PERMISSIONS_UPDATED_CHANNEL_NAME);

        this.getProxy().getPluginManager().registerListener(this, new ConnectionListener(this));
        this.getProxy().getPluginManager().registerListener(this, new CommandListener(this));
        this.getProxy().getPluginManager().registerListener(this, new PluginMessageListener(this));

        this.getProxy().getPluginManager().registerCommand(this, new MainCommand(this));

        if (getProxy().getPluginManager().getPlugin("LuckPerms") != null) {
            new LuckPermsListener(this).init();
        }

        new Metrics(this, BUNGEE_METRIC_ID);

    }

    @Override
    public void onDisable() {
        this.getProxy().unregisterChannel(PERMISSIONS_UPDATED_CHANNEL_NAME);
    }

    @SneakyThrows
    private void setupConfig() {
        config = ConfigManager.create(Config.class, (it) -> {
            it.withConfigurer(new YamlBungeeConfigurer());
            it.withBindFile(new File(getDataFolder(), "config.yml"));
            it.withRemoveOrphans(false);
            it.saveDefaults();
            it.load(true);
        });

        Class<MessagesEN> langClass = (Class<MessagesEN>) Class.forName("com.thoxia.limbosecure.bungee.config.messages.Messages" + config.getLanguage().name());
        messages = ConfigManager.create(langClass, (it) -> {
            it.withConfigurer(new YamlBungeeConfigurer());
            it.withBindFile(new File(getDataFolder(), config.getLanguage().getFileName()));
            it.withRemoveOrphans(false);
            it.saveDefaults();
            it.load(true);
        });
    }

    @SneakyThrows
    @Override
    public void setupServer() {
        limboServer = NanoLimboBungee.getInstance().getServers().get(config.getLimboSettings().getLimboName());
        if (!limboServer.isRunning()) {
            limboServer.start();
        }
    }

    @Override
    public void connectPlayer(Object obj) {
        ProxiedPlayer player = (ProxiedPlayer) obj;
        ServerInfo info = getProxy().getServerInfo(config.getLimboSettings().getLimboName());
        player.connect(info);

        String code = twoFAManager.createCode(player.getName(), config.getCodeChars(), config.getCodeLength());
        BungeeSecureHandler handler = new BungeeSecureHandler(this, player, code);
        handler.start();
        this.handlerMap.put(player.getName(), handler);
    }

    @Override
    public void markVerified(String playerName) {
        ProxiedPlayer player = getProxy().getPlayer(playerName);
        String ip = player.getSocketAddress().toString().split("/")[1];
        twoFAManager.setLastIP(playerName, ip);
        twoFAManager.addVerified(playerName);

        BungeeSecureHandler removed = this.handlerMap.remove(playerName);
        if (removed != null) {
            removed.stop();
        }

        ServerInfo info = getProxy().getServerInfo(config.getLimboSettings().getConnectServer());
        player.connect(info);
    }

}
