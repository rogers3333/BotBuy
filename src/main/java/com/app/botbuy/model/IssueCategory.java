package com.app.botbuy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueCategory {
    private String channelType;
    private String businessType;
    private String consultIssueType;
}
