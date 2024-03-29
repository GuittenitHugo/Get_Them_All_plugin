package com.ideagle.plugin.handlers;

import com.ideagle.plugin.GetThemAll;
import com.ideagle.plugin.catchballs.BasicBall;
import com.ideagle.plugin.catchballs.BrittleBall;
import com.ideagle.plugin.catchballs.Catchball;
import com.ideagle.plugin.catchballs.PerfectBall;
import de.tr7zw.nbtapi.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class CatchballHandler implements Listener {

    private List<EntityType> _mobs;
    private boolean _isWhiteList;

    public CatchballHandler(GetThemAll plugin) {

        Bukkit.getPluginManager().registerEvents(this,plugin);
        _mobs = new ArrayList<>();
        for (String mobtype : GetThemAll._config.getConfig().getStringList("Mobs.catchable")){
            String type = mobtype.trim().replace(' ', '_').toUpperCase();
            try{
                _mobs.add(EntityType.valueOf(type));
            } catch (IllegalArgumentException ignored){}
        }

        _isWhiteList = GetThemAll._config.getConfig().getBoolean("Mobs.as_whitelist");

    }

    /**
     * Handles catchball hitting an entity or a block, so it can catch or release a mob
     * @param e event assigned to a projectile hitting something
     */
    @EventHandler
    public void onCatchballHit(ProjectileHitEvent e) {
        /* base case : snowball hitting smth event */
        if (e.getEntity().getType() == EntityType.SNOWBALL)
        {
            Projectile snowballProjectile = e.getEntity();
            NBTEntity snowballProjectileNBT = new NBTEntity(snowballProjectile);
            /* checking if it is a get them all catch ball */
            if (isSnowballACatchBall(snowballProjectileNBT)) {
                /* init base data */
                Block hitBlock = e.getHitBlock();
                Entity hitEntity = e.getHitEntity();
                ItemStack snowballItem = new ItemStack(Material.SNOWBALL, 1);
                NBTItem snowballItemNBT = new NBTItem(snowballItem);
                Catchball catchBallData;

                switch (snowballProjectileNBT
                        .getCompound("Item")
                        .getCompound("tag")
                        .getCompound("get_them_all_data")
                        .getInteger("type")){
                    case 1 :
                        catchBallData = new BasicBall(snowballProjectileNBT);
                        break;
                    case 2:
                        catchBallData = new BrittleBall(snowballProjectileNBT);
                        break;
                    case 3:
                        catchBallData = new PerfectBall(snowballProjectileNBT);
                        break;
                    default:
                        catchBallData = new BasicBall(snowballProjectileNBT);
                }

                /* case 1 : ball is empty, capture mob */
                if (!snowballProjectileNBT
                        .getCompound("Item")
                        .getCompound("tag")
                        .getCompound("get_them_all_data")
                        .getCompound("caught_mob_data").hasTag("mob_type")
                ){
                    if (hitEntity instanceof LivingEntity
                            && !(hitEntity instanceof HumanEntity)) {


                        if (((_mobs.contains(hitEntity.getType()) && _isWhiteList)
                                || (!_mobs.contains(hitEntity.getType()) && !_isWhiteList))) {

                            NBTEntity hitEntityNBT = new NBTEntity(hitEntity);

                            /*storing caught mob data*/

                            catchBallData._mobType = hitEntity.getType();
                            NBTContainer container = (NBTContainer) NBT.createNBTObject();
                            List<String> keysToRemove =
                                    Arrays.asList
                                            ("Pos", "Motion",
                                                    "Rotation", "UUID",
                                                    "ActiveEffects", "OnGround",
                                                    "FallDistance", "PortalCooldown",
                                                    "DeathTime", "HurtTime", "Fire",
                                                    "Air", "Leashed", "FallFlying"
                                            );

                            container.mergeCompound(hitEntityNBT);

                            for (String key :
                                    keysToRemove) {
                                container.removeKey(key);
                                if(container.hasTag(key)){
                                    Bukkit.getLogger().warning("failed to remove tag "+key);
                                }
                            }

                            catchBallData._mobNbt = (NBTCompound) NBT.createNBTObject();
                            catchBallData._mobNbt.mergeCompound(container);

                            /*delete unnecessary data*/

                            NBTCompound caughtMobData = snowballItemNBT.addCompound("get_them_all_data").addCompound("caught_mob_data");

                            caughtMobData.setString("mob_type", hitEntity.getType().name());

                            caughtMobData.addCompound("mob_nbt").mergeCompound(caughtMobData);

                            hitEntity.remove();

                            snowballProjectile.getWorld().playSound(snowballProjectile.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, .5f);


                        } else {
                            if (snowballProjectile.getShooter() instanceof Player) {
                                Player shooter;
                                shooter = (Player) snowballProjectile.getShooter();
                                shooter.sendMessage(ChatColor.RED + "You don't have permission to catch this mob.");
                            }

                        }

                    }
                }
                /* case 2 : ball isn't empty, release mob */
                else {

                    if (((_mobs.contains(catchBallData._mobType) && _isWhiteList)
                            || (!_mobs.contains(catchBallData._mobType) && !_isWhiteList))) {

                        assert hitBlock != null || hitEntity != null;

                        if (catchBallData._type == BasicBall.TYPE) {
                            assert catchBallData instanceof BasicBall;
                            ((BasicBall) catchBallData)._useCount++;
                        }
                        if (catchBallData._type == BrittleBall.TYPE) {
                            assert catchBallData instanceof BrittleBall;
                            ((BrittleBall) catchBallData)._used = true;
                        }

                        Location spawnLocation;
                        /* snowball hits a block  */
                        if (hitBlock != null) {
                            assert e.getHitBlockFace() != null;
                            spawnLocation = hitBlock.getRelative(e.getHitBlockFace()).getLocation().add(0.5, 0, 0.5);

                        } else {
                            spawnLocation = snowballProjectile.getLocation();
                        }


                        EntityType entityType = catchBallData._mobType;

                        catchBallData._mobNbt.setUUID("UUID", UUID.randomUUID());

                        Entity releasedEntity;
                        if (hitBlock != null){
                            releasedEntity = hitBlock.getWorld().spawnEntity(spawnLocation, entityType);
                        } else {
                            releasedEntity = hitEntity.getWorld().spawnEntity(spawnLocation, entityType);
                        }

                        NBTEntity freedEntityNBT = new NBTEntity(releasedEntity);

                        freedEntityNBT.mergeCompound(catchBallData._mobNbt);

                        releasedEntity.teleport(spawnLocation);

                        catchBallData._mobType = null;
                        catchBallData._mobNbt = null;

                        snowballProjectile.getWorld().playSound(snowballProjectile.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, .7f);
                    } else {
                        if (snowballProjectile.getShooter() instanceof Player) {
                            Player shooter;
                            shooter = (Player) snowballProjectile.getShooter();
                            shooter.sendMessage(ChatColor.RED + "You don't have permission to release this mob.");
                        }

                    }

                }
                /* getting back the snowball IF empty and has enough durability left */

                if (catchBallData.checkDropableOnRelease()) {

                    snowballItemNBT = catchBallData.toNBTItem(snowballItemNBT);
                    if (catchBallData._mobNbt != null) {

                        snowballItemNBT.setInteger("HideFlags", 1);
                        snowballItem = snowballItemNBT.getItem();
                        snowballItem.addUnsafeEnchantment(Enchantment.SOUL_SPEED, 1);
                    } else {
                        snowballItem = snowballItemNBT.getItem();
                    }
                    snowballProjectile.getWorld().dropItem(snowballProjectile.getLocation(), snowballItem);
                }

                e.setCancelled(true);
                snowballProjectile.teleport(snowballProjectile.getLocation().add(0, Integer.MIN_VALUE, 0));
            }

        }
    }

    private boolean isSnowballACatchBall(@NotNull NBTEntity snowballProjectileNBT){
        if (snowballProjectileNBT.hasTag("Item")) {
            if (snowballProjectileNBT.getCompound("Item").hasTag("tag")) {
                return snowballProjectileNBT.getCompound("Item").getCompound("tag").hasTag("get_them_all_data");
            }
        }
        return false;
    }

}
