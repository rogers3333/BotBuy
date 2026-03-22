package com.app.botbuy.chat.embedding;

import com.app.botbuy.common.Constants;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisChatMemoryStore implements ChatMemoryStore {

    private static final int RETENTION_DAYS = 30;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisChatMemoryStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Object messages = this.redisTemplate.opsForValue().get(Constants.CHAT_MEMORY_PREFIX + memoryId);
        if (messages == null) {
            return List.of();
        }
        return ChatMessageDeserializer.messagesFromJson((String) messages);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        this.redisTemplate.opsForValue().set(
                Constants.CHAT_MEMORY_PREFIX + memoryId,
                ChatMessageSerializer.messagesToJson(messages),
                RETENTION_DAYS,
                TimeUnit.DAYS);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        this.redisTemplate.delete(Constants.CHAT_MEMORY_PREFIX + memoryId);
    }
}
