package jp.mochisuke.lmmchat.sounds;

import com.mojang.logging.LogUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.function.Function;


public class VoicevoxSpeech extends AbstractSpeechGenerator {
    OkHttpClient client;
    String baseUrl;
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public VoicevoxSpeech(String url) {
        client = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(60))
                .connectTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .build();
        baseUrl = url;
    }
    @Override
    public void generateImpl(String hash,String text, int speakerId, Function<byte[],Void> callback){

        // preprocess
        // find @~~~~ mention and remove this
        text=text.replaceAll("@[a-zA-Z0-9_]+", "");
        // find !~~~~ mention and remove this
        text=text.replaceAll("![a-zA-Z0-9_]+", "");
        // replace " to whitespace (because this is not supported)
        text=text.replaceAll("\"", " ");
        // audio_query
        String text2=text;
        Request req = new Request.Builder()
                .url(baseUrl+"audio_query?speaker="+speakerId+"&text=\""+text2+"\"")
                .post(okhttp3.RequestBody.create("", okhttp3.MediaType.parse("application/json")))
                .build();
        // url
        LOGGER.info("requesting "+baseUrl+"audio_query?speaker="+speakerId+"&text="+text2+"");

        var call=client.newCall(req);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                LOGGER.error("failed to request "+baseUrl+"audio_query?speaker="+speakerId+"&text="+text2+"");
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {

                String x= null;
                if (response.body() != null) {
                    x = response.body().string();
                }else{
                    LOGGER.error("failed to request "+baseUrl+"audio_query?speaker="+speakerId+"&text="+text2+"");
                    return;
                }
                Request request = new Request.Builder()
                        .url(baseUrl+"synthesis?speaker="+speakerId)
                        .post(okhttp3.RequestBody.create(x, okhttp3.MediaType.parse("application/json")))
                        .build();
                // url
                LOGGER.info("requesting "+baseUrl+"synthesis?speaker="+speakerId);

                var call2=client.newCall(request);
                call2.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, java.io.IOException e) {
                        LOGGER.error("failed to request "+baseUrl+"synthesis?speaker="+speakerId);
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {

                        if (response.body() != null) {
                            // store to memory
                            byte[] data=response.body().bytes();
                            LOGGER.info("received "+baseUrl+"synthesis?speaker="+speakerId);
                            callback.apply(data);
                        }else{
                            LOGGER.error("failed to request "+baseUrl+"synthesis?speaker="+speakerId);
                            return;
                        }


                    }
                });
            }
        });


    }
}
