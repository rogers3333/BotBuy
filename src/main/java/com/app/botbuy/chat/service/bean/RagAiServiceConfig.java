package com.app.botbuy.chat.service.bean;

import com.app.botbuy.chat.embedding.RedisChatMemoryStore;
import com.app.botbuy.chat.service.RagAiService;
import com.app.botbuy.chat.tool.ConversationEndTool;
import com.app.botbuy.chat.tool.TransferHumanTool;
import com.app.botbuy.common.AiServiceBeanNameEnum;
import com.app.botbuy.service.PromptService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagAiServiceConfig {

    private final OpenAiStreamingChatModel streamingChatLanguageModel;
    private final OpenAiChatModel openAiChatModel;
    private final DefaultRetrievalAugmentor defaultRetrievalAugmentor;
    private final RedisChatMemoryStore redisChatMemoryStore;
    private final TransferHumanTool transferHumanTool;
    private final ConversationEndTool conversationEndTool;

    @Resource
    private PromptService promptService;

    @Value("${chat.memory.message.max-length:20}")
    private int chatMemoryMaxLength;

    public RagAiServiceConfig(OpenAiStreamingChatModel streamingChatLanguageModel,
                              OpenAiChatModel openAiChatModel,
                              DefaultRetrievalAugmentor defaultRetrievalAugmentor,
                              RedisChatMemoryStore redisChatMemoryStore,
                              TransferHumanTool transferHumanTool,
                              ConversationEndTool conversationEndTool) {
        this.streamingChatLanguageModel = streamingChatLanguageModel;
        this.openAiChatModel = openAiChatModel;
        this.defaultRetrievalAugmentor = defaultRetrievalAugmentor;
        this.redisChatMemoryStore = redisChatMemoryStore;
        this.transferHumanTool = transferHumanTool;
        this.conversationEndTool = conversationEndTool;
    }

    @Bean("ragStreamAiService")
    public RagAiService ragStreamAiService() {
        return AiServices.builder(RagAiService.class)
                .streamingChatModel(streamingChatLanguageModel)
                .chatMemoryProvider(memoryId -> messageWindow(memoryId, chatMemoryMaxLength))
                .tools(transferHumanTool, conversationEndTool)
                .retrievalAugmentor(defaultRetrievalAugmentor)
                .systemMessageProvider(memoryId ->
                        promptService.findByKey(AiServiceBeanNameEnum.RAG_STREAM_AI_SERVICE.getBeanName()))
                .build();
    }

    @Bean("ragAiService")
    public RagAiService ragAiService() {
        return AiServices.builder(RagAiService.class)
                .chatModel(openAiChatModel)
                .chatMemoryProvider(memoryId -> messageWindow(memoryId, chatMemoryMaxLength))
                .tools(transferHumanTool, conversationEndTool)
                .retrievalAugmentor(defaultRetrievalAugmentor)
                .systemMessageProvider(memoryId ->
                        promptService.findByKey(AiServiceBeanNameEnum.RAG_AI_SERVICE.getBeanName()))
                .build();
    }

    private MessageWindowChatMemory messageWindow(Object memoryId, int maxMessages) {
        return MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(maxMessages)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
    }
}
