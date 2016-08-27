package Main;

import java.util.ArrayList;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Globals {

    //CoolDowns
    public static int serverCoolDown = 0;
    public static int doAdminMention = 0;

    //Console Bot message Variables
    public static String consoleMessageCID = "161055776151044096";
    public static String creatorID = "153159020528533505";
    public static String botName = "S.A.I.L v2.0";



    public static ArrayList<GuildCooldowns> cooldowns = new ArrayList<>();

    /**
     * Gets the "GuildCooldowns" for the guild with ID "guildID"
     */
    public static GuildCooldowns getCooldowns(String guildID) {
        GuildCooldowns guildCooldowns = new GuildCooldowns(Constants.GC_NOT_FOUND);
        for (GuildCooldowns gC : cooldowns) {
            if (gC.guildID.equals(guildID)) {
                guildCooldowns = gC;
            }
        }
        return guildCooldowns;
    }

    /**
     * Adds a "GuildCoolDowns" to the ArrayList "cooldowns" for the guild with ID "guildID"
     */
    public static void addCooldown(String guildID) {
        cooldowns.add(new GuildCooldowns(guildID));
    }

}
