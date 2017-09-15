package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test implements Command {

    /**
     * Important ass code do not delete plserino
     * //        if (obe.getFirstWord().equalsIgnoreCase("Mention")) {
     * //            if (obe.getRest() != null) {
     * //                IUser user = null;
     * //                SplitFirstObject mentionee = new SplitFirstObject(obe.getRest());
     * //                String toTest = Matcher.quoteReplacement(mentionee.getFirstWord()).replace("_", "[_| ]");
     * //                for (IUser u : command.guild.getProfiles()) {
     * //                    try {
     * //                        if ((u.getName() + "#" + u.getDiscriminator()).matches(toTest)) {
     * //                            user = u;
     * //                        }
     * //                    } catch (PatternSyntaxException e) {
     * //                        //do nothing.
     * //                    }
     * //                }
     * //                try {
     * //                    long uID = Long.parseLong(mentionee.getFirstWord());
     * //                    user = command.client.getUserByID(uID);
     * //                } catch (NumberFormatException e) {
     * //                    if (command.message.getMentions().size() > 0) {
     * //                        user = command.message.getMentions().get(0);
     * //                    }
     * //                }
     * //                if (user != null) {
     * //                    return "> User was found.";
     * //                } else {
     * //                    return "> user could not be found.";
     * //                }
     * //            }
     * //        }
     */

    String nothing = "> Nothing to see here move along.";

    @Override
    public String execute(String args, CommandObject command) {
//        ProfileObject profile = command.guildUsers.getUserByID(command.authorSID);
//        long userXp = profile.getXP();
//        profile.setXp(20000);
//        StringBuilder builder = new StringBuilder();
//        builder.append("> Start test.");
//        for (int i = 1; i < 41; i++) {
//            XpHandler.doDeacyUser(profile, command.guildObject, i);
//            String stats = "\nXp: **" + profile.getXP() + "**, Level: **" + profile.getCurrentLevel() + "**, Day: **" + i + "**, Role Count: **" + command.author.getRolesForGuild(command.guild).size() + "**.";
//            if (builder.length() + stats.length() > 1800) {
//                Utility.sendMessage(builder.toString(), command.channel);
//                builder.delete(0, builder.length());
//            }
//            builder.append(stats);
//        }
//        builder.append("\n> Test Complete.");
//        profile.setXp(userXp);
//        profile.removeLevelFloor();
//        XpHandler.checkUsersRoles(profile.getID(), command.guildObject);
//        return builder.toString();
//        return Utility.getCommandsByType(Globals.getAllCommands(), command, TYPE_DM, true).size() + "";
        throw new DiscordException("Test Exception");
//        return nothing;
    }


    @Override
    public String[] names() {
        return new String[]{"Test", "Testing"};
    }

    @Override
    public String description() {
        return "Tests Things.";
    }

    @Override
    public String usage() {
        return "[Lol this command has no usages XD]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.ADMINISTRATOR};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
