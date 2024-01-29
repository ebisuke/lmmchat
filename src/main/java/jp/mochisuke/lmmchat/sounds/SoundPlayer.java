package jp.mochisuke.lmmchat.sounds;

import com.mojang.logging.LogUtils;
import io.reactivex.disposables.Disposable;
import jp.mochisuke.lmmchat.LMMChatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

@OnlyIn(Dist.CLIENT)

public class SoundPlayer implements Disposable {
    AbstractSpeechGenerator generator;
    private static final Logger LOGGER = LogUtils.getLogger();
    HashMap<Integer, Date> entityIdToLastPlayed = new HashMap<>();
    Thread generatorThread;
    Queue<SoundInstance> soundPlayQueue;
    Queue<Tuple<String,Integer>> generatorQueue;
    boolean isPlaying=false;
    boolean isDisposed=false;
    public SoundPlayer(AbstractSpeechGenerator generator) {
        this.generator = generator;
        soundPlayQueue =new ConcurrentLinkedDeque<>();
        generatorQueue=new ConcurrentLinkedDeque<>();
        cleanupSpeechDir();
        generatorThread=new Thread(this::generatorProcess);
        generatorThread.start();
    }
    private void generatorProcess(){
        while(true){
            if(!generatorQueue.isEmpty()){
                Tuple<String,Integer> data=generatorQueue.poll();
                LOGGER.debug("generating:"+data.getA());
                generator.generate(data.getA(), data.getB(), (x) ->{
                        return null;
                        });
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void cleanupSpeechDir(){
        java.io.File dir = new java.io.File("speech");
        if (!dir.exists()) {
            dir.mkdir();
        }
        java.io.File[] files = dir.listFiles();
        if(files==null){
            return;
        }
        for (java.io.File file : files) {
            //remove *.ogg only
            if (file.isFile() && (
                    file.getName().toLowerCase().endsWith(".ogg")||
                    file.getName().toLowerCase().endsWith(".wav"))||
                    file.getName().toLowerCase().endsWith(".locking")) {
                file.delete();
            }
        }
    }
    double calculateDuration(final InputStream oggFile,int size) throws IOException {
        //https://stackoverflow.com/questions/20794204/how-to-determine-length-of-ogg-file
        int rate = -1;
        int length = -1;

        byte[] t = new byte[size];

        var stream = oggFile;
        stream.read(t);

        for (int i = size-1-8-2-4; i>=0 && length<0; i--) { //4 bytes for "OggS", 2 unused bytes, 8 bytes for length
            // Looking for length (value after last "OggS")
            if (
                    t[i]==(byte)'O'
                            && t[i+1]==(byte)'g'
                            && t[i+2]==(byte)'g'
                            && t[i+3]==(byte)'S'
            ) {
                byte[] byteArray = new byte[]{t[i+6],t[i+7],t[i+8],t[i+9],t[i+10],t[i+11],t[i+12],t[i+13]};
                ByteBuffer bb = ByteBuffer.wrap(byteArray);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                length = bb.getInt(0);
            }
        }
        for (int i = 0; i<size-8-2-4 && rate<0; i++) {
            // Looking for rate (first value after "vorbis")
            if (
                    t[i]==(byte)'v'
                            && t[i+1]==(byte)'o'
                            && t[i+2]==(byte)'r'
                            && t[i+3]==(byte)'b'
                            && t[i+4]==(byte)'i'
                            && t[i+5]==(byte)'s'
            ) {
                byte[] byteArray = new byte[]{t[i+11],t[i+12],t[i+13],t[i+14]};
                ByteBuffer bb = ByteBuffer.wrap(byteArray);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                rate = bb.getInt(0);
            }

        }

        double duration = (double) (length*1000) / (double) rate;
        return duration;
    }
    public void generateAndPlay(int senderid,int speakerId, String text) {
        if(Minecraft.getInstance().isPaused()||Minecraft.getInstance().level==null){
            return;
        }

        if(isPlaying){
            LOGGER.debug("is playing");
            return;
        }

        String splittingChars= LMMChatConfig.getVoicevoxSentenceSplitter();


        ArrayList<String> allSentences=new ArrayList<>();
        String remain=text;
        while(true) {
            int minIndex = Integer.MAX_VALUE;
            for (int i = 0; i < splittingChars.length(); i++) {
                String s = String.valueOf(splittingChars.charAt(i));
                int index = remain.indexOf(s);
                if (index != -1) {
                    minIndex = Math.min(minIndex, index);
                }
            }
            if(minIndex==Integer.MAX_VALUE){
                allSentences.add(remain);
                break;
            }else{
                allSentences.add(remain.substring(0,minIndex+1));
                remain=remain.substring(minIndex+1);
            }
        }
        //push generator queue
        for (String sentence : allSentences) {
            generatorQueue.add(new Tuple<>(sentence,speakerId));
        }

        entityIdToLastPlayed.put(senderid, new Date());

        Thread th=new Thread(()->{
            attemptToPlay(senderid,text,speakerId);
        });
        th.start();


    }
    public void attemptToPlay(int senderId,String text,int speakerId){
        isPlaying = true;
        try{
            String firstSentence = text;
            String nextSentence = "";
            //find 、。！？\n
            String splittingChars= LMMChatConfig.getVoicevoxSentenceSplitter();
            //to array
            ArrayList<String> find=new ArrayList<>();
            for (int i = 0; i < splittingChars.length(); i++) {
                find.add(String.valueOf(splittingChars.charAt(i)));
            }

            int firstIndex=Integer.MAX_VALUE;
            for (String s : find) {
                int index = text.indexOf(s);
                if (index != -1) {
                    firstIndex = Math.min(firstIndex, index);
                }
            }

            if(firstIndex!=Integer.MAX_VALUE){
                firstSentence = text.substring(0, firstIndex+1);
                nextSentence = text.substring(firstIndex + 1);
            }

            if(firstIndex==0){
                LOGGER.debug("sentences[0] length is 0");
                attemptToPlay(senderId, text.substring(1),speakerId);
                return;
            }

            InputStream oggFile = null;
            TTSResult result=null;
            int limit=100;
            do{
                result=generator.getIfAlreadyCreated(firstSentence, speakerId);
                if (result==null) {
                    if(Minecraft.getInstance().isPaused()||Minecraft.getInstance().level==null){
                        isPlaying=false;
                        return;
                    }
                    LOGGER.debug("not found in cache");
                    if(generatorQueue.isEmpty()) {
                        //一応キューに入れておく
                        //generatorQueue.add(new Tuple<>(firstSentence, speakerId));
                    }
                    Thread.sleep(100);
                    limit--;
                }
                if (limit==0) {
                    LOGGER.warn("wait for sound generation limit exceeded");
                    isPlaying = false;
                    return;
                }
            }while(result==null);

            try {
                oggFile = new FileInputStream(result.filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            double length = 0;
            try {
                length = calculateDuration(oggFile, result.raw_data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Entity sourceEntity;
            sourceEntity = Minecraft.getInstance().level.getEntity(senderId);

            if (sourceEntity==null || !sourceEntity.isAlive() || sourceEntity.isSpectator()) {
                isPlaying = false;
                return;
            }

            play(senderId, sourceEntity.blockPosition(), result.text, result.filename);
            //wait

            LOGGER.debug("waiting " + length + "ms");
            Thread.sleep((long) (length)+LMMChatConfig.getVoiceVoxVoiceAdditionalDuration());

            if (nextSentence.length() >0) {

                String remain = nextSentence;
                LOGGER.debug("generating next sentence:" + remain);
                //invoke another thread
                Thread thread = new Thread(() -> {
                    attemptToPlay(senderId,  remain, speakerId);
                });
                thread.start();
            } else {
                isPlaying = false;
            }
        }catch (Exception e){
            LOGGER.error("failed to play sound:"+e.toString());
            isPlaying = false;
        }
    }
    public void onTick(){
        //has?
        if(!soundPlayQueue.isEmpty()){
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            soundManager.play(Objects.requireNonNull(soundPlayQueue.poll()));
            LOGGER.debug("played sound");
        }
    }
    public void play(int senderid, BlockPos pos, String text, String filename) {
        //save as wav
        //mkdir 'speech'
        java.io.File dir = new java.io.File("speech");
        if (!dir.exists()) {
            dir.mkdir();
        }

        //play
        Sound sound = new Sound("test.ogg",
                new SpeechSampledFloat(1.0f),
                new SpeechSampledFloat(1.0f),
                1,
                Sound.Type.FILE,
                true,
                false,
                24);
        //get location field
        try {
            Field f= sound.getClass().getDeclaredFields()[1];   // location
            f.setAccessible(true);
            ModifiedResourceLocation fileloc = new ModifiedResourceLocation(filename);
            f.set(sound,fileloc);

            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            SoundInstance soundInstance = new SpeechSoundInstance(sound, text, fileloc, 1.0f, 1.0f, pos.getX(), pos.getY(), pos.getZ(), SoundInstance.Attenuation.LINEAR);
            soundPlayQueue.add(soundInstance);
            LOGGER.debug("queued sound");

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }



    }

    @Override
    public void dispose() {
        generatorThread.interrupt();
        isDisposed=true;
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
