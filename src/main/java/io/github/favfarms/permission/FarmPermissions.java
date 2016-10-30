package io.github.favfarms.permission;

/**
 * FavouriteFarms Created by Awesome Red on 9/21/2016.
 */
public enum FarmPermissions {

    COMMAND_FARM("favfarms.farm"),
    COMMAND_FARM_MULTIPLE("favfarms.farm.multiple"),
    COMMAND_FARM_MULTIPLEX("favfarms.farm.multiple."),
    COMMAND_FARM_MULTIPLE_UNLIMITED("favfarms.farm.multiple.unlimited"),
    COMMAND_RELOAD("favfarms.reload"),
    COMMAND_TOOL_SELF("favfarms.tool.self"),
    COMMAND_SHOW_SELF("favfarms.show.self"),
    COMMAND_SHOW_OTHER("favfarms.show.other"),
    COMMAND_START_FARM("favfarms.farm.start"),
    COMMAND_START_FARM_OTHER("favfarms.create.other"),
    COMMAND_FARM_HOME_SELF("favfarms.home.self"),
    COMMAND_FARM_HOME_OTHER("favfarms.home.other"),
    COMMAND_REMOVE_FARM("favfarms.farm.remove"),
    COMMAND_REMOVE_FARM_OTHER("favfarms.remove.other"),
    COMMAND_LIST_FARMS("favfarms.list"),
    COMMAND_INFO_SELF("favfarms.info.self"),
    COMMAND_INFO_OTHERS("favfarms.info.others"),
    COMMAND_OBTAIN_CATCHER("favfarms.catcher.self"),
    COMMAND_OBTAIN_CATCHER_OTHER("favfarms.catcher.other"),
    COMMAND_OBTAIN_RESET("favfarms.catcher.reset"),
    COMMAND_SET_SPAWN("favfarms.set.spawn"),
    COMMAND_INV_SELF("favfarms.inv.self"),
    COMMAND_INV_OTHER("favfarms.inv.other"),
    COMMAND_FARM_ANIMALS("favfarms.farm.animals"),
    COMMAND_SET_TAMES("favfarms.set.tames"),
    COMMAND_SET_REPLENISHES("favfarms.set.replenishes"),
    COMMAND_SET_STYLECHANGES("favfarms.set.stylechnages"),
    COMMAND_ADD_TAMES("favfarms.add.tames"),
    COMMAND_ADD_REPLENISHES("favfarms.add.replenishes"),
    COMMAND_ADD_STYLECHANGES("favfarms.add.stylechanges"),
    COMMAND_CHECK_ANIMALS("favfarms.check.animals"),
    BYPASS_FARM_AREA("favfarms.bypass.area"),
    BYPASS_FARM_SIZE_LIMIT("favfarms.bypass.size.limit"),
    BYPASS_FARM_CREATE_COOLDOWN("favfarms.bypass.create.cooldown"),
    BYPASS_FARM_CATCHER_COOLDOWN("favfarms.bypass.catcher.cooldown"),
    BYPASS_FARM_COMMAND_COOLDOWN("favfarms.bypass.command.cooldown");

    private final String permission;

    FarmPermissions(final String permission) {
        this.permission = permission;
    }

    public String toString() {
        return permission;
    }

}