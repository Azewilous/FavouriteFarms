package io.github.favfarms.permission;

/**
 * FavouriteFarms Created by Awesome Red on 9/21/2016.
 */
public enum FarmPermissions {

    COMMAND_FARM("favfarms.farm"),
    COMMAND_FARM_MULTIPLE("favfarms.farms.multiple"),
    COMMAND_FARM_MULTIPLEX("favfarms.farms.multiple."),
    COMMAND_FARM_MULTIPLE_UNLIMITED("favfarms.farms.multiple.unlimited"),
    COMMAND_RELOAD("favfarms.reload"),
    COMMAND_TOOL("favfarms.tool"),
    COMMAND_SHOW("favfarms.show"),
    COMMAND_START_FARM("favfarms.create"),
    COMMAND_REMOVE_FARM("favfarms.remove"),
    COMMAND_REMOVE_FARM_OTHER("favfarms.remove.other"),
    COMMAND_INFO_SELF("favfarms.info.self"),
    COMMAND_INFO_OTHERS("favfarms.info.others"),
    COMMAND_OBTAIN_CATCHER("favfarms.obtain.catcher"),
    COMMAND_SET_SPAWN("favfarms.set.spawn"),
    COMMAND_INV_SELF("favfarms.inv.self"),
    COMMAND_INV_OTHER("favfarms.inv.other"),
    COMMAND_FARM_ANIMALS("favfarms.farm.animals"),
    BYPASS_FARM_AREA("favfarms.bypass.area"),
    BYPASS_FARM_CREATE_COOLDOWN("favfarms.bypass.create.cooldown");

    private final String permission;

    FarmPermissions(final String permission) {
        this.permission = permission;
    }

    public String toString() {
        return permission;
    }

}