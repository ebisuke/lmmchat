package jp.mochisuke.lmmchat.embedding;

import java.util.List;

public interface IEmbedderBase {

    List<List<Double>> calculateEmbedding(List<String> text);
}
