package com.thoxia.limbosecure.twofa;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.thoxia.limbosecure.SecurePlugin;
import com.thoxia.limbosecure.backend.RequestHandler;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class TwoFAManager {

    private final SecurePlugin plugin;
    private final Map<String, String> userToId;
    private final boolean skipSameIP;
    private final boolean skipReconnect;
    private final Set<String> blockedUsers;

    private final Set<String> blockedIps = new HashSet<>();
    private final Set<String> verified = new HashSet<>();

    private final Cache<String, Integer> ipTries = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.HOURS).build();
    private final Cache<String, String> userToIp = CacheBuilder.newBuilder().expireAfterWrite(24, TimeUnit.HOURS).build();
    private final Cache<String, Long> skipVerifyMap = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();
    private final Cache<String, String> codeMap = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    public void addStaff(String playerName, String discordId) {
        this.userToId.put(playerName, discordId);
    }

    public String getDiscordId(String playerName) {
        return this.userToId.get(playerName);
    }

    public boolean isStaff(String playerName) {
        if (getDiscordId(playerName) != null) return true;

        for (String s : this.userToId.keySet()) {
            if (s.equalsIgnoreCase(playerName))
                return true;
        }

        return false;
    }

    public String getLastIP(String playerName) {
        return this.userToIp.getIfPresent(playerName);
    }

    public void setLastIP(String playerName, String ip) {
        this.userToIp.put(playerName, ip);
    }

    public String getPlayerNameFromDiscordId(String discordId) {
        return this.userToId.entrySet().stream()
                .filter(e -> e.getValue().equals(discordId))
                .map(Map.Entry::getKey)
                .findAny().orElse(null);
    }

    public String createCode(String playerName, String chars, int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        this.codeMap.put(playerName, sb.toString());

        String discordId = this.getDiscordId(playerName);
        RequestHandler.ASYNC_EXECUTOR.execute(() -> plugin.getRequestHandler().createCode(sb.toString(), discordId));

        return sb.toString();
    }

    public String getPlayerIdFromCode(String code) {
        return this.codeMap.asMap().entrySet().stream()
                .filter(e -> e.getValue().equals(code))
                .map(Map.Entry::getKey)
                .findAny().orElse(null);
    }

    public void invalidateCode(String code) {
        this.codeMap.asMap().values().remove(code);
    }

    public boolean shouldVerify(String name, String ip) {
        if (skipReconnect && skipVerifyMap.asMap().containsKey(name)) {
            return !ip.equals(getLastIP(name));
        }

        if (skipSameIP && ip.equals(getLastIP(name))) return false;

        return userToId.containsKey(name);
    }

    public void markReconnect(String name) {
        this.skipVerifyMap.put(name, System.currentTimeMillis());
    }

    public boolean isIPBlocked(String ip) {
        return this.blockedIps.contains(ip);
    }

    public void addBlockedIP(String ip) {
        blockedIps.add(ip);
    }

    public void removeBlockedIP(String ip) {
        blockedIps.remove(ip);
    }

    public void addTries(String ip) {
        this.ipTries.put(ip, getTries(ip) + 1);
    }

    public int getTries(String ip) {
        return this.ipTries.asMap().getOrDefault(ip, 0);
    }

    public boolean isBlocked(String name) {
        return this.blockedUsers.contains(name);
    }

    public void addBlocked(String name) {
        this.blockedUsers.add(name);
    }

    public void removeBlocked(String name) {
        this.blockedUsers.remove(name);
    }

    public Set<String> getBlockedUsers() {
        return blockedUsers;
    }

    public Set<String> getBlockedIps() {
        return blockedIps;
    }

    public Map<String, String> getUserToId() {
        return userToId;
    }

    public boolean isVerified(String name) {
        return this.verified.contains(name);
    }

    public void addVerified(String name) {
        this.verified.add(name);
    }

}
