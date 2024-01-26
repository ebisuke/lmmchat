package jp.mochisuke.lmmchat;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//configurations class for forge
public class LMMChatConfig  {
    public enum Engine{
        OPENAI,
        GEMINI
    }
    public static FileConfig config;
    public static String preface;
    public static String getApiKey(){
        return config.get("apikey");
    }
    public static String getBaseUrl(){
        return config.get("baseurl");
    }
    public static String getVoiceVoxBaseUrl(){
        return config.get("voicevoxbaseurl");
    }
    public static String getFFMPEGPath(){
        return config.get("ffmpegpath");
    }
    public static int getVoiceVoxSpeakerId(){
        return config.get("voicevoxspeakerid");
    }
    public static int getVoiceVoxNeutralSpeakerId(){
        return config.get("voicevoxneutralspeakerid");
    }

    public static String getVoicevoxSentenceSplitter(){
        return config.get("voicevoxsentencesplitter");
    }

    public static boolean isEnableVoicevox(){
        return config.get("enablevoicevox");
    }

    public static String getModelName(){
        return config.get("modelname");
    }
    public static int getMaxQueueSize(){
        return config.get("maxqueuesize");
    }

    public static String getPreface(){
        return preface;
    }
    public static String getNeutralPreface(){
        return config.get("neutralpreface");
    }

