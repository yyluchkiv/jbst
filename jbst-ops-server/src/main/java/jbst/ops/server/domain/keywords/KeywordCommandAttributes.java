package jbst.ops.server.domain.keywords;

import lombok.Data;

import static java.util.Objects.nonNull;

@Data
public class KeywordCommandAttributes {
    // core
    private boolean enabledDefault;
    // logs-service
    private Integer serverId;
    // chatgpt-service
    private String chatgptPrompt;

    public static KeywordCommandAttributes enabledDefault() {
        var instance = new KeywordCommandAttributes();
        instance.enabledDefault = true;
        return instance;
    }

    public static KeywordCommandAttributes server(Integer serverId) {
        var instance = new KeywordCommandAttributes();
        instance.serverId = serverId;
        return instance;
    }

    public boolean isEnabled() {
        return this.enabledDefault ||
                (nonNull(this.serverId) || nonNull(this.chatgptPrompt));
    }

    public boolean isChatGPTPromptValid() {
        return (this.chatgptPrompt.startsWith("\"") || this.chatgptPrompt.startsWith("“")) &&
                (this.chatgptPrompt.endsWith("\"") || this.chatgptPrompt.endsWith("”"));
    }

    public String getCleanChatGPTPrompt() {
        return this.chatgptPrompt.substring(1, this.chatgptPrompt.length() - 1);
    }
}
