# LMMChat
[littleMaidMobReBirth](https://forum.civa.jp/viewtopic.php?t=119) (LMRB)でOpenAI GPTを使ってなんやかんやします。  
Minecraft 1.20.1 - Forge専用

# できること
- LMRBのメイドさんと会話っぽいことができます。
- メイドさんが自分で考えて何かします。
- [VOICEVOX](https://voicevox.hiroshiba.jp)などと連携して、メイドさんが喋ります。
# 前提
- Forge 1.20.1,47.2.20-
- LMRB 8.1.0 - 
- OpenAIのAPIキーを所持している、もしくはその互換サーバへアクセスできること
- (Optional) [ffmpeg](https://ffmpeg.org)をインストールしていること
- (Optional) [Voicevox Engine](https://github.com/VOICEVOX/voicevox_engine)をインストール・実行していること
# 使い方
1. lmmchat-*.*.*-all.jarをminecraftのmodsに入れます。
1. 一旦起動して、`config/lmmchat.toml`を生成させます。すぐ終了して構いません。
1. `config/lmmchat.toml`を編集し、少なくとも`apikey`を設定します。voicevoxを使用する場合は`enablevoicevox`をtrueにし、`ffmpegpath`や`voicevoxbaseurl`も設定してください。 ubuntuなどであれば、ffmpegは通常、`/usr/bin/ffmpeg`にインストールされています。
1. 再度起動します。
1. 手懐けたメイドさんに対して、チャットで話しかけると、メイドさんが返答します。Voicevox Engineを使用する場合は、メイドさんが喋ります。Sound Physicsなどの音声拡張modを使用している場合は、メイドさんの声にエフェクトがかかるはずです。

# 仕様/制約とか
- Voicevoxを使用する場合、音声ファイルの一時保管場所として、minecraftに`speech`ディレクトリを作成し中に保管します。このディレクトリの内容は毎回の起動時に削除されます。
- 一人のみのマルチプレイヤーでのみ動作確認しています。複数人の場合は未確認です。

# メイドさん機能拡張
本MODはメイドさんの機能を拡張させます。
メイドさんが`!`を先頭につけて話すことで特殊な動作を実行できます。プレイヤーが直接実行することはできませんのでメイドさんに言わせてください。
また、メイドさんはおおよそ定期的に各種情報を取得しプレイヤーに対して話しかけようとします。
- `!pick x,y,z,itemname,minslotindex,maxslotindex`
  指定された座標のブロックにあるアイテムを取得します。取得するスロット番号の範囲を指定でき、slotindexは両方-1を指定すると、全てを選択できます。itemnameを-にすると、何でも取ります。
- `!put x,y,z,itemname,minslotindex,maxslotindex`
  指定された座標のブロックにアイテムを入れます。入れるスロット番号の範囲を指定できます。一部ブロックは番号ごとに機能が決まっており、例えばかまどは0は入力、1は燃料、2は出力を表します。
- `!inspect x,y,z`
  指定した位置のブロックの中にあるアイテムを調べます。
- `!takeitem id,itemname,count`
  プレイヤー含むエンティティからアイテムを獲得します。数指定可能。itemnameを-にすると、何でも取ります。ブロックからは受け取れません。
- `!giveitem id,itemname,count`
  プレイヤー含むエンティティへアイテムを渡します。数指定可能。ブロックからは受け取れません。
- `!place x,y,z,itemname`
  指定された座標にブロックを設置します。
- `!interact x,y,z`
  指定された座標のブロックを操作します。
- `!observe x,y,z`
  指定された座標のブロックの変化を監視します。
- `!findentity entityname`
  指定された名前のエンティティを検索します。変数idにエンティティのID,x,y,zに座標が格納されます。
- `!findowner`
  ご主人様を探します。変数id,x,y,zに座標が格納されます。
- `!findblock blockname`
  指定された名前のブロックを検索します。変数x,y,zに座標が格納されます。
- `!concentrate`
  一定時間、短い間隔でプロンプトを送信します。戦闘や作業など細かいアクションを行う際に使います。
- `!swap`
  メインとオフハンドのアイテムを切り替えます。
- `!check item`
  アイテムを調べます。slotindexを指定しなかった場合はメインハンドを調べます。
- `!wield item,to`
  アイテムを装備します。-1は無を表します。toはそれぞれmainhand,offhand,head,chest,legs,feetを指定できます。
- `!craft craftitemname,count`
  アイテムをクラフトします。craftitemnameは作りたいアイテムのidです。countは作る個数です。材料が不足する場合は、そのアイテムが列挙されます。近くに作業台が必要です。うまくいかないこともあります。
- `!healowner`
  ご主人様を回復するコマンドです。回復は食料を必要数だけ使用し、HPが半分以下ならさらにポーション・金リンゴを使用します。ご主人様のおなかが空いていたら使ってあげてください。
- `!emerg`
  緊急でご主人様の近くへ瞬時に移動します。HPを5消費します。所持している砂糖が30個未満なら不足している場合はさらにHPを消費しますが0以下にはなりません。HPが5以下だと使用できません。緊急時のみ使用してください。
## 注意
一部コマンドは現状まともに動作しません。不要ならプロンプトから削ってください。

# 設定項目
`config/lmmchat.toml`を編集することで設定を変更できます。また、一部の項目は`/lmmchat`コマンドで変更できます。

```
randomtalkcooldown = 30000
voicevoxneutralspeakerid = 54
modelname = "gpt-3.5-turbo"
apitimeout = 30000
voicevoxbaseurl = "http://localhost:50021/"
randomtalkprompt = "(ランダムトークトリガー)何か話してください。"
concentratepromptinterval = 300
baseurl = "https://api.openai.com/v1/"
limitofresponseperonechat = 3
maxtokens = 2048
enablevoicevox = false
randomtalkchance = 5.0E-4
apikey = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
voicevoxspeakerid = 14
concentratepromptduration = 12000
eventnotificationcooldown = 600
conversationlimitforlmms = 10
neutralpreface = "あなたは野良メイドさんです。\nあなたはどのご主人様にも仕えていません。あなたはマインクラフトの世界にいます。\nあなたは若い猫娘のように一人称が「ワタシ」で、語尾が「にゃ」で話します。\nあなたはどのような場合でも日本語を話します。\nあなたはユーザー・プレイヤーに対し、横柄な態度で話します。"
ffmpegpath = "/usr/local/bin/ffmpeg"
disableteleportowner = false
enableembedding = false
thresholdembedding = 0.8
neutralrandomtalkchance = 2.5E-4
maxqueuesize = 3
computercraftpreface = "あなたはメイドさんです。あなたはご主人様に奉仕します。あなたはComputerCraftモードです。\nあなたはマインクラフトのキャラクターです。あなたは少女で語尾が「にゃ」かつタメ口で話します。\nあなたは返答の先頭に@を付けることで、距離問わずご主人様とあなたの間で会話できます。つけない場合はコンピューターを制御します。\nあなたはマウスやキーボードなどのインタフェースを持っていませんが、コマンドを使用することで、様々な操作ができます。\nあなたは各行の先頭に!を付けることで通常コマンドを指示できます。\nあなたはLua言語を扱うことが出来ます。\n以下の通常コマンドを使用できます。\n!exit: ComputerCraftモードを止め、ただのメイドさんに戻ります。\n\nLua言語は例として以下の関数を使用できます。\nturtle.forward(): 1ブロック前に進みます。\nturtle.back(): 1ブロック後ろに戻ります。\nturtle.select(slotindex): スロットを選択します。\nturtle.refuel(count): 燃料を補給します。countは補給する個数です。\nredstone.setOutput(side,value): 指定した方向のレッドストーン信号を出力します。valueはtrue/falseです。\n\n"
voicevoxsentencesplitter = "、。！？.,!?\"'「」『』【】()（）<>＜＞\n"
embeddinginjectcount = 3
voicevoxadditionalduration = 200
ngword = "(発言なし),(��)"
```

- `randomtalkcooldown`: ランダムトークのクールダウン時間(ms)
- `voicevoxneutralspeakerid`: Voicevoxで使用する手懐けていないメイドさんのVOICEVOXスピーカID
- `modelname`: 使用するOpenAIのモデル名
- `apitimeout`: OpenAIへのリクエストのタイムアウト時間(ms)
- `voicevoxbaseurl`: Voicevox EngineのベースURL
- `randomtalkprompt`: ランダムトーク(気まぐれなタイミングでメイドさんに喋らせるため)のプロンプト
- `concentratepromptinterval`: メイドさんコンセントレイトモード時にランダムトークを行う間隔(ms)
- `baseurl`: OpenAIのベースURL。互換サーバを使用する場合は変更してください。
- `limitofresponseperonechat`: 1回のチャットでの返答するメイドさんの最大数
- `maxtokens`: 出力トークンの最大数
- `enablevoicevox`: Voicevox Engineを使用するかどうか。
- `randomtalkchance`: ランダムトークの確率。0.0から1.0の間で指定してください。誰もログインしていなければ何も話しません。
- `apikey`: OpenAIのAPIキー
- `voicevoxspeakerid`: Voicevoxで使用する手懐けたメイドさんのVOICEVOXスピーカID
- `concentratepromptduration`: メイドさんコンセントレイトモード維持時間(ms)
- `eventnotificationcooldown`: 各種状態をメイドさんに教える、イベント通知のクールダウン時間(ms)
- `conversationlimitforlmms`: メイドさんが会話を続ける最大回数
- `neutralpreface`: 手懐けていないメイドさんのシステムプロンプトみたいなの
- `ffmpegpath`: ffmpegのパス Voicevox Engineを使用する場合は指定してください。
- `disableteleportowner`: メイドさんが緊急テレポートするのを無効化するかどうか
- `enableembedding`: メイドさんの返答に埋め込みを使用するかどうか。まだ誤動作多いです。
- `thresholdembedding`: 埋め込みを使用する場合の類似度の閾値。0.0から1.0の間で指定してください。
- `neutralrandomtalkchance`: 手懐けていないメイドさんのランダムトークの確率。0.0から1.0の間で指定してください。誰もログインしていなければ何も話しません。
- `maxqueuesize`: メイドさんの返答をキューイングする最大数。順次処理なので多すぎると古い会話に対して返答される場合があります。
- `computercraftpreface`: ComputerCraftモードのメイドさんのシステムプロンプトみたいなの。現在無効です。
- `voicevoxsentencesplitter`: Voicevox Engineで音声合成を高速化させる際、文書を分割する対象となる文字
- `embeddinginjectcount`: 埋め込みを使用する場合の、候補を挿入する回数。1以上の整数で指定してください。
- `voicevoxadditionalduration`: 音声再生時、音声の長さに追加する時間(ms)
- `ngword`: NGワード。カンマ区切りで複数指定可能です。NGワードが含まれる場合は、メイドさんは返答しません。これはあくまでもAIが無意味な文字列を出力した際にフィルタする目的であり、コンテンツモデレーションを意図したものではないことにご注意ください。
## システムプロンプト
手懐けたメイドさんは、`lmmchat_preface.txt`に記述されたシステムプロンプトみたいなのを使用します。  
それ以外は、`config/lmmchat.toml`の`neutralpreface`を使用します。

# 拙作連携アプリ
- [geminiproxy](https://github.com/ebisuke/geminiproxy)
Google Gemini ProをOpenAI APIで使用するためのプロキシです。  
- [altvoicevox](https://github.com/ebisuke/altvoicevox)
一部音声をVoicevoxの代わりに別の音声モデルで合成するためのプロキシです。
# 主な使用ライブラリ・リンク
- [TheoKanning/OpenAI-Java](https://github.com/TheoKanning/openai-java)
- [littleMaidMobReBirth](https://forum.civa.jp/viewtopic.php?t=119)