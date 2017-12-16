package com.github.vaerys.main;

/**
 * Created by Vaerys on 14/06/2017.
 */
public enum UserSetting {

    //User Settable
    NO_XP_GAIN, //Allows the user to turn of Xp gain for themselves.
    SEND_LVLUP_CURRENT_CHANNEL, //Sends level up messages to the channel that the message to rank up was made in.
    SEND_LVLUP_RANK_CHANNEL, //Will send the Level up to the server set "Rank Chat/Level Up" channel else
    // if no channel exists it will send it to the channel the final message was sent to.
    SEND_LVLUP_DMS, //Sends level up messages to Direct messages.
    DONT_SEND_LVLUP, //makes it so that you don't receive level up messages.
    HIDE_RANK, //allow the user to hide their rank
    NO_LEVEL_UP_REACTIONS,
    PRIVATE_PROFILE, //stops level up reactions from showing up on your posts.

    //Admin Only
    DENIED_XP, //Admin Settable. Denies all xp gain for the user.
    DONT_SHOW_LEADERBOARD, //Hides user on the server leader-board.
    AUTO_SHITPOST, //Automatically sets all new custom commands made by this user to be shitpost locked.
    DENY_MAKE_CC, //Removes the ability for the user to make custom commands.
    DENY_USE_CCS, //Removes the ability for the user to use custom commands.
    DENY_AUTO_ROLE, //stops the auto role system for sail
    DONT_DECAY, //stops the automatic role allocation
    DENY_ART_PINNING, //stop users from pinning art.

    //bot assigned
    HIT_LEVEL_FLOOR;//added when the user reaches the xp floor of a reward, used to welcome
    // profiles back when the level up again

    public static UserSetting get(String setting){
        switch (setting.toLowerCase()) {
            case "levelchannel":
                return SEND_LVLUP_RANK_CHANNEL;
            case "currentchannel":
                return SEND_LVLUP_CURRENT_CHANNEL;
            case "dms":
                return SEND_LVLUP_DMS;
            case "nolevelmessages":
                return DONT_SEND_LVLUP;
            case "noxp":
                return NO_XP_GAIN;
            case "hiderank":
                return HIDE_RANK;
            case "deniedxp":
                return DENIED_XP;
            case "dontshowrank":
                return DONT_SHOW_LEADERBOARD;
            case "autoshitpost":
                return AUTO_SHITPOST;
            case "denymakecc":
                return DENY_MAKE_CC;
            case "denyuseccs":
                return DENY_USE_CCS;
            case "hitlevelfloor":
                return HIT_LEVEL_FLOOR;
            case "nolevelupreactions":
                return NO_LEVEL_UP_REACTIONS;
            case "privateprofile":
                return PRIVATE_PROFILE;
            case "dontdecay":
                return DONT_DECAY;
            case "denyartpinning":
                return DENY_ART_PINNING;
            default:
                return null;
        }
    }

    public static String get(UserSetting setting){
        switch (setting) {
            case SEND_LVLUP_RANK_CHANNEL:
                return "LevelChannel";
            case SEND_LVLUP_CURRENT_CHANNEL:
                return "CurrentChannel";
            case SEND_LVLUP_DMS:
                return "DMs";
            case DONT_SEND_LVLUP:
                return "NoLevelMessage";
            case NO_XP_GAIN:
                return "NoXP";
            case HIDE_RANK:
                return "HideRank";
            case DENIED_XP:
                return "DeniedXP";
            case DONT_SHOW_LEADERBOARD:
                return "DontShowRank";
            case AUTO_SHITPOST:
                return "AutoShitpost";
            case DENY_MAKE_CC:
                return "DenyMakeCC";
            case DENY_USE_CCS:
                return "DenyUseCCs";
            case HIT_LEVEL_FLOOR:
                return "HitLevelFloor";
            case NO_LEVEL_UP_REACTIONS:
                return "NoLevelUpReactions";
            case PRIVATE_PROFILE:
                return "PrivateProfile";
            case DENY_AUTO_ROLE:
                return "DenyAutoRole";
            case DONT_DECAY:
                return "DontDecay";
            case DENY_ART_PINNING:
                return "DenyArtPinning";
            default:
                return null;
        }
    }
}

