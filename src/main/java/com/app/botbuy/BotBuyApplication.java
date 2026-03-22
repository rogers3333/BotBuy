package com.app.botbuy;

import com.app.botbuy.config.BotBuyPromptProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BotBuyPromptProperties.class)
public class BotBuyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotBuyApplication.class, args);
    }

}
