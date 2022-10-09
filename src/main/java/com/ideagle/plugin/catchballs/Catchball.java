package com.ideagle.plugin.catchballs;


import com.ideagle.plugin.GetThemAll;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public abstract class Catchball {

    public EntityType _mobType;
    public NBTCompound _mobNbt;
    public int _type;


    public Catchball(NBTEntity nbtEntity){
        NBTCompound nbtData = nbtEntity.getCompound("Item").getCompound("tag").getCompound("get_them_all_data");

        if(nbtData.getCompound("caught_mob_data").hasKey("mob_type")){
            NBTCompound caughtMobNbtData = nbtData.getCompound("caught_mob_data");
            _mobType = EntityType.valueOf(caughtMobNbtData.getString("mob_type"));
            _mobNbt = caughtMobNbtData.getCompound("mob_nbt");
        }
        else{
            _mobType = null;
            _mobNbt = null;
        }
    }

    public Catchball() {

        _mobNbt = null;
        _mobType = null;

    }

    /**
     * Initializes the crafting recipe of specified catchball type
     * @param catchballType the type of the catchball (ex: type of BrittleCatchball is "brittle")
     */
    protected static void initCraftingRecipe(String catchballType, Catchball catchballObject) {

        catchballType = catchballType.toLowerCase();
        FileConfiguration config = GetThemAll._config.getConfig();

        // Get crafted quantity of catchball
        int qtyCrafted = Math.max(config.getInt("recipes.crafted_qty." + catchballType, 1), 1);

        // creating catchball Nbt
        ItemStack catchball = new ItemStack(Material.SNOWBALL, qtyCrafted);
        NBTItem catchballNbt = new NBTItem(catchball);

        catchball = catchballObject.toNBTItem(catchballNbt).getItem();

        ShapedRecipe catchBallRecipe = new ShapedRecipe(NamespacedKey.minecraft(catchballType + "_catchball"), catchball);

        String recipePath = "recipes.crafting_recipes."+catchballType+".";
        String[] rowsPaths = {"top_row", "middle_row", "bottom_row"};

        String[] rowsItems = {"", "", ""};
        ArrayList<Material> MaterialList = new ArrayList<>();


        for (int i = 0; i < rowsPaths.length; i++) {
            // Getting path of a recipe row
            String recipeRowPath = recipePath + rowsPaths[i];

            // Getting string list in recipe row
            List<String> materialStrings = config.getStringList(recipeRowPath);

            for (int j = 0; j < Math.min(3, materialStrings.size()); j++) {
                // If material is recognized as a real material
                try {
                    Material material = Material.valueOf(materialStrings.get(j).toUpperCase().trim().replace(' ','_'));
                    // Updating MaterialList with new material if it wasn't met before
                    if (!MaterialList.contains(material))
                        MaterialList.add(material);

                    // Completing rowsPaths with a material id for crafting recipe
                    rowsItems[i] += MaterialList.indexOf(material);
                }
                // If material isn't recognized as a real material
                catch (IllegalArgumentException e) {
                    rowsItems[i] += " ";
                }
            }

            // If there is less than 3 materials specified in this row,
            // Completing remaining characters with spaces
            if (materialStrings.size() < 3) {
                for (int j = materialStrings.size(); j < 3; j++)
                    rowsItems[i] += " ";
            }
        }

        catchBallRecipe.shape(rowsItems[0], rowsItems[1], rowsItems[2]);

        for (int i = 0; i < MaterialList.size(); i++) {
            char id = (char) (48 + i); //Ascii code for 0 is 48
            catchBallRecipe.setIngredient(id, MaterialList.get(i));
        }
        getServer().addRecipe(catchBallRecipe);

    }

    public abstract boolean checkDropableOnRelease();

    public NBTItem toNBTItem(NBTItem nbtItem){

        NBTCompound gtaDataNBT = nbtItem.addCompound("get_them_all_data");

        gtaDataNBT.setInteger("type", _type);

        NBTCompound caughtMobData = gtaDataNBT.addCompound("caught_mob_data");

        if (_mobType!=null) caughtMobData.setString("mob_type",_mobType.name());

        if (_mobNbt !=null) caughtMobData.addCompound("mob_nbt").mergeCompound(_mobNbt);


        NBTCompound display = nbtItem.addCompound("display");

        NBTList<String> lore = display.getStringList("Lore");

        String mobType = _mobType == null ? "Empty" : _mobType.name().substring(0,1).toUpperCase() + _mobType.name().substring(1).toLowerCase().replace("_"," ");

        lore.add("[{\"text\":\"Contains : \",\"color\":\"gold\",\"italic\":false},{\"text\":\""+mobType+"\",\"color\":\"gray\",\"italic\":false}]");
        if(_mobNbt != null) {
            if (_mobNbt.hasKey("CustomName")) {
                String name = _mobNbt.getString("CustomName");
                name = name.substring((name.indexOf(":\""))+2,name.lastIndexOf("\"}"));
                lore.add("[{\"text\":\" - Name : \",\"color\":\"gold\",\"italic\":false},{\"text\":\"" + name + "\",\"color\":\"white\",\"italic\":false}]");
            }

            if (_mobNbt.hasKey("Owner")){
                String ownerName = Bukkit.getOfflinePlayer(_mobNbt.getUUID("Owner")).getName();
                lore.add("[{\"text\":\" - Owner : \",\"color\":\"gold\",\"italic\":false},{\"text\":\"" + ownerName + "\",\"color\":\"white\",\"italic\":false}]");
            }

            if(_mobNbt.hasKey("variant")){
                String variant = _mobNbt.getString("variant");
                variant = variant.substring(variant.indexOf(":")+1);
                variant = variant.substring(0,1).toUpperCase() + variant.substring(1).toLowerCase();
                variant = variant.replace("_"," ");
                lore.add("[{\"text\":\" - Variant : \",\"color\":\"gold\",\"italic\":false},{\"text\":\"" + variant + "\",\"color\":\"white\",\"italic\":false}]");
            }

            if(_mobType == EntityType.VILLAGER){
                String profession = _mobNbt.getCompound("VillagerData").getString("profession");
                profession = profession.substring(profession.indexOf(":")+1);
                profession = profession.substring(0,1).toUpperCase() + profession.substring(1).toLowerCase();
                lore.add("[{\"text\":\" - Profession : \",\"color\":\"gold\",\"italic\":false},{\"text\":\"" + profession + "\",\"color\":\"white\",\"italic\":false}]");


            }

        }

        return nbtItem;

    }



}
