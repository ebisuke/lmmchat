package jp.mochisuke.lmmchat.embedding;

import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import jp.mochisuke.lmmchat.LMMChatConfig;

import java.util.List;

public class OpenAIEmbedder implements IEmbedderBase {
    OpenAiService service;
    public OpenAIEmbedder(){
        service = new OpenAiService(LMMChatConfig.getApiKey());
    }
    @Override
    public List<List<Double>> calculateEmbedding(List<String> text) {
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(text)
                .build();
        var result= service.createEmbeddings(request);
        if(result.getData().size() == 0){
            return null;
        }
        var r= result.getData().stream().map(x->x.getEmbedding()).toList();
        return r;

    }
}
