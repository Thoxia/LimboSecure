package com.thoxia.limbosecure.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.OnlineStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class BotConfig {

    private String token = "YOUR_TOKEN";
    private String secretKey = "YOUR_SECRET_KEY";
    private OnlineStatus status = OnlineStatus.ONLINE;

    private String author = "LimboSecure";
    private String thumbnail = "https://i.imgur.com/s7FxmdJ.png";
    private String footer = "Thoxia - LimboSecure | 2024";
    private String footerIcon = "https://i.imgur.com/s7FxmdJ.png";

}
