package com.thoxia.limbosecure.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotMessages {

    private String embedContent = """
                Hey there our fella staff!
                
                This is our security plugin. LimboSecure.
                When you join the server, it will ask you to verify yourself.
                You must click to the button below and enter the code you have.
                
                And you are all set.
                Yep. That's it.
                """;

    private String url = "https://bbyb.it/creators/359068";

    private String verifyButton = "Verify";
    private String inputLabel = "Enter the code.";
    private String inputPlaceholder = "Your Code";
    private String inputTopGuiLabel = "LimboSecure | Enter your code";

    private String rateLimitedMessage = ":x: Rate limited! Please consider buying premium to support us.";
    private String incorrectCodeMessage = ":x: Incorrect code!";
    private String verifiedMessage = ":white_check_mark: Verified!";

}
