package jp.mochisuke.lmmchat.sounds;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChatConfig;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.Function;
@OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
public abstract class AbstractSpeechGenerator {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String BASE_DIR="speech/";
    public void generate(String text, int speakerId, Function<TTSResult,Void> callback){
        MessageDigest md= null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update((speakerId+"_"+text).getBytes());
        var hash= md.digest();
        // use 48 chars
        var hashstr= Base64.getEncoder().encodeToString(hash);
        String modified_filename=hashstr.replaceAll("[^a-zA-Z0-9\\-]", "_");
        String path=BASE_DIR+modified_filename+".ogg";
        //already generated?
        if (java.nio.file.Files.exists(java.nio.file.Paths.get(path))) {
            //already generated
            //return ogg
            try {
                var data=java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
                callback.apply(new TTSResult(text,path,data));
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        Function<byte[],Void> wrappedCallback=(x)->{
            if(x!=null){

                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get(BASE_DIR+modified_filename+".wav"),x);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //create empty .locking file
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get(BASE_DIR+modified_filename+".locking"),"locked".getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {

                    LOGGER.debug("ffmpeg path: "+ LMMChatConfig.getFFMPEGPath());
                    ProcessBuilder pb = new ProcessBuilder(LMMChatConfig.getFFMPEGPath(), "-i", BASE_DIR+modified_filename+".wav", "-acodec", "libvorbis", BASE_DIR+modified_filename+".ogg");
                    Process p = pb.start();
                    p.waitFor();
                    //remove wav
                    java.nio.file.Files.delete(java.nio.file.Paths.get(BASE_DIR+modified_filename+".wav"));
                    LOGGER.debug("ffmpeg finished");
                    try {
                        var data=java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
                        callback.apply(new TTSResult(text,path,data));
                        return null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally{
                    //remove .locking file
                    try {
                        java.nio.file.Files.delete(java.nio.file.Paths.get(BASE_DIR+modified_filename+".locking"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            callback.apply(null);
            return null;
        };
        generateImpl(hashstr,text,speakerId,wrappedCallback);

    }
    public TTSResult getIfAlreadyCreated(String text,int speakerId){
        MessageDigest md= null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update((speakerId+"_"+text).getBytes());
        var hash= md.digest();
        // use 48 chars
        var hashstr= Base64.getEncoder().encodeToString(hash);
        String modified_filename=hashstr.replaceAll("[^a-zA-Z0-9\\-]", "_");
        String path=BASE_DIR+modified_filename+".ogg";
        //already generated?
        if (java.nio.file.Files.exists(java.nio.file.Paths.get(path))&& !java.nio.file.Files.exists(java.nio.file.Paths.get(BASE_DIR+modified_filename+".locking"))) {
            //already generated
            //return ogg
            try {
                var data=java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
                return new TTSResult(text,path,data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return null;
    }
    abstract void generateImpl(String hash,String text, int speakerId, Function<byte[],Void> callback);
}
