package com.app.botbuy.chat.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 占位实现：原项目通过 ConversationService 发送关单/确认按钮。
 */
@Slf4j
@Component
public class ConversationEndTool {

    @Tool(name = "askUserIfProblemSolved", value = {
            "This function will ask the user whether the problem is solved. When you need to ask user whether the user's problem has been solved, you should call this function."
    })
    public String askUserIfProblemSolved(@ToolMemoryId String memoryId) {
        log.info("[stub] askUserIfProblemSolved memoryId={}", memoryId);
        return "询问用户问题是否解决（占位）";
    }

    @Tool(name = "updateTicketStatusToSolved", value = {
            "This function marks the ticket status as resolved in the background. Only when the user replies that his problem has been resolved and there are no other problems, "
                    + "you can call this method to mark the problem solved for the user in the background, the marking behavior does not need to be notified to the user."
    })
    public String updateTicketStatusToSolved(@ToolMemoryId String memoryId) {
        log.info("[stub] updateTicketStatusToSolved memoryId={}", memoryId);
        return "用户问题已解决（占位）";
    }
}
