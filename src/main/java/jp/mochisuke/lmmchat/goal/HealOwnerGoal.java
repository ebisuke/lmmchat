package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Random;

public class HealOwnerGoal<T extends TamableAnimal> extends AIGoalBase{
    protected final T entity;
    int cooldown=0;

    int healed=0;

    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.MOVE,Flag.LOOK);
    }

    public HealOwnerGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return active;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canUse();
    }
    public void setUp(){
        activate();
    }

    @Override
    public void start() {
        //navigate to owner
        LivingEntity owner=entity.getOwner();
        if(owner==null){
            fail("owner not found");
            return;
        }
        entity.getNavigation().moveTo(owner,1.0);
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
    public void activateCooldown(){
        cooldown=18;
    }
    @Override
    public void tick() {
        if(!canUse()){
            return;
        }
        LivingEntity owner=entity.getOwner();
        if(!(owner instanceof Player player)){
            fail("no owner");
            return;
        }
        if(!player.getFoodData().needsFood() && owner.getHealth()>=owner.getMaxHealth()){
            if(healed>0){
                success();
            }else {
                fail("owner is full health");
            }
            return;
        }

        if(cooldown>0){
            cooldown--;
            return;
        }
        //check  distance between owner and entity
        if(entity.distanceTo(owner)>4){
            //far
            return;
        }
        //look target
        entity.getLookControl().setLookAt(owner,30,30);
        //severity
        int severity=0;
        if(owner.getHealth()<owner.getMaxHealth()*2/3){
            severity=1;
        }
        if(owner.getHealth()<owner.getMaxHealth()/2){
            severity=2;
        }
        Container maidinventory= Helper.getInventoryContainer(entity);
        Container playerinventory=player.getInventory();

        //heal by golden apple
        if(severity>=2){
            // not satisfied?
                //find edible items from maid inventory
                var edibles=Helper.findEdibleItems(maidinventory);
                //removing by filter golden apple ,spider eye,rotten flesh, poisonous potato
                edibles.removeIf(itemStack ->
                        (!itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.golden_apple")||
                                !itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.enchanted_golden_apple")));



                if(!edibles.isEmpty()){
                    //eat
                    //pick randomly
                    Random random=new Random();
                    int index=random.nextInt(edibles.size());
                    ItemStack edible=edibles.get(index);

                    //eat
                    player.eat(player.level(),edible);
                    //sound
                    player.level().playSound(player,
                            player.blockPosition(),edible.getItem().getEatingSound(),player.getSoundSource(),1.0f,1.0f);
                    //swing
                    entity.swing(InteractionHand.MAIN_HAND);
                    healed++;
                    //cooldown
                    activateCooldown();
                    return;
                }
                //find player inventory
                edibles=Helper.findEdibleItems(playerinventory);
                //filter golden apple ,spider eye,rotten flesh, poisonous potato
                edibles.removeIf(itemStack ->
                        (!itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.golden_apple")||
                                !itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.enchanted_golden_apple")));

                if(!edibles.isEmpty()){
                    //eat
                    //pick randomly
                    Random random=new Random();
                    int index=random.nextInt(edibles.size());
                    ItemStack edible=edibles.get(index);
                    //eat
                    player.eat(player.level(),edible);
                    //swing
                    entity.swing(InteractionHand.MAIN_HAND);
                    //sound
                    player.level().playSound(player,player.blockPosition(),edible.getItem().getEatingSound(),player.getSoundSource(),1.0f,1.0f);
                    healed++;
                    //cooldown
                    activateCooldown();
                    return;
            }

        }
        //heal by potion
        if(severity>=1){

            //find instant health, regeneration
            var potions=Helper.findPotionItems(maidinventory);
            //filter instant health, regeneration
            potions.removeIf(itemStack ->
                    (!itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.potion\\.healing")&&
                            !itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.potion\\.regeneration")));
            if(!potions.isEmpty()){
                //drink
                //pick randomly
                Random random=new Random();
                int index=random.nextInt(potions.size());
                ItemStack potion=potions.get(index);
                //drink
                player.eat(player.level(),potion);
                healed++;
                //sound
                player.level().playSound(player,player.blockPosition(),potion.getItem().getEatingSound(),player.getSoundSource(),1.0f,1.0f);
                //swing
                entity.swing(InteractionHand.MAIN_HAND);
                //emit eating particle

                //cooldown
                activateCooldown();
                return;
            }
            //find player inventory
            potions=Helper.findPotionItems(playerinventory);
            //filter instant health, regeneration
            potions.removeIf(itemStack ->
                    (!itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.potion\\.healing")&&
                            !itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.potion\\.regeneration")));
            if(!potions.isEmpty()){
                //drink
                //pick randomly
                Random random=new Random();
                int index=random.nextInt(potions.size());
                ItemStack potion=potions.get(index);
                //drink
                player.eat(player.level(),potion);
                //sound
                player.level().playSound(player,player.blockPosition(),potion.getItem().getEatingSound(),player.getSoundSource(),1.0f,1.0f);
                //swing
                entity.swing(InteractionHand.MAIN_HAND);
                healed++;
                //cooldown
                activateCooldown();
                return;
            }

        }

        //heal by food
        if(severity>=0){
            // not satisfied?
            if(player.getFoodData().needsFood()){
                //find edible items from maid inventory
                var edibles=Helper.findEdibleItems(maidinventory);
                //removing by filter golden apple ,spider eye,rotten flesh, poisonous potato
                edibles.removeIf(itemStack ->
                        (itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.golden_apple")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.spider_eye")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.rotten_flesh")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.poisonous_potato")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.pufferfish")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.enchanted_golden_apple")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.suspicious_stew")
                        ));

                if(!edibles.isEmpty()){
                    //eat
                    //pick randomly
                    Random random=new Random();
                    int index=random.nextInt(edibles.size());
                    ItemStack edible=edibles.get(index);

                    //eat
                    player.eat(player.level(),edible);
                    //sound
                    player.level().playSound(player,player.blockPosition(),
                            edible.getItem().getEatingSound(),player.getSoundSource(),1.0f,1.0f);
                    //swing
                    entity.swing(InteractionHand.MAIN_HAND);
                    healed++;
                    //cooldown
                    activateCooldown();
                    return;
                }
                //find player inventory
                edibles=Helper.findEdibleItems(playerinventory);
                //filter golden apple ,spider eye,rotten flesh, poisonous potato
                edibles.removeIf(itemStack ->
                        (itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.golden_apple")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.spider_eye")||
                               itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.rotten_flesh")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.poisonous_potato")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.pufferfish")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.enchanted_golden_apple")||
                                itemStack.getItem().getDescriptionId().matches("item\\.minecraft\\.suspicious_stew")
                        ));

                if(!edibles.isEmpty()){
                    //eat
                    //pick randomly
                    Random random=new Random();
                    int index=random.nextInt(edibles.size());
                    ItemStack edible=edibles.get(index);
                    //eat
                    player.eat(player.level(),edible);
                    //sound
                    player.level().playSound(player,player.blockPosition(),
                            edible.getItem().getEatingSound(),player.getSoundSource(),1.0f,1.0f);
                    //swing
                    entity.swing(InteractionHand.MAIN_HAND);
                    healed++;
                    //cooldown
                    activateCooldown();
                    return;
                }
            }else{
                if(healed>0){
                    //swing
                    success();
                }else{
                    //swing
                    fail("owner is not hungry. cannot heal by food.");
                }
            }
        }
        fail("nothing to do");
    }
}
