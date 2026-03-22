package com.app.botbuy.chat.service.bean;

import com.app.botbuy.chat.service.ReasoningAiService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReasoningAiServiceConfig {

    private final OpenAiChatModel openAiChatModel;

    public ReasoningAiServiceConfig(@Qualifier("OpenAiReasoningChatModel") OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @Bean
    public ReasoningAiService reasoningAiService() {
        return AiServices.builder(ReasoningAiService.class)
                .chatLanguageModel(openAiChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }
}
