package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class UnBlacklistUser extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) return missingArgs(command);

        // Attempt to get an IUser so we know our thing is real.
        IUser user = Utility.getUser(command, args, false).get();
        if (user == null) return "> Could not find user.";

        GlobalData globalData = Globals.getGlobalData();
        if (globalData == null) throw new NullPointerException("GlobalData not loaded yet!");
        if (globalData.getBlacklistedUsers().removeIf(o -> o.getUserID() == user.getLongID())) {
            return "> User **" + user.getName() + "#" + user.getDiscriminator() + "** un-blacklisted.";
        }
        return "> User **" + user.getName() + "#" + user.getDiscriminator() + "** could not be removed from blacklist or is not blacklisted.";
    }

    @Override
    protected String[] names() {
        return new String[]{"UnBlacklistUser", "UnBlacklist"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes a user from the command blacklist";
    }

    @Override
    protected String usage() {
        return "[@user]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    protected void init() {

    }
}
