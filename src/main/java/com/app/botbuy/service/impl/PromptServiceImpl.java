package com.app.botbuy.service.impl;

import com.app.botbuy.common.AiServiceBeanNameEnum;
import com.app.botbuy.config.BotBuyPromptProperties;
import com.app.botbuy.chat.service.RagAiService;
import com.app.botbuy.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {

    private final BotBuyPromptProperties properties;

    @Override
    public String findByKey(String key) {
        if (AiServiceBeanNameEnum.LLM_IMG_RECOGNIZE.getBeanName().equals(key)) {
            return properties.getLlmImgRecognize();
        }
        if (AiServiceBeanNameEnum.RAG_STREAM_AI_SERVICE.getBeanName().equals(key)) {
            return textOrDefault(properties.getRagStreamAiService(), RagAiService.AI_SERVICE_SYSTEM_MESSAGE);
        }
        if (AiServiceBeanNameEnum.RAG_AI_SERVICE.getBeanName().equals(key)) {
            return textOrDefault(properties.getRagAiService(), RagAiService.AI_SERVICE_SYSTEM_MESSAGE);
        }
        return "";
    }

    private static String textOrDefault(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }
}
