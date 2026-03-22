package com.app.botbuy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "botbuy.prompt")
public class BotBuyPromptProperties {

    private String llmImgRecognize = "";
    private String ragStreamAiService = "";
    private String ragAiService = "";
}
