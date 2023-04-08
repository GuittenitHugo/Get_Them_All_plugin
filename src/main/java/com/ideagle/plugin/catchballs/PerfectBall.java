package com.ideagle.plugin.catchballs;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.NamespacedKey;

import static org.bukkit.Bukkit.getServer;

public class PerfectBall extends Catchball {

    public static int TYPE = 3;

    public PerfectBall(NBTEntity nbtEntity) {
        super(nbtEntity);
        _type = TYPE;
    }

    public PerfectBall(){
        super();
        _type = TYPE;
    }

    public static void initCraftingRecipe(){
        Catchball.initCraftingRecipe("perfect", new PerfectBall());
    }

    public static void destroyCraftingRecipe(){
        getServer().removeRecipe(NamespacedKey.minecraft("perfect_catchball"));
    }

    @Override
    public NBTItem toNBTItem(NBTItem nbtItem) {
        NBTItem nbti = super.toNBTItem(nbtItem);

        NBTCompound display = nbti.addCompound("display");
        display.setString("Name", "{\"text\":\"Perfect ball\",\"color\":\"aqua\",\"bold\":true,\"italic\":false}");
        NBTList<String> lore = display.getStringList("Lore");
        lore.add(0,"{\"text\":\"Unlimited uses\",\"color\":\"white\",\"italic\":false}");

        nbti.setInteger("CustomModelData",3);

        return nbti;
    }

    @Override
    public boolean checkDropableOnRelease() {
        return true;
    }
}
