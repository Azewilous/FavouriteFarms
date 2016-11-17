package io.github.favfarms.ability;

/**
 * FavouriteFarms Created by Awesome Red on 10/19/2016.
 */
public enum Abilities {

    SNOW_BLOWER(0),
    SPRINKLER(1),
    SHIELD(2),
    EXPLOSIVE(3);

    private int id;

    Abilities(int id) {
        this.id = id;
    }

    public Integer getAbilityId() {
        return id;
    }

}
