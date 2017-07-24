package Enums;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class EnumString {

    public static String get(UserSetting setting) {
        switch (setting) {
            case SEND_LVLUP_RANK_CHANNEL:
                return "LevelChannel";
            case SEND_LVLUP_CURRENT_CHANNEL:
                return "CurrentChannel";
            case SEND_LVLUP_DMS:
                return "DMs";
            case DONT_SEND_LVLUP:
                return "NoMessage";
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
                return "DeniedCreateCC";
            case DENY_USE_CCS:
                return "DeniedUseCCs";
            case HIT_LEVEL_FLOOR:
                return "HitLevelFloor";
            default:
                return null;
        }
    }
}
