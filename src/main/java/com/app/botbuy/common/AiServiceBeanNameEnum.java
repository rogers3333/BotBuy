package com.app.botbuy.common;

public enum AiServiceBeanNameEnum {

    RAG_STREAM_AI_SERVICE("ragStreamAiService"),
    RAG_AI_SERVICE("ragAiService"),
    LLM_IMG_RECOGNIZE("llmImgRecognize");

    private final String beanName;

    AiServiceBeanNameEnum(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
