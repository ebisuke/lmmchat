package jp.mochisuke.lmmchat.embedding;

import com.mojang.logging.LogUtils;
import io.reactivex.annotations.Nullable;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import kotlin.jvm.functions.Function2;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.CraftingTableBlock;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class EmbeddingTask {
    private static final Logger logger = LogUtils.getLogger();
    private final EmbeddingDictionary dictionary=new EmbeddingDictionary();
    private final IEmbedderBase embedder;
    private final Thread thread;
    private    HashMap<String,List<Double>> stringToVectorMap = new HashMap<String, List<Double>>();
    private ConcurrentLinkedQueue<Tuple<String,EmbeddingAnswer >> toGenerate= new ConcurrentLinkedQueue<Tuple<String, EmbeddingAnswer>>();
    String path="config/lmmchat_embedding.csv";
    public EmbeddingTask( IEmbedderBase embedder){
        this.embedder = embedder;


        //run thread

        thread= new  Thread(this::threadProc);
        thread.start();
    }
    public void prepareData() throws FileNotFoundException {

        //load from txt file

        File textFile = new File(path);
        stringToVectorMap.clear();
        if(textFile.exists()){
            FileInputStream fileInputStream = new FileInputStream(textFile);
            //wrap string stream
            Stream<String> stream = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)).lines();

            //read file
            stream.map(s -> s.split(",")).forEach(s -> {
                if(s.length<=1){
                    return;
                }
                //unescape comma
                s[0]=s[0].replace("､",",");
                //unescape newline
                s[0]=s[0].replace("\\n","\n");
                //concat 2nd comma separated string
                String word = s[0];
                String vector= Arrays.stream(s).skip(1).reduce((s1, s2) -> s1 + "," + s2).orElse("");

                //parse vector to  float array

                List<Double> vectorArray = Arrays.stream(vector.split(",")).mapToDouble(Double::parseDouble).boxed().toList();
                //add to dictionary
                stringToVectorMap.put(word,vectorArray);
            });
            stream.close();
        }


        //------------- recipe embedding --------------
        logger.info("start recipe embedding prep");
        //get all craft recipes
        var recipeManager= LMMChat.getServer().getRecipeManager();
        recipeManager.getRecipes().stream().filter(
                x -> x.getType().equals(RecipeType.CRAFTING) || x.getType().equals(RecipeType.SMELTING) || x.getType().equals(RecipeType.BLASTING) || x.getType().equals(RecipeType.SMOKING)
        ).forEach(
                recipe->{
                    final StringBuilder recipeString=new StringBuilder();
                    //dump recipe product name

                    recipeString.append(recipe.getType().toString()+ " でアイテム"+recipe.getResultItem().getDisplayName().getString()+"x"+ recipe.getResultItem().getCount()+"の作り方\n");
                    //get all ingredients
                    recipeString.append("必要な材料:");
                    var ingredients=recipe.getIngredients();
                    //count ingredients per itemtype
                    var ingredientCountMap=new HashMap<String,Integer>();
                    ingredients.forEach(
                            ingredient->{
                                var itemStacks= Arrays.stream(ingredient.getItems());
                                itemStacks.forEach(
                                        itemStack->{
                                            var item=itemStack.getItem();
                                            var itemName=item.getDefaultInstance().getDisplayName().getString();
                                            if(ingredientCountMap.containsKey(itemName)){
                                                ingredientCountMap.put(itemName,ingredientCountMap.get(itemName)+1);
                                            }else{
                                                ingredientCountMap.put(itemName,1);
                                            }
                                        }
                                );
                            }
                    );
                    //dump ingredients list
                    ingredientCountMap.forEach(
                            (itemName,count)->{
                                recipeString.append(itemName+" x"+count+"\n");
                            }
                    );
                    //calculate embedding

                    add(recipe.getResultItem().getDisplayName().getString()+" "+ recipe.getResultItem().getCount()+"個をクラフト",recipeString.toString());


                }
        );

        logger.info("finish recipe embedding prep");
        //------------- information notification ----------
        logger.info("start information notification embedding prep");
        add("近くの敵",FunctionsForEmbedding::nearbyEntities, "hostile", Monster.class);
        add("近くのエンティティ",FunctionsForEmbedding::nearbyEntities, "living", LivingEntity.class);
        add("近くの動物",FunctionsForEmbedding::nearbyEntities, "tamable", TamableAnimal.class);
        add("作業台の場所",FunctionsForEmbedding::nearbyBlocks, CraftingTableBlock.class);
        add("持ち物",FunctionsForEmbedding::inspectInventory);
        add("インベントリ",FunctionsForEmbedding::inspectInventory);
        add("時刻",FunctionsForEmbedding::getTime);
        add("何時",FunctionsForEmbedding::getTime);

        //-------------- how to use -----------------------
        add("かまどの使い方","かまど(furnace) スロット 0:入力,1:燃料,2:出力");
        add("燻製機の使い方","燻製機(smoker) スロット 0:入力,1:燃料,2:出力");
        add("精錬炉の使い方","精錬炉(blast furnace) スロット 0:入力,1:燃料,2:出力");
        add("醸造台の使い方","醸造台(brewing stand) スロット 0.1.2:ボトル,3:材料,4:ブレイズパウダー");

    }
    public void saveDict() throws IOException {
        File textFile = new File(path);

        FileOutputStream fileOutputStream = new FileOutputStream(textFile);
        var stream = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        //write to file

        for(var entry:stringToVectorMap.keySet()){
            var value=stringToVectorMap.get(entry);
            entry=entry.replace(",","､");
            var line=entry+","+value.stream().map(Object::toString).reduce((s1, s2) -> s1 + "," + s2).orElse("");
            //escape comma

            //escape newline
            line=line.replace("\n","\\n");
            stream.write(line+"\n");
        }

        stream.close();


    }
    public void add(String question, String answer){
        if(stringToVectorMap.containsKey(question)){
            EmbeddingQuestion embeddingQuestion = new EmbeddingQuestion(question, stringToVectorMap.get(question));
            dictionary.add(embeddingQuestion,new EmbeddingAnswer(answer,null));
        }else{
            toGenerate.add(new Tuple<String, EmbeddingAnswer>(question,new EmbeddingAnswer(answer,null)));
        }

    }
    public void add(String question, Function2<TamableAnimal,Object[],String> answer,Object... args){
        question=question.trim();
        if(stringToVectorMap.containsKey(question)){
            EmbeddingQuestion embeddingQuestion = new EmbeddingQuestion(question, stringToVectorMap.get(question));
            dictionary.add(embeddingQuestion,new EmbeddingAnswer(answer,args));
        }else{
            toGenerate.add(new Tuple<String, EmbeddingAnswer>(question,new EmbeddingAnswer(answer,args)));
        }

    }




    public void threadProc(){
        try {
            prepareData();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while(true){
            if(toGenerate.size()>0){
                int chunk=500;
                for(int count=0;count<Math.ceil((double)toGenerate.size()/chunk);count++) {
                    //get whole
                    var toGenerateList = new ConcurrentLinkedQueue<Tuple<String, EmbeddingAnswer>>();
                    while (toGenerate.size() > 0 && toGenerateList.size()<chunk) {
                        toGenerateList.add(toGenerate.poll());
                    }
                    if(toGenerateList.size()==0){
                        break;
                    }
                    ArrayList<String> questions = new ArrayList<String>();
                    questions.addAll(toGenerateList.stream().map(x -> x.getA()).toList());
                    //generate
                    var embeddedQuestions = embedder.calculateEmbedding(questions);
                    //add to dictionary
                    for (int i = 0; i < toGenerateList.size(); i++) {
                        var question = toGenerateList.poll();
                        var embeddedQuestion = embeddedQuestions.get(i);
                        //add conversion dict
                        stringToVectorMap.put(question.getA(), embeddedQuestion);
                        dictionary.add(new EmbeddingQuestion(question.getA(), embeddedQuestion), question.getB());
                    }
                    //save
                    try {
                        saveDict();
                    } catch (IOException e) {
                        logger.error("failed to save dictionary", e);
                    }
                    logger.info("added " + toGenerateList.size() + " questions to dictionary");
                }
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public @Nullable List<Object> searchSimilar(String question,int count){
        var embeddedQuestion=embedder.calculateEmbedding(List.of(question)).get(0);
        var similar= dictionary.searchSimilar(embeddedQuestion, LMMChatConfig.getThresholdSimilarity(),count);

        if(similar!=null && similar.size()>0){
            ArrayList<Object> result=new ArrayList<Object>();
            for(var entry:similar){
                result.add(entry.getAnswer());
            }
            return result;
        }else{
            return null;
        }
    }
}
