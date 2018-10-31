package com.github.vaerys.enums;

import com.github.vaerys.main.Utility;

public enum DungeonStat {

    HEALTH("heart"),
    ARMOUR_CLASS("shield"),
    INITIATIVE("bangbang"),

    STRENGTH("muscle"),
    DEXTERITY("bow_and_arrow"),
    CONSTITUTION("green_apple"),
    INTELLIGENCE("bulb"),
    WISDOM("eye"),
    CHARISMA("performing_arts");

    private String emojiName;

    DungeonStat(String emojiName) {
        this.emojiName = emojiName;
    }

    public String getEmoji() {
        return Utility.getUnicodeEmoji(emojiName);
    }

}
