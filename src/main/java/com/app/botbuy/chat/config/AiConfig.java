package com.app.botbuy.chat.config;

import com.aliyun.dashvector.DashVectorClient;
import com.aliyun.dashvector.DashVectorClientConfig;
import com.app.botbuy.chat.embedding.DashVectorEmbeddingStore;
import com.app.botbuy.common.AiServiceBeanNameEnum;
import com.app.botbuy.service.PromptService;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.VisionQueryTextExtractor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${chat.open-ai.api-key}")
    private String openAiApiKey;

    @Value("${embedding.dash-vector.endpoint}")
    private String dashVectorEndpoint;

    @Value("${embedding.dash-vector.api-key}")
    private String dashVectorApiKey;

    @Value("${embedding.dash-vector.collection-name}")
    private String dashVectorCollectionName;

    @Value("${embedding.dash-vector.client.request-timeout}")
    private float dashVectorRequestTimeout;

    @Value("${embedding.dash-vector.query.max-score}")
    private double dashVectorQueryMaxScore;

    @Value("${embedding.dash-vector.query.max-results}")
    private int dashVectorQueryMaxResults;

    @Value("${log.chat-model.request.enable}")
    private boolean chatModelLogRequest;

    @Value("${log.chat-model.response.enable}")
    private boolean chatModelLogResponse;

    @Value("${log.embedding.request.enable}")
    private boolean embeddingModelLogRequest;

    @Value("${log.embedding.response.enable}")
    private boolean embeddingModelLogResponse;

    @Value("${embedding.open-ai.model.name:text-embedding-3-small}")
    private String embeddingModelName;

    @Value("${chat.open-ai.model.name:gpt-4o-mini}")
    private String chatModelName;

    @Value("${chat.open-ai.reasoning.model.name:o1-mini}")
    private String reasoningModelName;

    @Value("${chat.open-ai.base-url-proxy}")
    private String openAiBaseUrl;

    @Resource
    private PromptService promptService;

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .baseUrl(openAiBaseUrl)
                .apiKey(openAiApiKey)
                .modelName(embeddingModelName)
                .logRequests(embeddingModelLogRequest)
                .logResponses(embeddingModelLogResponse)
                .build();
    }

    @Bean
    public DashVectorClient dashVectorClient() {
        DashVectorClientConfig config = DashVectorClientConfig.builder()
                .endpoint(dashVectorEndpoint)
                .apiKey(dashVectorApiKey)
                .timeout(dashVectorRequestTimeout)
                .build();
        return new DashVectorClient(config);
    }

    @Bean
    public DashVectorEmbeddingStore dashVectorEmbeddingStore() {
        return new DashVectorEmbeddingStore(
                dashVectorClient(),
                dashVectorCollectionName,
                dashVectorQueryMaxScore,
                dashVectorQueryMaxResults);
    }

    @Bean
    public EmbeddingStoreContentRetriever embeddingStoreContentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(dashVectorEmbeddingStore())
                .embeddingModel(embeddingModel())
                .build();
    }

    @Bean
    public DefaultRetrievalAugmentor defaultRetrievalAugmentor() {
        return DefaultRetrievalAugmentor.builder()
                .queryTextExtractor(new VisionQueryTextExtractor(
                        openAiChatModel(),
                        promptService.findByKey(AiServiceBeanNameEnum.LLM_IMG_RECOGNIZE.getBeanName()),
                        true))
                .queryTransformer(new CompressingQueryTransformer(openAiChatModel()))
                .contentRetriever(embeddingStoreContentRetriever())
                .contentInjector(new DefaultContentInjector())
                .build();
    }

    @Bean("openAiChatModel")
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(openAiBaseUrl)
                .apiKey(openAiApiKey)
                .modelName(chatModelName)
                .logRequests(chatModelLogRequest)
                .logResponses(chatModelLogResponse)
                .temperature(0.5)
                .build();
    }

    @Bean
    public OpenAiStreamingChatModel openAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(openAiBaseUrl)
                .apiKey(openAiApiKey)
                .modelName(chatModelName)
                .logRequests(chatModelLogRequest)
                .logResponses(chatModelLogResponse)
                .temperature(0.5)
                .build();
    }

    @Bean("OpenAiReasoningChatModel")
    public OpenAiChatModel openAiReasoningChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(openAiBaseUrl)
                .apiKey(openAiApiKey)
                .modelName(reasoningModelName)
                .logRequests(chatModelLogRequest)
                .logResponses(chatModelLogResponse)
                .temperature(1.0)
                .build();
    }
}
