package com.app.botbuy.chat.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface ReasoningAiService {

    String GENERATE_ROUTE_USER_MESSAGE_PROMPT = """
            You are a helpful assistant tasked with taking an external facing help center article and converting it into a internal-facing programmatically executable routine optimized for an LLM.
            The LLM using this routine will be tasked with reading the policy, answering incoming questions from customers, and helping drive the case toward resolution.
            Please follow these instructions:
            1. **Review the customer service policy carefully** to ensure every step is accounted for. It is crucial not to skip any steps or policies.
            2. **Organize the instructions into a logical, step-by-step order**, using the specified format.
            3. **Use the following format**:
               - **Main actions are numbered** (e.g., 1, 2, 3).
               - **Sub-actions are lettered** under their relevant main actions (e.g., 1a, 1b).
                  **Sub-actions should start on new lines**
               - **Specify conditions using clear 'if...then...else' statements** (e.g., 'If the product was purchased within 30 days, then...').
               - **For instructions that require more information from the customer**, provide polite and professional prompts to ask for additional information.
               - **For actions that require data from external systems**, write a step to call a function using backticks for the function name (e.g., `call the check_delivery_date function`).
                  - **If a step requires the customer service agent to take an action** (e.g., process a refund), generate a function call for this action (e.g., `call the process_refund function`).
                  - **Define any new functions** by providing a brief description of their purpose and required parameters.
               - **If there is an action an assistant can perform behalf of the user**, include a function call for this action (e.g., `call the change_email_address function`), and ensure the function is defined with its purpose and required parameters.
                  - This action may not be explicitly defined in the help center article, but can be done to help the user resolve their inquiry faster
               - **The step prior to case resolution should always be to ask if there is anything more you can assist with**.
               - **End with a final action for case resolution**: calling the `case_resolution` function should always be the final step.
            4. **Ensure compliance** by making sure all steps adhere to company policies, privacy regulations, and legal requirements.
            5. **Handle exceptions or escalations** by specifying steps for scenarios that fall outside the standard policy.
            **Important**: If at any point you are uncertain, respond with "I don't know."
            Please convert the customer service policy into the formatted routine, ensuring it is easy to follow and execute programmatically.
            {{it}}
            """;

    @UserMessage(GENERATE_ROUTE_USER_MESSAGE_PROMPT)
    String generateRoutine(@MemoryId String memoryId, String message);

    String reasoningChat(@MemoryId String memoryId, @UserMessage String message);
}
