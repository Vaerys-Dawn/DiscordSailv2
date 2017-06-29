package Handlers;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Globals;
import Objects.GuildContentObject;
import Objects.UserTypeObject;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vaerys on 29/06/2017.
 */
public class XpHandler {

    public static void doDecay(GuildContentObject content, ZonedDateTime nowUTC) {
        if (content.getGuildConfig().doXpDecay) {
            for (UserTypeObject u : content.getGuildUsers().getUsers()) {
                long diff = nowUTC.toEpochSecond() - u.getLastTalked();
                long days = TimeUnit.DAYS.toDays(diff);
                float temp = 0;
                long decay;
                //modifiable min and max decay days needs to be implemented.
                if (days > 7 && days < 30) {
                    //normal xp decay formula
                    temp = (days - 7) * (Globals.avgMessagesPerDay * Globals.baseXPModifier * content.getGuildConfig().xpModifier) / 4;
                } else if (days > 30) {
                    //plateau xp decay
                    temp = (30 - 7) * (Globals.avgMessagesPerDay * Globals.baseXPModifier * content.getGuildConfig().xpModifier) / 4;
                }
                decay = (long) temp;
                // calc xp for lowest reward, if lower don't decay.
                // if higher do decay until xp for highest obtained role -400
            }
        }
    }

    public static void grantXP(CommandObject object) {
        //bots don't get XP
        if (object.author.isBot()) return;

        //creates a profile for the user if they dont already have one.
        UserTypeObject user = new UserTypeObject(object.authorSID);
        if (object.guildUsers.getUserByID(object.authorSID) == null) {
            object.guildUsers.getUsers().add(user);
        } else {
            user = object.guildUsers.getUserByID(object.authorSID);
        }

        user.lastTalked = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
        //you can only gain xp once per min
        if (object.guildContent.getSpokenUsers().contains(object.authorID)) return;

        //messages that might be considered commands should be ignored.
        if (object.message.getContent().startsWith(object.guildConfig.getPrefixCommand())) return;
        if (object.message.getContent().startsWith(object.guildConfig.getPrefixCC())) return;

        //you must have typed at least 10 chars to gain xp
        if (object.message.getContent().length() < 10) return;

        //you cannot gain xp in an xpDenied channel
        ArrayList<String> xpChannels = object.guildConfig.getChannelIDsByType(Command.Channel_XP_DENIED);
        if (xpChannels != null && xpChannels.size() > 0) {
            if (xpChannels.contains(object.channelSID)) return;
        }

        //gives them their xp.
        user.addXP(object.guildConfig);
    }
}