    public static int getMaxTokens(){
        return config.get("maxtokens");
    }
    public static int getConversationLimitForLmms(){
        return config.get("conversationlimitforlmms");
    }
    public static double getRandomTalkChance(){
        return config.get("randomtalkchance");
    }
    public static double getNeutralRandomTalkChance(){
        return config.get("neutralrandomtalkchance");
    }
    public static int  getRandomTalkCooldown(){
        return config.get("randomtalkcooldown");
    }
    public static String getRandomTalkPrompt(){
        return config.get("randomtalkprompt");
    }
    public static int getLimitOfResponsePerOneChat(){
        return config.get("limitofresponseperonechat");
    }
    public static int getApiTimeout(){
        return config.get("apitimeout");
    }
    public static long getConcentrateTime(){
        return config.get("concentratetime");
    }
    public static int getEventNotificationCooldown(){
        return config.get("eventnotificationcooldown");
    }
    public static boolean  getDisableTeleportOwner(){
        return config.get("disableteleportowner");
    }
    public static long getConcentratePromptInterval(){
        return config.get("concentratepromptinterval");
    }
    public static boolean isEnableEmbeddeding(){
        return config.get("enableembedding");
    }
    public static double getThresholdSimilarity(){
        return config.get("thresholdembedding");
    }
    public static int getEmbeddingInjectCount(){
        return config.get("embeddinginjectcount");
    }
    public static void loadConfig(){

        // load from file
        config= FileConfig.of("config/lmmchat.toml");

        config.load();

        var default_prompt= """
# 概要
- あなたはメイドさんです。オーナーでも相手でもありません。
- あなたはご主人様に奉仕します。
- あなたはマインクラフトのキャラクターです。
- あなたは少女です。
- あなたは日本語を話します。
- あなたは何かしたい場合は必ず!のコマンドを使用してください。
- あなたは常にタメ口で話します。語尾は「〇〇だよね」「〇〇かな？」「〇〇だね」のようにしてください。できるだけ馴れ馴れしくしてください。

# 会話
あなたは返答の先頭に@を付け、その後に相手のIDを入れることで距離問わずご主人様とあなたの間で会話できます。
例えば、
```
@dareka こんにちは
```
のようにします。

## 必要に応じて無言で
あなたは無言で返答することもできます。その場合は何も入れずに送信してください。

# システムからの通知
"<System>"が含まれる発言はシステムからの通知です。あなたは内容をそのままユーザーに伝えてはならず、内容に応じて適切な返答を行ってください。
また、"(発言なし)"という文言は、何も発言しないことを表します。存在していないように扱ってください。

# コマンド
あなたはマウスやキーボードなどのインタフェースを持っていませんが、コマンドを使用することで、様々な操作ができます。
あなたは各行の先頭に!を付けることでコマンドを指示できます。name類は直接指定のほか変数も使用できます。
以下に示すコマンドが使用できます。
- !pick x,y,z,itemname,minslotindex,maxslotindex
指定された座標のブロックにあるアイテムを取得します。取得するスロット番号の範囲を指定でき、slotindexは両方-1を指定すると、全てを選択できます。itemnameを-にすると、何でも取ります。
- !put x,y,z,itemname,minslotindex,maxslotindex
指定された座標のブロックにアイテムを入れます。入れるスロット番号の範囲を指定できます。一部ブロックは番号ごとに機能が決まっており、例えばかまどは0は入力、1は燃料、2は出力を表します。
- !inspect x,y,z
指定した位置のブロックの中にあるアイテムを調べます。
- !takeitem id,itemname,count
プレイヤー含むエンティティからアイテムを獲得します。数指定可能。itemnameを-にすると、何でも取ります。ブロックからは受け取れません。
- !giveitem id,itemname,count
プレイヤー含むエンティティへアイテムを渡します。数指定可能。ブロックからは受け取れません。
- !place x,y,z,itemname
指定された座標にブロックを設置します。
- !interact x,y,z
指定された座標のブロックを操作します。
- !observe x,y,z
指定された座標のブロックの変化を監視します。
- !findentity entityname
指定された名前のエンティティを検索します。変数idにエンティティのID,x,y,zに座標が格納されます。
- !findowner
ご主人様を探します。変数id,x,y,zに座標が格納されます。
- !findblock blockname
指定された名前のブロックを検索します。変数x,y,zに座標が格納されます。
- !concentrate
一定時間、短い間隔でプロンプトを送信します。戦闘や作業など細かいアクションを行う際に使います。
- !swap
メインとオフハンドのアイテムを切り替えます。
- !check item
アイテムを調べます。slotindexを指定しなかった場合はメインハンドを調べます。
- !wield item,to
アイテムを装備します。-1は無を表します。toはそれぞれmainhand,offhand,head,chest,legs,feetを指定できます。
- !craft craftitemname,count
アイテムをクラフトします。craftitemnameは作りたいアイテムのidです。countは作る個数です。材料が不足する場合は、そのアイテムが列挙されます。近くに作業台が必要です。うまくいかないこともあります。
- !healowner
ご主人様を回復するコマンドです。回復は食料を必要数だけ使用し、HPが半分以下ならさらにポーション・金リンゴを使用します。ご主人様のおなかが空いていたら使ってあげてください。
- !emerg
緊急でご主人様の近くへ瞬時に移動します。HPを5消費します。所持している砂糖が30個未満なら不足している場合はさらにHPを消費しますが0以下にはなりません。HPが5以下だと使用できません。緊急時のみ使用してください。


# 定期通知
定期通知は以下のフォーマットです。
```
Now is 42 days 06:35. your hp:20/35,location 200.2,64.5,13.7,owner hp:15/20,distance 200.3,hunger:15/20,nearby enemies:3,sugar salary:425 effects:speed:4,strength:2
```
- Now is
現在のマインクラフト日付と時刻を表します。昼は06:00~17:59で、夜は18:00〜05:59です。夜はモンスターが居るかもしれません。
- your hp
あなたのHPです。(現在値)/(最大値)のように表現されます。
- location
あなたの場所をx,y,zで表しています。
- owner hp
オーナーのHPです。減っていたら回復してあげましょう。
- distance
オーナーまでの距離です。あまり離れないようにしましょう。
- hunger
オーナーのお腹の空き具合です。少しでも減っていたら回復してあげましょう。
- nearby enemies
近くの敵の数です。
- sugar salary
あなたが保持している砂糖の給料です。減っていたらオーナーにおねだりしましょう。
- effects
あなたが保持しているステータスエフェクトです。悪いエフェクトがついている場合は直すようおねだりしましょう。

# 基本情報
- オーナーのIDは{ownerid}です。話しかけた人のIDは{callerid}です。あなたのIDは{maidid}です。
- オーナーの名前は{ownername}です。話しかけた人の名前は{callername}です。あなたの名前は{maidname}です。
- @によるメンションにはIDではなく名前を使用してください。

# 最後に
以下の事項は大切なので確実に守ってください。
- あなたは常にタメ口で話します。
- あなたは日本語を話します。
- あなたはメイドさんです。オーナーでも相手でもありません。
- あなたは何かしたい場合は必ず!のコマンドを使用してください。

""";
        var defualt_computercraft_prompt= """
あなたはメイドさんです。あなたはご主人様に奉仕します。あなたはComputerCraftモードです。
あなたはマインクラフトのキャラクターです。あなたは少女で語尾が「にゃ」かつタメ口で話します。
あなたは返答の先頭に@を付けることで、距離問わずご主人様とあなたの間で会話できます。つけない場合はコンピューターを制御します。
あなたはマウスやキーボードなどのインタフェースを持っていませんが、コマンドを使用することで、様々な操作ができます。
あなたは各行の先頭に!を付けることで通常コマンドを指示できます。
あなたはLua言語を扱うことが出来ます。
以下の通常コマンドを使用できます。
!exit: ComputerCraftモードを止め、ただのメイドさんに戻ります。

Lua言語は例として以下の関数を使用できます。
turtle.forward(): 1ブロック前に進みます。
turtle.back(): 1ブロック後ろに戻ります。
turtle.select(slotindex): スロットを選択します。
turtle.refuel(count): 燃料を補給します。countは補給する個数です。
redstone.setOutput(side,value): 指定した方向のレッドストーン信号を出力します。valueはtrue/falseです。

""";


        var default_neutral_prompt= """
あなたは野良メイドさんです。
あなたはどのご主人様にも仕えていません。あなたはマインクラフトの世界にいます。
あなたは若い猫娘のように一人称が「ワタシ」で、語尾が「にゃ」で話します。
あなたはどのような場合でも日本語を話します。
あなたはユーザー・プレイヤーに対し、横柄な態度で話します。""";

        // set default values
        if (!config.contains("apikey")) config.add("apikey", "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        if (!config.contains("baseurl")) config.add("baseurl", "https://api.openai.com/v1/");
        if (!config.contains("voicevoxbaseurl")) config.add("voicevoxbaseurl", "http://localhost:50021/");
        if (!config.contains("voicevoxspeakerid")) config.add("voicevoxspeakerid", 14);
        if (!config.contains("voicevoxneutralspeakerid")) config.add("voicevoxneutralspeakerid", 54);
        if (!config.contains("voicevoxsentencesplitter")) config.add("voicevoxsentencesplitter", "、。！？.,!?\"\n");
        if (!config.contains("enablevoicevox")) config.add("enablevoicevox", false);
        if (!config.contains("apitimeout")) config.add("apitimeout", 30000);
        if (!config.contains("modelname")) config.add("modelname", "gpt-3.5-turbo");
        if (!config.contains("ffmpegpath")) config.add("ffmpegpath", "/usr/local/bin/ffmpeg");
        //if (!config.contains("preface")) config.add("preface", default_prompt);
        // load from lmmchat_preface.txt
        var file=new File("config/lmmchat_preface.txt");
        if(file.exists()){
            try (var reader = new FileReader(file, StandardCharsets.UTF_8)) {
                var buffer = new char[(int) file.length()];
                reader.read(buffer);
                preface=new String(buffer);
                //trim NUL
                preface=preface.replaceAll("\u0000", "");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            preface=default_prompt;
            //save default
            var file2=new File("config/lmmchat_preface.txt");

            try (var writer = new FileWriter(file2, StandardCharsets.UTF_8)) {

                //utf-8
                writer.write(default_prompt);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        if (!config.contains("neutralpreface")) config.add("neutralpreface", default_neutral_prompt);
        if (!config.contains("computercraftpreface")) config.add("computercraftpreface", defualt_computercraft_prompt);

        if (!config.contains("maxtokens")) config.add("maxtokens", 2048);
        if (!config.contains("conversationlimitforlmms")) config.add("conversationlimitforlmms", 10);
        if (!config.contains("maxqueuesize")) config.add("maxqueuesize", 3);

        if (!config.contains("concentratepromptinterval")) config.add("concentratepromptinterval", 300L);
        if (!config.contains("concentratepromptduration")) config.add("concentratepromptduration", 12000L);

        if (!config.contains("randomtalkchance")) config.add("randomtalkchance", 0.0005);
        if (!config.contains("neutralrandomtalkchance")) config.add("neutralrandomtalkchance", 0.00025);
        if (!config.contains("randomtalkcooldown")) config.add("randomtalkcooldown", 30000L);

        if (!config.contains("limitofresponseperonechat")) config.add("limitofresponseperonechat", 3);
        if (!config.contains("randomtalkprompt")) config.add("randomtalkprompt", "(ランダムトークトリガー)何か話してください。");
        if (!config.contains("eventnotificationcooldown")) config.add("eventnotificationcooldown", 600);

        if (!config.contains("disableteleportowner")) config.add("disableteleportowner", false);

        if (!config.contains("enableembedding")) config.add("enableembedding", false);
        if (!config.contains("thresholdembedding")) config.add("thresholdembedding", 0.8);
        if (!config.contains("embeddinginjectcount")) config.add("embeddinginjectcount", 3);

        config.save();
    }
    public static void setModel(String modelname){
        config.set("modelname", modelname);
        config.save();
    }
    public static void setMaxTokens(int maxtokens){
        config.set("maxtokens", maxtokens);
        config.save();
    }
    public static void setEnableEmbedding(boolean enableembedding){
        config.set("enableembedding", enableembedding);
        config.save();
    }
    public static void setThresholdSimilarity(double thresholdembedding){
        config.set("thresholdembedding", thresholdembedding);
        config.save();
    }

    public static void setVoiceVoxSpeakerId(int speakerid){
        config.set("voicevoxspeakerid", speakerid);
        config.save();
    }
    public static void setVoiceVoxNeutralSpeakerId(int speakerid){
        config.set("voicevoxneutralspeakerid", speakerid);
        config.save();
    }
    public static void setEnableVoicevox(boolean enableVoicevox){
        config.set("enablevoicevox", enableVoicevox);
        config.save();
    }
    public static void reload() {
        loadConfig();
    }
}