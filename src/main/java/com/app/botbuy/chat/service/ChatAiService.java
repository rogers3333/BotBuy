package com.app.botbuy.chat.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface ChatAiService {

    String SYSTEM_ROLE_MESSAGE = "You are a professional human customer service representative, "
            + "mainly responsible for technical support work. You have excellent professional qualities and a friendly service attitude. "
            + "Conversing with you will make people feel relaxed and happy, and you can quickly help customers solve problems";

    @SystemMessage(SYSTEM_ROLE_MESSAGE)
    Flux<String> streamChat(@MemoryId String memoryId, @UserMessage String message);
}
