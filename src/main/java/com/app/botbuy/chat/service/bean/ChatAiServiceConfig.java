package com.app.botbuy.chat.service.bean;

import com.app.botbuy.chat.embedding.RedisChatMemoryStore;
import com.app.botbuy.chat.service.ChatAiService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatAiServiceConfig {

    private final OpenAiStreamingChatModel streamingChatLanguageModel;
    private final RedisChatMemoryStore redisChatMemoryStore;

    public ChatAiServiceConfig(OpenAiStreamingChatModel streamingChatLanguageModel,
                               RedisChatMemoryStore redisChatMemoryStore) {
        this.streamingChatLanguageModel = streamingChatLanguageModel;
        this.redisChatMemoryStore = redisChatMemoryStore;
    }

    @Bean
    public ChatAiService chatAiService() {
        return AiServices.builder(ChatAiService.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(redisChatMemoryStore)
                        .build())
                .build();
    }
}
