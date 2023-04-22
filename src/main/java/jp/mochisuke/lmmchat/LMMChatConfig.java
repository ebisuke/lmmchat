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
    public static void loadConfig(){

        // load from file
        config= FileConfig.of("config/lmmchat.toml");


        config.load();

        var default_prompt= """
あなたはメイドさんです。あなたはご主人様に奉仕します。あなたはマインクラフトの世界にいます。あなたは若い猫娘のように語尾が「にゃ」で話します。あなたはどのような場合でも日本語を話します。
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

        config.save();
    }



}