package jp.mochisuke.lmmchat.embedding;

import io.reactivex.annotations.Nullable;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmbeddingDictionary {
    private final HashMap<EmbeddingAnswer,List<Double>> reversedDictionary = new HashMap<EmbeddingAnswer, List<Double>>();
    private final HashMap<List<Double>,EmbeddingAnswer> dictionary = new HashMap<List<Double>, EmbeddingAnswer>();

    private final HashMap<String, EmbeddingQuestion> questions = new HashMap<String, EmbeddingQuestion>();
    public EmbeddingDictionary(){

    }


    public void add(EmbeddingQuestion question, EmbeddingAnswer word){
        assert !question.isNeedCalcEmbedding();
        reversedDictionary.put(word, question.getVector());
        dictionary.put(question.getVector(), word);
        questions.put(question.getQuestion(),question);
    }
    public void clear(){
        reversedDictionary.clear();
        dictionary.clear();
        questions.clear();
    }
    public EmbeddingAnswer getAnswer(String word){
        EmbeddingQuestion question = questions.get(word);
        if (question == null){
            return null;
        }
        List<Double> vector = question.getVector();
        EmbeddingAnswer answer = dictionary.get(vector);
        if (answer == null){
            return null;
        }
        return answer.clone();
    }

    public @Nullable List<EmbeddingAnswer> searchSimilar(List<Double> vector, double threshold,int count){
        assert(threshold >= 0 && threshold <= 1);
        EmbeddingAnswer maxWord = null;
        //cosine similarity
        ArrayList<Tuple<Double,EmbeddingAnswer>> list = new ArrayList<Tuple<Double, EmbeddingAnswer>>();
        for (var entry : dictionary.entrySet()){
            List<Double> v = entry.getKey();
            double dot = 0;
            double norm1 = 0;
            double norm2 = 0;
            for (int i = 0; i < v.size(); i++){
                dot += v.get(i) * vector.get(i);
                norm1 += v.get(i) * v.get(i);
                norm2 += vector.get(i) * vector.get(i);
            }
            double norm = Math.sqrt(norm1) * Math.sqrt(norm2);
            double cos = dot / norm;
            if (cos >= threshold){
                list.add(new Tuple<Double, EmbeddingAnswer>(cos,entry.getValue()));
            }
        }

        list.sort((a,b) -> b.getA().compareTo(a.getA()));

        return list.stream().map(a -> a.getB()).limit(count).toList();
    }

}
