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
        throw new DiscordException("TestException");
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
