package com.ideagle.plugin.catchballs;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.NamespacedKey;

import static org.bukkit.Bukkit.getServer;

public class BrittleBall extends Catchball {

    public static int TYPE = 2;
    public boolean _used;

    public BrittleBall(NBTEntity nbtEntity) {
        super(nbtEntity);
        _type = TYPE;
        _used = nbtEntity.getCompound("Item").getCompound("tag").getCompound("get_them_all_data").getBoolean("used");
    }

    public BrittleBall(){
        super();
        _type = TYPE;
        _used = false;
    }

    public static void initCraftingRecipe(){
        Catchball.initCraftingRecipe("brittle", new BrittleBall());
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
        display.setString("Name", "{\"text\":\"Brittle ball\",\"color\":\"gray\",\"italic\":true}");
        NBTList<String> lore = display.getStringList("Lore");
        lore.add(0,"{\"text\":\"Only one use\",\"color\":\"red\",\"bold\":true,\"italic\":true}");

        nbti.setInteger("CustomModelData",2);

        return nbti;
    }

    @Override
    public boolean checkDropableOnRelease() { return _mobNbt!=null || !_used; }
}
