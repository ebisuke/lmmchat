
package jp.mochisuke.lmmchat.chat.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpenAIJsonResponse {

    @SerializedName("choices")
    @Expose
    private List<Choice> choices;
    @SerializedName("usage")
    @Expose
    private Usage usage;
    @SerializedName("id")
    @Expose
    private Integer id;

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
