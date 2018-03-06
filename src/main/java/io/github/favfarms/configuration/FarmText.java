package io.github.favfarms.configuration;

import io.github.favfarms.FavFarms;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * FavFarms Created by Awesome Red on 8/24/2016.
 */
public class FarmText {

    private FarmText() {}

    private static FarmText instance = new FarmText();

    public static FarmText getInstance() {
        return instance;
    }

    private Plugin farms = FavFarms.getInstance();

    public String getInfo() {
        String about = "?8---------------------Favourite Farms--------------\n"
                + "?3/farm : Info - Favourite Farms\n"
                + "?3/farm reload : Info - Reloads Favourite Farms\n"
                + "?3/farm tool : Info - Gives Player Farm Selection Tool\n"
                + "?3/farm show : Info - Shows Farm Area\n"
                + "?3/farm start <name> : Info - Creates A Farm\n"
                + "?3/farm info <player> : Info - Shows Info About A Players Farm\n"
                + "?3/farm obtain catcher : Info - Gives Player Item To Catch Animals\n"
                + "?3/farm set spawn : Info - Sets The Farm Spawn For A Player\n"
                + "?3/farm remove <name> : Info - Removes Farm For Player Self\n"
                + "?3/farm inv <player> : Info - Opens A Players Farm InventoryGSON\n"
                + "?3/farm animals <player> : Info - Shows Animals A Player Owns\n"
                + "?3Current Version : Info - " + farms.getDescription().getVersion() + "\n"
                + "?8---------------------By: Azewilous----------------\n";
        String aboutTrans = ChatColor.translateAlternateColorCodes('?', about);
        return aboutTrans.trim();
    }
}
