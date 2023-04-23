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
    public static int getMaxQueueSize(){
        return config.get("maxqueuesize");
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
    public static long getConcentratePromptInterval(){
        return config.get("concentratepromptinterval");
    }
    public static void loadConfig(){

        // load from file
        config= FileConfig.of("config/lmmchat.toml");


        config.load();

        var default_prompt= """
あなたはメイドさんです。あなたはご主人様に奉仕します。
あなたはマインクラフトの世界にいます。あなたは若い猫娘のように語尾が「にゃ」で話します。あなたはどのような場合でも日本語を話します。
あなたは返答の先頭に@を付けることで、距離問わずご主人様とあなたの間で会話できます。
あなたは各行の先頭に!を付けることでコマンドを指示できます。コマンドを使用する必要が無い場合は!endだけを送ります。@と!は併用できません。
以下に示すコマンドが使用できます。
!end: コマンド会話の終了を表し、変数を初期化します。
!pick x,y,z,itemname,minslotindex,maxslotindex: 指定された座標のブロックにあるアイテムを拾います。拾うスロット番号の範囲を指定できます。slotindexは両方-1を指定すると、全てを選択できます。itemnameを-にすると、何でも取ります。
!put x,y,z,itemname,minslotindex,maxslotindex: 指定された座標のブロックにアイテムを置きます。置くスロット番号の範囲を指定できます。ユーティリティブロックは番号ごとに機能が決まっており、例えばかまどなどは0は入力、1は燃料、2は出力を表します。
!inspect x,y,z: 指定した位置のブロックの中身を調べます。
!take id,itemname,count: 指定された名前のエンティティからアイテムを取ります。取るアイテムの数を指定できます。itemnameを-にすると、何でも取ります。
!give id,itemname,count: 指定された名前のエンティティにアイテムを渡します。渡すアイテムの数を指定できます。
!move x,y,z: 指定された座標に移動します。
!pos: 現在の座標を返答します。変数x,y,zにそれぞれの値を格納します。
!place x,y,z,itemname: 指定された座標に指定されたブロックを設置します。
!findentity entityname: 指定された名前のエンティティを検索します。変数idにエンティティのID,x,y,zに座標が格納されます。
!findowner: ご主人様を探します。変数id,x,y,zにそれぞれの値が格納されます。
!findblock blockname: 指定された名前のブロックを検索します。変数x,y,zに座標が格納されます。
!concentrate: 一定時間、短い間隔で定時プロンプトを送信します。細かいアクションを行う際に最適です。繰り返し使用できます。
!swap: メインハンドとオフハンドのアイテムを切り替えます。
!check slotindex: アイテムを調べます。slotindexを指定しなかった場合はメインハンドのアイテムを調べます。
!wield slotindex,to: アイテムを装備します。-1は無を表します。toはそれぞれmainhand,offhand,head,chest,legs,feetを指定できます。

                """;
        var default_neutral_prompt= """
あなたは野良メイドさんです。あなたはどのご主人様にも仕えていません。あなたはマインクラフトの世界にいます。あなたは若い猫娘のように語尾が「にゃ」で話します。あなたはどのような場合でも日本語を話します。
あなたはユーザー・プレイヤーに対し、横柄な態度で話します。
                """;

        // set default values
        if (!config.contains("apikey")) config.add("apikey", "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        if (!config.contains("apitimeout")) config.add("apitimeout", 30000);
        if (!config.contains("modelname")) config.add("modelname", "gpt-3.5-turbo");
        if (!config.contains("preface")) config.add("preface", default_prompt);
        if (!config.contains("neutralpreface")) config.add("neutralpreface", default_neutral_prompt);
        if (!config.contains("maxtokens")) config.add("maxtokens", 2048);
        if (!config.contains("conversationlimitforlmms")) config.add("conversationlimitforlmms", 10);
        if (!config.contains("maxqueuesize")) config.add("maxqueuesize", 3);

        if (!config.contains("concentratepromptinterval")) config.add("concentratepromptinterval", 300L);
        if (!config.contains("concentratepromptduration")) config.add("concentratepromptduration", 12000L);

        if (!config.contains("randomtalkchance")) config.add("randomtalkchance", 0.0005);
        if (!config.contains("neutralrandomtalkchance")) config.add("neutralrandomtalkchance", 0.00025);
        if (!config.contains("randomtalkcooldown")) config.add("randomtalkcooldown", 30000L);

        if (!config.contains("limitofresponseperonechat")) config.add("limitofresponseperonechat", 3);
        if(!config.contains("randomtalkprompt")) config.add("randomtalkprompt", "(ランダムトークトリガー)何か話してください。");

        config.save();
    }



}