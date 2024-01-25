package jp.mochisuke.lmmchat.sounds;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@OnlyIn(Dist.CLIENT)
public class SoundPlayer {
    AbstractSpeechGenerator generator;
    private static final Logger LOGGER = LogUtils.getLogger();
    HashMap<Integer, Date> entityIdToLastPlayed = new HashMap<>();
    float cooldownBase=0.4f;
    Queue<SoundInstance> queue;
    boolean isPlaying=false;
    public SoundPlayer(AbstractSpeechGenerator generator) {
        this.generator = generator;
        queue=new ConcurrentLinkedDeque<>();
        cleanupSpeechDir();
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
            if (file.isFile()) {
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
    public void generateAndPlay(int senderid, Entity sourceEntity, String text, int speakerId,boolean isContinue) {
        //find 、。！？\n
        String[] find = {"、", "。", "！", "？", "\n"};
        int firstIndex=Integer.MAX_VALUE;
        for (String s : find) {
            int index = text.indexOf(s);
            if (index != -1) {
                firstIndex = Math.min(firstIndex, index);
            }
        }

        if(firstIndex==0){
            LOGGER.debug("sentences[0] length is 0");
            generateAndPlay(senderid, sourceEntity, text.substring(1), speakerId,isContinue);
            return;
        }
        String firstSentence = text;
        String nextSentence = "";
        if(firstIndex!=Integer.MAX_VALUE){
            firstSentence = text.substring(0, firstIndex);
            nextSentence = text.substring(firstIndex + 1);
        }

        if (sourceEntity==null || !sourceEntity.isAlive() || sourceEntity.isSpectator()) {
            LOGGER.debug("source entity is null or not alive");
            return;
        }
        if(Minecraft.getInstance().isPaused()||Minecraft.getInstance().level==null){
            return;
        }
        if(!isContinue && isPlaying){
            LOGGER.debug("is playing");
            return;
        }
//        if (entityIdToLastPlayed.containsKey(senderid) && !isContinue) {
//            Date lastPlayed = entityIdToLastPlayed.get(senderid);
//            Date now = new Date();
//            long diff = now.getTime() - lastPlayed.getTime();
//
//            long cooldowntime=(long) (cooldownBase*text.length() * 1000);
//            cooldowntime=Math.min(cooldowntime,30000);
//            if (diff <cooldowntime) {
//                return;
//            }
//        }
        entityIdToLastPlayed.put(senderid, new Date());
        //generate first sentence
        String finalNextSentence = nextSentence;
        String finalNextSentence1 = nextSentence;
        generator.generate(firstSentence, speakerId, (x) -> {

            //parse ogg header
            try {
                InputStream oggFile = null;
                try {
                    oggFile = new FileInputStream(x.filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                double length = 0;
                try {
                    length = calculateDuration(oggFile, x.raw_data.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!sourceEntity.isAlive() || sourceEntity.isSpectator()) {
                    return null;
                }
                //shutdown?
                if(Minecraft.getInstance().isPaused()||Minecraft.getInstance().level==null){
                    return null;
                }
                if(!isContinue && isPlaying){
                    LOGGER.debug("is playing");
                    return null;
                }
                isPlaying = true;
                play(senderid, sourceEntity.blockPosition(), x.text, x.filename);
                //wait
                try {
                    LOGGER.debug("waiting " + length + "ms");
                    Thread.sleep((long) (length)-50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (finalNextSentence.length() >0) {

                    String remain = finalNextSentence1;
                    LOGGER.debug("generating next sentence:" + remain);
                    //invoke another thread
                    Thread thread = new Thread(() -> {
                        generateAndPlay(senderid, sourceEntity, remain, speakerId, true);
                    });
                    thread.start();
                } else {
                    isPlaying = false;
                }
            }catch (Exception e){
                e.printStackTrace();
                isPlaying = false;
            }
            return null;
        });
    }
    public void onTick(){
        //has?

        if(!queue.isEmpty()){
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            soundManager.play(Objects.requireNonNull(queue.poll()));
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
                new SpeechSampledFloat(0.9f),
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
            queue.add(soundInstance);
            LOGGER.debug("queued sound");

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }



    }
}
