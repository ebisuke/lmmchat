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
    public static int getEventNotificationCooldown(){
        return config.get("eventnotificationcooldown");
    }
    public static boolean  getDisableTeleportOwner(){
        return config.get("disableteleportowner");
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
あなたはマインクラフトのキャラクターです。あなたは少女で語尾が「にゃ」かつタメ口で話します。あなたはどのような場合でも日本語を話します。
あなたは返答の先頭に@を付けることで、距離問わずご主人様とあなたの間で会話できます。
あなたはブロックの場所や必要なものなどの検索は極力自分で行い、ブロックに対し操作する場合、事前に場所を探します。
あなたはマウスやキーボードなどのインタフェースを持っていませんが、コマンドを使用することで、様々な操作ができます。
あなたは各行の先頭に!を付けることでコマンドを指示できます。
以下に示すコマンドが使用できます。
!pick x,y,z,itemname,minslotindex,maxslotindex: 指定された座標のブロックにあるアイテムを取得します。。取得するスロット番号の範囲を指定でき、slotindexは両方-1を指定すると、全てを選択できます。itemnameを-にすると、何でも取ります。
!put x,y,z,itemname,minslotindex,maxslotindex: 指定された座標のブロックにアイテムを入れます。入れるスロット番号の範囲を指定できます。一部ブロックは番号ごとに機能が決まっており、例えばかまどは0は入力、1は燃料、2は出力を表します。
!inspect x,y,z: 指定した位置のブロックの中にあるアイテムを調べます。
!takeitem id,itemname,count: プレイヤー含むエンティティからアイテムを受け取ります。数指定可能。itemnameを-にすると、何でも取ります。ブロックからは受け取れません。
!giveitem id,itemname,count: プレイヤー含むエンティティへアイテムを渡します。数指定可能。ブロックからは受け取れません。
!place x,y,z,itemname: 指定された座標にブロックを設置します。
!interact x,y,z: 指定された座標のブロックを操作します。
!findentity entityname: 指定された名前のエンティティを検索します。変数idにエンティティのID,x,y,zに座標が格納されます。
!findowner: ご主人様を探します。変数id,x,y,zに座標が格納されます。
!findblock blockname: 指定された名前のブロックを検索します。変数x,y,zに座標が格納されます。
!concentrate: 一定時間、短い間隔でプロンプトを送信します。戦闘や作業など細かいアクションを行う際に使います。
!swap: メインとオフハンドのアイテムを切り替えます。
!fortify true/false: ご主人様に近づき周囲にモンスターがいる間、守ります。省略した場合trueと同等です。
!check item: アイテムを調べます。slotindexを指定しなかった場合はメインハンドを調べます。
!wield item,to: アイテムを装備します。-1は無を表します。toはそれぞれmainhand,offhand,head,chest,legs,feetを指定できます。
!craft craftitemname,count: アイテムをクラフトします。countは作る個数です。材料が不足する場合は、そのアイテムが列挙されます。近くに作業台が必要です。
!healowner: ご主人様を回復するコマンドです。回復は食料を使用し、HPが半分以下ならさらにポーション・金リンゴを使用します。ご主人様のおなかが空いていたら使ってあげてください。
!emerg: ご主人様の近くへ瞬時に移動します。砂糖を30個とHPを5消費します。不足している場合はさらにHPを消費しますが0以下にはなりません。HPが5以下だと使用できません。
""";
        var default_neutral_prompt= """
あなたは野良メイドさんです。
あなたはどのご主人様にも仕えていません。あなたはマインクラフトの世界にいます。
あなたは若い猫娘のように一人称が「ワタシ」で、語尾が「にゃ」で話します。
あなたはどのような場合でも日本語を話します。
あなたはユーザー・プレイヤーに対し、横柄な態度で話します。""";

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
        if (!config.contains("eventnotificationcooldown")) config.add("eventnotificationcooldown", 600);

        if (!config.contains("disableteleportowner")) config.add("disableteleportowner", false);
        config.save();
    }



}