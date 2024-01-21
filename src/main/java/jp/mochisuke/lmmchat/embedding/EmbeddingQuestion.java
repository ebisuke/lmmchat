package jp.mochisuke.lmmchat.embedding;

import io.reactivex.annotations.Nullable;

import java.util.List;

public class EmbeddingQuestion {
    private final String question;
    private final List<Double> vector;

    public EmbeddingQuestion(String question, @Nullable List<Double> vector){
        this.question = question;
        this.vector= vector;
    }
    public boolean isNeedCalcEmbedding(){
        return vector == null;
    }

    public String getQuestion(){
        return question;
    }

    public List<Double> getVector(){
        return vector;
    }
}
