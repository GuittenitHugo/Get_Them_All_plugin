package com.ideagle.plugin.catchballs;

import com.ideagle.plugin.GetThemAll;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import static org.bukkit.Bukkit.getServer;

public class PerfectCatchball extends Catchball {

    public static int TYPE = 3;

    public PerfectCatchball(NBTEntity nbtEntity) {
        super(nbtEntity);
        _type = TYPE;
    }

    public PerfectCatchball(){
        super();
        _type = TYPE;
    }

    public static void initCraftingRecipe(){
        Catchball.initCraftingRecipe("perfect", new PerfectCatchball());
    }

    public static void destroyCraftingRecipe(){
        getServer().removeRecipe(NamespacedKey.minecraft("perfect_catchball"));
    }

    @Override
    public NBTItem toNBTItem(NBTItem nbtItem) {
        NBTItem nbti = super.toNBTItem(nbtItem);

        NBTCompound display = nbti.addCompound("display");
        display.setString("Name", "{\"text\":\"Perfect catch ball\",\"color\":\"aqua\",\"bold\":true,\"italic\":false}");
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
