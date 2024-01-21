package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class EventNotificationGoal<T extends TamableAnimal> extends AIGoalBase{
    protected T entity;

    private HashMap<String,Integer> itemMap ;

    private final ArrayList<String> eventList = new ArrayList<>();
    private final int eventLimit=32;
    private int eventTimer=0;
    private int cooldown;
    private int firsttouchcooldown;
    public EventNotificationGoal(T entity) {
        this.entity = entity;
        cooldown=0;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canUse();
    }
    public void addEvent(String message){
        eventList.add(message);

        while(eventList.size()>eventLimit) {
            //remove old event
            eventList.remove(0);
        }
        if(firsttouchcooldown!=0){
            eventTimer=Math.max(20,200-5*eventList.size());
            firsttouchcooldown=1200;
        }

    }
    public void fireEvent(){
        if(eventList.size()>0){
            String message = eventList.stream().reduce("",(a,b)->a+b);
            LMMChat.addChatMessage(null,entity,true,false,message,0);
            eventTimer=0;
            cooldown=LMMChatConfig.getEventNotificationCooldown();
            eventList.clear();
            firsttouchcooldown=1200;
        }
    }
    private  HashMap<String,Integer> getItemCountMap(){
        HashMap<String,Integer> itemMap = new HashMap<>();
        Container inventory = Helper.getInventoryContainer(entity);
        for(int i=0;i<inventory.getContainerSize();i++){
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack.isEmpty())continue;
            String itemName = itemStack.getItem().getDescriptionId();

            if(itemMap.containsKey(itemName)){
                itemMap.put(itemName,itemMap.get(itemName)+itemStack.getCount());
            }else{
                itemMap.put(itemName,itemStack.getCount());
            }
        }
        return itemMap;
    }
    @Override
    public void tick(){

        if(eventList.size()>0){
            if(eventTimer>0){
                eventTimer--;
            }else{
                if(cooldown==0) {
                    fireEvent();
                }
            }
        }
        if(firsttouchcooldown>0){
            firsttouchcooldown--;
        }
        if(cooldown>0){
            cooldown--;
        }
        //manual event checking

        if(entity.tickCount%100==0){
            //check changes
            var newItemMap=getItemCountMap();
            if(itemMap==null){
                itemMap = newItemMap;
            }else if(!itemMap.equals(newItemMap)){
                //send message
                //enumerate items
                for(var item:newItemMap.entrySet()){
                    if(itemMap.containsKey(item.getKey())){
                        if(itemMap.get(item.getKey())!=item.getValue()){
                            addEvent("item "+item.getKey()+" changed from "+itemMap.get(item.getKey())+" to "+item.getValue()+"\n");
                        }
                    }else{
                        addEvent("item "+ item.getValue()+" "+item.getKey()+" added\n");
                    }
                }
                //check remove
                for(var item:itemMap.entrySet()){
                    if(!newItemMap.containsKey(item.getKey())){
                        addEvent("item "+ item.getValue()+" "+item.getKey()+" removed\n");
                    }
                }
                itemMap = newItemMap;
            }

        }
    }
    public void onGetProjectile (net.minecraftforge.event.entity.living.LivingGetProjectileEvent event){
        Entity projectile = event.getEntity();
        addEvent("you get a projectile :"+projectile.getDisplayName().getString()+"\n");
    }

    public void onHealEntity(net.minecraftforge.event.entity.living.LivingHealEvent event){
        // maids are frequently healed. so this event is not notified.

        //addEvent("you are healed hp +"+(int)event.getAmount()+"\n");

    }

    public void onHurtEntity(net.minecraftforge.event.entity.living.LivingHurtEvent event){
        Entity attacker = event.getSource().getEntity();
        if(attacker!=null){
            addEvent("you are attacked by "+attacker.getDisplayName().getString()+" hp -"+ (int)event.getAmount() +"\n");
        }else{
            addEvent("you are damaged hp -"+(int)event.getAmount()+"\n");
        }
    }
    public void onHurtOwner(net.minecraftforge.event.entity.living.LivingHurtEvent event){
        Entity attacker = event.getSource().getEntity();
        if(attacker!=null){
            addEvent("your owner is attacked by "+attacker.getDisplayName().getString()+" hp -"+ (int)event.getAmount() +"\n");
        }else{
            addEvent("your owner is damaged hp -"+(int)event.getAmount()+"\n");
        }
    }
    public void onUsedTotem(net.minecraftforge.event.entity.living.LivingUseTotemEvent event){
        addEvent("you have taken a fatality damage. you used and survived by a totem of undying.\n");
    }
    public void onKilled(net.minecraftforge.event.entity.living.LivingDeathEvent event){
        DamageSource source = event.getSource();
        if(source!=null){
            Entity attacker = source.getEntity();
            if(attacker!=null){
                addEvent("you killed "+event.getEntity().getDisplayName().getString()+".\n");
            }
        }
    }

    public void onExplodedEntity(ExplosionEvent.Detonate event){
        if(event.getAffectedEntities().contains(entity)){
            // hit explosion
            // has source?
            if(event.getExplosion()!=null) {
                //has expoloder
                if(event.getExplosion().getExploder() != null) {
                    addEvent("you are hit by explosion from " + event.getExplosion().getExploder().getDisplayName().getString() +". pos:"+ event.getExplosion().getPosition() + "\n");
                } else {
                    addEvent("you are hit by explosion from " + event.getExplosion().getPosition() + ".\n");
                }
            }
        }
    }
    public void onPlayerFishedItem(net.minecraftforge.event.entity.player.ItemFishedEvent event){
        addEvent(event.getEntity().getDisplayName().getString()+" fished "+event.getDrops().get(0).getDisplayName().getString()+".\n");
    }


    public void onEnderManAngered(net.minecraftforge.event.entity.living.EnderManAngerEvent event){
        // addEvent("you heard a horrible scream from "+event.getEntity().getDisplayName().getString()+".\n");
    }

    public void onVillageSieged(net.minecraftforge.event.village.VillageSiegeEvent event){
        addEvent("nearby village is under attack.\n");
    }

    public void onZombieAid(net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent event){
        addEvent("you heard zombies scream. you felt many zombies are coming.\n");
    }
}
