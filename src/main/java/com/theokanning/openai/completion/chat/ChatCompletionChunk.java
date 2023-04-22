package com.theokanning.openai.completion.chat;
import lombok.Data;

import java.util.List;

/**
 * Object containing a response chunk from the chat completions streaming api.
 */
@Data
public class ChatCompletionChunk {
	/**
     * Unique id assigned to this chat completion.
     */
    String id;
    public String getId() {
        return id;
    }


    /**
     * The type of object returned, should be "chat.completion.chunk"
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
     * The model used.
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
}