package com.app.botbuy.common;

import com.app.botbuy.model.IssueCategory;
import org.springframework.util.StringUtils;

public final class CommonUtils {

    private CommonUtils() {
    }

    public static String wrapChatMemoryId(String conversationId, IssueCategory issueCategory) {
        return String.format("%s:%s", conversationId, wrapIssueCategoryString(issueCategory));
    }

    public static String getIssueCategoryStringFromChatMemoryId(String chatMemoryId) {
        if (!StringUtils.hasText(chatMemoryId)) {
            return "";
        }
        int firstIndex = chatMemoryId.indexOf(':');
        if (firstIndex == -1 || firstIndex == chatMemoryId.length() - 1) {
            return "";
        }
        return chatMemoryId.substring(firstIndex + 1);
    }

    public static IssueCategory getIssueCategoryFromChatMemoryId(String chatMemoryId) {
        String issueCategoryString = getIssueCategoryStringFromChatMemoryId(chatMemoryId);
        return convertIssueCategory(issueCategoryString);
    }

    public static String wrapIssueCategoryString(IssueCategory issueCategory) {
        return String.format("%s:%s:%s",
                issueCategory.getChannelType(),
                issueCategory.getBusinessType(),
                issueCategory.getConsultIssueType());
    }

    public static IssueCategory convertIssueCategory(String issueCategoryString) {
        if (!StringUtils.hasText(issueCategoryString)) {
            return null;
        }
        String[] split = issueCategoryString.split(":");
        if (split.length < 3) {
            throw new IllegalArgumentException("无效的 issueCategoryString: " + issueCategoryString);
        }
        return new IssueCategory(split[0], split[1], split[2]);
    }
}
