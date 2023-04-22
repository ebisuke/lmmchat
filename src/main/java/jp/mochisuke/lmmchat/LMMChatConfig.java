package jp.mochisuke.lmmchat;

import com.electronwill.nightconfig.core.file.FileConfig;

//configurations class for forge
public class LMMChatConfig  {

    public static FileConfig config;

    public static String getApiKey(){
        return config.get("apikey");
    }
    public static String getModelName(){
        return config.get("modelname");
    }

    public static String getPreface(){
        return config.get("preface");
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
    public static float getRandomTalkChance(){
        return config.get("randomtalkchance");
    }
    public static float getNeutralRandomTalkChance(){
        return config.get("neutralrandomtalkchance");
    }
    public static long getRandomTalkCooldown(){
        return config.get("randomtalkcooldown");
    }
    public static int getLimitOfResponsePerOneChat(){
        return config.get("limitofresponseperonechat");
    }

    public static void loadConfig(){

        // load from file
        config= FileConfig.of("config/lmmchat.toml");


        config.load();

        var default_prompt= """
あなたはメイドさんです。あなたはご主人様に奉仕します。
あなたはマインクラフトの世界にいます。あなたは若い猫娘のように語尾が「にゃ」で話します。あなたはどのような場合でも日本語を話します。
あなたはユーザー・プレイヤーに対し、友好的に話します。
itemname,entitynameはminecraftの内部名称です。
あなたは返答の先頭に!を付けることでコマンドを指示できます。コマンドを使用する必要が無い場合は!endだけを送ります。以下に示すコマンドが使用できます。
!end: コマンド会話の終了を表します。同時に他のコマンドを使用してはいけません。
!pick x,y,z,itemname,minslotindex,maxslotindex: あなたは指定された座標のブロックにあるアイテムを拾います。拾うスロット番号の範囲を指定できます。
!put x,y,z,itemname,minslotindex,maxslotindex: あなたは指定された座標のブロックにアイテムを置きます。置くスロット番号の範囲を指定できます。
!take targetid,itemname,count: あなたは指定された名前のエンティティからアイテムを取ります。取るアイテムの数を指定できます。
!give targetid,itemname,count: あなたは指定された名前のエンティティにアイテムを渡します。渡すアイテムの数を指定できます。
!move x,y,z: あなたは指定された座標に移動します。
!attack targetid: あなたは指定された名前のエンティティを攻撃します。
!findentity entityname: あなたは指定された名前のエンティティを探します。返答はtargetidです。
!findowner: あなたはご主人様を探します。返答はtargetidです。
!findblock entityname: あなたは指定された名前のブロックを探します。返答はx,y,zです。
                """;
        var default_neutral_prompt= """
あなたは野良メイドさんです。あなたはどのご主人様にも仕えていません。あなたはマインクラフトの世界にいます。あなたは若い猫娘のように語尾が「にゃ」で話します。あなたはどのような場合でも日本語を話します。
あなたはユーザー・プレイヤーに対し、やや敵対的に話します。
                """;

        // set default values
        if (!config.contains("apikey")) config.add("apikey", "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        if (!config.contains("modelname")) config.add("modelname", "gpt-3.5-turbo");
        if (!config.contains("preface")) config.add("preface", default_prompt);
        if (!config.contains("neutralpreface")) config.add("neutralpreface", default_neutral_prompt);
        if (!config.contains("maxtokens")) config.add("maxtokens", 2048);
        if (!config.contains("conversationlimitforlmms")) config.add("conversationlimitforlmms", 10);

        if (!config.contains("randomtalkchance")) config.add("randomtalkchance", 0.005f);
        if (!config.contains("neutralrandomtalkchance")) config.add("neutralrandomtalkchance", 0.0025f);
        if (!config.contains("randomtalkcooldown")) config.add("randomtalkcooldown", 30000L);

        if (!config.contains("limitofresponseperonechat")) config.add("limitofresponseperonechat", 3);

        config.save();
    }



}