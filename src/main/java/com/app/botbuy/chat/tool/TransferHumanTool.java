package com.app.botbuy.chat.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 占位实现：原项目通过 ConversationService 发送 Zendesk 转人工按钮。
 * 接入业务后请替换为真实实现。
 */
@Slf4j
@Component
public class TransferHumanTool {

    @Tool(name = "transferToHumanService", value = {
            "Transfer the user to a live human customer service support. When you can’t answer the user’s question accurately, "
                    + "or the user actively requests live human customer service support, then you should call this method, "
                    + "this method will transfer the user to a live human customer service support."
    })
    public void transferToHumanService(@ToolMemoryId String memoryId) {
        log.info("[stub] transferToHumanService memoryId={}", memoryId);
    }
}
