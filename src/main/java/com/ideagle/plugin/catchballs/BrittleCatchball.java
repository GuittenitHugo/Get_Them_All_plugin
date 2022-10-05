package com.ideagle.plugin.catchballs;

import com.ideagle.plugin.GetThemAll;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class BrittleCatchball extends Catchball {

    public static int TYPE = 2;
    public boolean _used;

    public BrittleCatchball(NBTEntity nbtEntity) {
        super(nbtEntity);
        _type = TYPE;
        _used = nbtEntity.getCompound("Item").getCompound("tag").getCompound("get_them_all_data").getBoolean("used");
    }

    public BrittleCatchball(){
        super();
        _type = TYPE;
        _used = false;
    }

    public static void initCraftingRecipe(){
        Catchball.initCraftingRecipe("brittle", new BrittleCatchball());
    }

    public static void destroyCraftingRecipe(){
        getServer().removeRecipe(NamespacedKey.minecraft("brittle_catchball")) ;
    }

    @Override
    public NBTItem toNBTItem(NBTItem nbtItem) {
        NBTItem nbti = super.toNBTItem(nbtItem);

        NBTCompound gtaDataNBT = nbtItem.addCompound("get_them_all_data");
        gtaDataNBT.setBoolean("used", _used);

        NBTCompound display = nbti.addCompound("display");
        display.setString("Name", "{\"text\":\"Brittle catch ball\",\"color\":\"gray\",\"italic\":true}");
        NBTList<String> lore = display.getStringList("Lore");
        lore.add(0,"{\"text\":\"Only one use\",\"color\":\"red\",\"bold\":true,\"italic\":true}");

        nbti.setInteger("CustomModelData",2);

        return nbti;
    }

    @Override
    public boolean checkDropableOnRelease() { return _mobNbt!=null || !_used; }
}
