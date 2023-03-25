package com.ideagle.plugin.catchballs;

import com.ideagle.plugin.GetThemAll;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.NamespacedKey;

import static org.bukkit.Bukkit.getServer;

public class BasicBall extends Catchball {

    public static int TYPE = 1;
    public int _useCount;

    public BasicBall(NBTEntity nbtEntity) {
        super(nbtEntity);
        NBTCompound nbtData = nbtEntity.getCompound("Item").getCompound("tag").getCompound("get_them_all_data");
        _useCount = nbtData.getInteger("use_count");
        _type = TYPE;
    }

    public BasicBall(){
        super();
        _useCount=0;
        _type = TYPE;
    }

    public static void initCraftingRecipe(){
        Catchball.initCraftingRecipe("basic", new BasicBall());

    }

    public static void destroyCraftingRecipe(){
        getServer().removeRecipe(NamespacedKey.minecraft("basic_catchball"));
    }

    @Override
    public NBTItem toNBTItem(NBTItem nbtItem) {
        NBTItem nbti = super.toNBTItem(nbtItem);

        NBTCompound gtaDataNBT = nbtItem.addCompound("get_them_all_data");
        gtaDataNBT.setInteger("use_count", _useCount);

        NBTCompound display = nbti.addCompound("display");
        display.setString("Name", "{\"text\":\"Basic ball\",\"color\":\"white\",\"italic\":false}");
        NBTList<String> lore = display.getStringList("Lore");
        lore.add(0,"[{\"text\":\"Uses left : \",\"color\":\"gold\",\"italic\":false},{\"text\":\""+( GetThemAll._config.getConfig().getInt("max_uses")-_useCount)+"\",\"color\":\"white\",\"italic\":false}]");

        nbti.setInteger("CustomModelData",1);

        return nbti;
    }

    @Override
    public boolean checkDropableOnRelease() { return _useCount < GetThemAll._config.getConfig().getInt("max_uses"); }
}
