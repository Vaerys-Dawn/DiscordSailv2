package com.github.vaerys.enums;

/**
 * Created by Vaerys on 14/06/2017.
 */
public enum UserSetting {

    //User Settable
    NO_XP_GAIN("NoXp"), //Allows the user to turn of Xp gain for themselves.
    SEND_LVLUP_CURRENT_CHANNEL("CurrentChannel"), //Sends level up messages to the channel that the message to rank up was made in.
    SEND_LVLUP_RANK_CHANNEL("LevelChannel"), //Will send the Level up to the server set "Rank Chat/Level Up" channel else
    // if no channel exists it will send it to the channel the final message was sent to.
    SEND_LVLUP_DMS("DMs"), //Sends level up messages to Direct messages.
    DONT_SEND_LVLUP("NoLevelMessages"), //makes it so that you don't receive level up messages.
    HIDE_RANK("HideRank"), //allow the user to hide their rank
    NO_LEVEL_UP_REACTIONS("NoLevelUpReactions"),
    PRIVATE_PROFILE("PrivateProfile"), //stops level up reactions from showing up on your posts.

    //Admin Only
    DENIED_XP("DeniedXP"), //Admin Settable. Denies all xp gain for the user.
    DONT_SHOW_LEADERBOARD("DontShowRank"), //Hides user on the server leader-board.
    AUTO_SHITPOST("AutoShitpost"), //Automatically sets all new custom commands made by this user to be shitpost locked.
    DENY_MAKE_CC("DenyMakeCC"), //Removes the ability for the user to make custom commands.
    DENY_USE_CCS("DenyUseCCs"), //Removes the ability for the user to use custom commands.
    DENY_AUTO_ROLE("DenyAutoRole"), //stops the auto role system for sail
    DONT_DECAY("DontDecay"), //stops the automatic role allocation
    DENY_ART_PINNING("DenyArtPinning"), //stop users from pinning art.

    //bot assigned
    HIT_LEVEL_FLOOR("HitLevelFloor"),//added when the user reaches the xp floor of a reward, used to welcome
    // profiles back when the level up again
    READ_RULES("ReadRules"); // user has correctly guessed the guild's rulecode


    private final String text;

    UserSetting(final String text) {
        this.text = text;
    }

    public static UserSetting get(String setting) {
        for (UserSetting s : UserSetting.values()) {
            if (s.text.equalsIgnoreCase(setting))
                return s;
        }
        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}

