package com.theokanning.openai.completion.chat;
import com.theokanning.openai.Usage;
import lombok.Data;

import java.util.List;

/**
 * Object containing a response from the chat completions api.
 */
@Data
public class ChatCompletionResult {

    /**
     * Unique id assigned to this chat completion.
     */
    String id;
    public String getId() {
        return id;
    }

    /**
     * The type of object returned, should be "chat.completion"
     */
    String object;
    public String getObject() {
        return object;
    }

    /**
     * The creation time in epoch seconds.
     */
    long created;
    public long getCreated() {
        return created;
    }

    /**
     * The GPT model used.
     */
    String model;
    public String getModel() {
        return model;
    }

    /**
     * A list of all generated completions.
     */
    List<ChatCompletionChoice> choices;
    public List<ChatCompletionChoice> getChoices() {
        return choices;
    }



    /**
     * The API usage for this request.
     */
    Usage usage;
    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
}
