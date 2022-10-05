package com.ideagle.plugin.handlers;

import com.ideagle.plugin.GetThemAll;
import com.ideagle.plugin.catchballs.BasicCatchball;
import com.ideagle.plugin.catchballs.BrittleCatchball;
import com.ideagle.plugin.catchballs.Catchball;
import com.ideagle.plugin.catchballs.PerfectCatchball;
import de.tr7zw.nbtapi.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                        catchBallData = new BasicCatchball(snowballProjectileNBT);
                        break;
                    case 2:
                        catchBallData = new BrittleCatchball(snowballProjectileNBT);
                        break;
                    case 3:
                        catchBallData = new PerfectCatchball(snowballProjectileNBT);
                        break;
                    default:
                        catchBallData = new BasicCatchball(snowballProjectileNBT);
                }


                if (snowballProjectile.getShooter() instanceof Player){
                    Entity shooter = (Entity) snowballProjectile.getShooter();

                    /* case 1 : snowball hit a living non player entity (catch the mob if none inside snowball) */
                    if (hitEntity instanceof LivingEntity
                            && !(hitEntity instanceof HumanEntity)) {


                        if (((_mobs.contains(hitEntity.getType()) && _isWhiteList)
                                || (!_mobs.contains(hitEntity.getType()) && !_isWhiteList))
                                || shooter.hasPermission("getthemall.catchball.catchforbiddenmob")) {
                            NBTEntity hitEntityNBT = new NBTEntity(hitEntity);

                            if (!snowballProjectileNBT
                                    .getCompound("Item")
                                    .getCompound("tag")
                                    .getCompound("get_them_all_data")
                                    .getCompound("caught_mob_data").hasKey("mob_type")
                            ) {
                                /*storing caught mob data*/

                                catchBallData._mobType = hitEntity.getType();
                                catchBallData._mobNbt = hitEntityNBT;

                                NBTCompound caughtMobData = snowballItemNBT.addCompound("get_them_all_data").addCompound("caught_mob_data");

                                caughtMobData.setString("mob_type", hitEntity.getType().name());

                                caughtMobData.addCompound("mob_nbt").mergeCompound(hitEntityNBT);

                                hitEntity.remove();

                                snowballProjectile.getWorld().playSound(snowballProjectile.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, .5f);
                            }

                        } else {
                            if (snowballProjectile.getShooter() instanceof Player) {
                                shooter = (Player) snowballProjectile.getShooter();
                                shooter.sendMessage(ChatColor.RED + "You don't have permission to catch this mob.");
                            }

                        }

                    }

                    /* case 2 : snowball hits a block (make the mob appear if any inside snowball) */
                    if (hitBlock != null && catchBallData._mobType != null) {


                        if (((_mobs.contains(catchBallData._mobType) && _isWhiteList)
                                || (!_mobs.contains(catchBallData._mobType) && !_isWhiteList))
                                || shooter.hasPermission("getthemall.catchball.releaseforbiddenmob")) {

                            if (catchBallData._type == BasicCatchball.TYPE)
                                ((BasicCatchball) catchBallData)._useCount++;
                            if (catchBallData._type == BrittleCatchball.TYPE)
                                ((BrittleCatchball) catchBallData)._used = true;

                            Location spawnLocation = hitBlock.getLocation().add(0.5, 1, 0.5);

                            EntityType entityType = catchBallData._mobType;

                            catchBallData._mobNbt.setUUID("UUID", UUID.randomUUID());

                            Entity freedEntity = hitBlock.getWorld().spawnEntity(spawnLocation, entityType);

                            NBTEntity freedEntityNBT = new NBTEntity(freedEntity);

                            NBTList<Double> Motion = catchBallData._mobNbt.getDoubleList("Pos");

                            for (Double motionCoord :
                                    Motion) {
                                motionCoord = 0.;
                            }

                            catchBallData._mobNbt.getDoubleList("Pos");

                            catchBallData._mobNbt.setFloat("FallDistance", 0f);

                            freedEntityNBT.mergeCompound(catchBallData._mobNbt);

                            freedEntity.teleport(spawnLocation);

                            catchBallData._mobType = null;
                            catchBallData._mobNbt = null;

                            snowballProjectile.getWorld().playSound(snowballProjectile.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, .7f);
                        } else {
                            if (snowballProjectile.getShooter() instanceof Player) {
                                shooter = (Player) snowballProjectile.getShooter();
                                shooter.sendMessage(ChatColor.RED + "You don't have permission to release this mob.");
                            }

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
        if (snowballProjectileNBT.hasKey("Item")) {
            if (snowballProjectileNBT.getCompound("Item").hasKey("tag")) {
                return snowballProjectileNBT.getCompound("Item").getCompound("tag").hasKey("get_them_all_data");
            }
        }
        return false;
    }

}
