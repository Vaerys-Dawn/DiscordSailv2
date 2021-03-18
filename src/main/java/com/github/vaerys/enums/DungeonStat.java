package com.github.vaerys.enums;

import emoji4j.EmojiUtils;

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

    private final String emojiName;

    DungeonStat(String emojiName) {
        this.emojiName = emojiName;
    }

    public String getEmoji() {
        return EmojiUtils.getEmoji(emojiName).getEmoji();
    }

}
