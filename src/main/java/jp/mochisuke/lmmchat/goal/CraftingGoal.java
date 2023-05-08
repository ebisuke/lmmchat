package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.helper.Helper;
import jp.mochisuke.lmmchat.lmm.PseudoPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class CraftingGoal<T extends TamableAnimal> extends AIGoalBase {
    protected final T entity;
    protected  String craftItemName;
    CraftingRecipe recipe;
    boolean instantCraft;
    Vec3i nearbyCraftingTable;
    int count;
    Item craftItem;
    int craftingProgress=0;
    public CraftingGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return active;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        super.start();
        prepare();
        if(!instantCraft){
            navigateNearbyCraftingTable();
        }
    }
    public void setup(String craftItemName,int count){
        this.craftItemName=craftItemName;
        this.count=count;
        activate();
    }
    @Override
    public void stop() {
        entity.getNavigation().stop();
    }

    private void prepare(){
        //generate virtual item stack

        //find resource location by item name
        ResourceLocation resourceLocation=null;
        // fuzzy search
        for (ResourceLocation key : ForgeRegistries.ITEMS.getKeys()) {
            if (key.getPath().contains(craftItemName)) {
                resourceLocation = key;
                break;
            }
        }
        if(resourceLocation==null){
            fail("specified item resource not found");
            return;
        }

        var targetItem=ForgeRegistries.ITEMS.getValue(new ResourceLocation(craftItemName));
        if(targetItem==null){
            fail("specified item not found");
            return;
        }
        craftItem=targetItem;

        var itemStack=new ItemStack(targetItem);

        //get all recipe
        var recipeManager=entity.level.getRecipeManager();
        var allRecipes=recipeManager.getRecipes();
        //filter recipe by shaped or shapeless
        var filtered=allRecipes.stream().filter(recipe -> recipe.getType()== RecipeType.CRAFTING);

        //find recipe can craft target item
        var recipe=filtered.filter(r -> r.getResultItem().is(itemStack.getItem())).findFirst();
        //found?

        if(recipe.isPresent()) {
            //found
            //check 2x2 crafting recipe
            this.recipe= (CraftingRecipe) recipe.get();
            instantCraft=false;
//            if(recipe.get().canCraftInDimensions(2,2)){
//                instantCraft=true;
//            }
            navigateNearbyCraftingTable();

        }else{
            fail("recipe not found");
        }

    }
    protected boolean navigateNearbyCraftingTable(){
        int range=20;
        int x=entity.blockPosition().getX()-range/2;
        int y=entity.blockPosition().getY()-range/2;
        int z=entity.blockPosition().getZ()-range/2;
        Vec3i nearest=null;
        for(int i=0;i<range;i++){
            for(int j=0;j<range;j++){
                for(int k=0;k<range;k++){
                    if(entity.level.getBlockState(new BlockPos(x+i,y+j,z+k)).getBlock() == Blocks.CRAFTING_TABLE){
                        Vec3i pos=new Vec3i(x+i,y+j,z+k);
                        if(nearest==null&&pos.closerThan(entity.blockPosition(),range)) {
                            nearest = pos;
                        }
                    }
                }
            }
        }
        if(nearest!=null){
            nearbyCraftingTable=nearest;
            entity.getNavigation().moveTo(nearest.getX(),nearest.getY(),nearest.getZ(),1);
            return true;
        }
        return false;
    }
    @Override
    public void tick() {
        if(!canUse()){
            return;
        }
        if(nearbyCraftingTable==null){
            if(!navigateNearbyCraftingTable()){
                fail("crafting table not found");
                return;
            }
        }
        //check valid
        BlockState block;
        if(!instantCraft) {
            block = entity.getLevel().getBlockState(new BlockPos(nearbyCraftingTable.getX(), nearbyCraftingTable.getY(), nearbyCraftingTable.getZ()));

            if (!block.is(Blocks.CRAFTING_TABLE)) {
                if (!navigateNearbyCraftingTable()) {
                    fail("crafting table not found");
                    return;
                }
            }
        }
        if(nearbyCraftingTable==null){
            fail("crafting table not found");
            return;
        }
        //check crafting table is near or instantcraft?
        Vec3i entitypos=entity.blockPosition();
        if(instantCraft || (nearbyCraftingTable!=null &&  entitypos.closerThan(nearbyCraftingTable,4))){
            entity.getNavigation().stop();
            if(craftingProgress<40){
                craftingProgress++;
                return;
            }else {
                Container inventory = Helper.getInventoryContainer(entity);
                //check craftable by recipe
                var manager = entity.getLevel().getRecipeManager();

                //get crafting table
                var craftingTable = entity.getLevel().getBlockEntity(new BlockPos(nearbyCraftingTable.getX(), nearbyCraftingTable.getY(), nearbyCraftingTable.getZ()));
                //found?
                if (craftingTable == null) {
                    fail("crafting table not found or already destroyed");
                    return;
                }
                CraftingTableBlock ccontainer = (CraftingTableBlock) craftingTable.getBlockState().getBlock();
                //get menu provider

                PseudoPlayer pseudoPlayer = new PseudoPlayer(entity.getLevel(), new BlockPos(nearbyCraftingTable.getX(), nearbyCraftingTable.getY(), nearbyCraftingTable.getZ()));
                var menuProvider = ccontainer.getMenuProvider(craftingTable.getBlockState(), entity.getLevel(),
                        new BlockPos(nearbyCraftingTable.getX(), nearbyCraftingTable.getY(), nearbyCraftingTable.getZ())
                );

                var menu = menuProvider.createMenu(0, pseudoPlayer.getInventory(), pseudoPlayer);
                //check recipe

                //prepare crafting grid
                CraftingContainer craftingContainer = new CraftingContainer(menu, 3, 3);
                var ingredients = recipe.getIngredients();
                var maidcontainer = Helper.getInventoryContainer(entity);
                //swing
                entity.swing(InteractionHand.MAIN_HAND);
                for (int repeat = 0; repeat < count; repeat++) {
                    for (int i = 0; i < 9; i++) {
                        var ingredient = ingredients.get(i);
                        if (ingredient.isEmpty()) {
                            continue;
                        }
                        //find maid inventory
                        for (int j = 0; j < maidcontainer.getContainerSize(); j++) {
                            var item = maidcontainer.getItem(j);
                            if (item.getItem() == ingredient.getItems()[0].getItem()) {
                                craftingContainer.setItem(i, item);
                                //remove item from maid inventory
                                maidcontainer.removeItem(j, 1);
                                break;
                            }
                        }
                    }
                    //attempt to assemble
                    var result = recipe.assemble(craftingContainer);
                    if (result.isEmpty()) {
                        //restore maid inventory
                        for (int i = 0; i < 9; i++) {
                            var item = craftingContainer.getItem(i);
                            if (item.isEmpty()) {
                                continue;
                            }
                            //store
                            for (int j = 0; j < maidcontainer.getContainerSize(); j++) {
                                var maiditem = maidcontainer.getItem(j);
                                if (maiditem.isEmpty()) {
                                    maidcontainer.setItem(j, item);
                                    break;
                                } else if (maiditem.getItem() == item.getItem() && maiditem.getCount() < maiditem.getMaxStackSize()) {
                                    maidcontainer.getItem(j).grow(1);
                                    break;
                                }
                            }
                        }
                        if(repeat==0){
                            String needed="";
                            HashMap<String,Integer> neededmap=new HashMap<>();
                            for (int i = 0; i < 9; i++) {
                                var ingredient = ingredients.get(i);
                                if (ingredient.isEmpty()) {
                                    continue;
                                }
                                if(neededmap.containsKey(ingredient.getItems()[0].getDisplayName().getString())) {
                                    neededmap.put(ingredient.getItems()[0].getDisplayName().getString(), neededmap.get(ingredient.getItems()[0].getDisplayName().getString()) + 1);
                                }else{
                                    neededmap.put(ingredient.getItems()[0].getDisplayName().getString(), 1);
                                }
                            }
                            //build string
                            for(String key:neededmap.keySet()){
                                needed+=key+"x"+neededmap.get(key)+",";
                            }
                            //remove last ,
                            needed=needed.substring(0,needed.length()-1);
                            fail("insufficient ingredients. needed: "+needed);
                        }else{
                            fail("insufficient ingredients. but partially crafted " + repeat + "/" + count + ".");
                        }

                        return;
                    } else {
                        //store result
                        boolean stored = false;
                        for (int i = 0; i < maidcontainer.getContainerSize(); i++) {
                            var item = maidcontainer.getItem(i);
                            if (item.isEmpty()) {
                                maidcontainer.setItem(i, result);
                                stored = true;
                                break;
                            } else if (item.getItem() == result.getItem() && (item.getCount()) < item.getMaxStackSize()) {
                                int storable = item.getMaxStackSize() - item.getCount();
                                item.grow(storable);
                                result.shrink(storable);

                                if (result.isEmpty()) {
                                    stored = true;
                                    break;
                                }
                            }
                        }
                        if (!stored) {
                            pseudoPlayer.drop(result, true);
                        }
                    }

                }
                success();
            }

        }else{
            if(entity.getNavigation().isDone()){
                //renavigate
                if(!navigateNearbyCraftingTable()){
                    fail("crafting table not found");
                }
            }
        }
    }
}
