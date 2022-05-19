package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

public class BlacklistUser extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) return missingArgs(command);

        // cast args to IUser to make sure there's no funny business.
        UserObject user = Utility.getUser(command, args, false);
        if (user == null) return "\\> User does not exist.";

        if (command.user.longID == user.longID) return "\\> Why are you trying to blacklist yourself?";

        GlobalData globalData = Globals.getGlobalData();
        if (globalData == null) throw new NullPointerException("GlobalData not loaded!");

        if (globalData.blacklistUser(user.longID, 5) != null)
            return "\\> User **" + user.get().getName() + "#" + user.get().getDiscriminator() + "** blacklisted.";

        return "\\> Failed to blacklist user.";
    }

    @Override
    protected String[] names() {
        return new String[]{"BlacklistUser", "Blacklist"};
    }

    @Override
    public String description(CommandObject command) {
        return "Adds a user to the command blacklist, preventing that person from using any bot commands on any server.";
    }

    @Override
    protected String usage() {
        return "[@User]";
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
    protected Permission[] perms() {
        return new Permission[0];
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
        // does nothing
    }
}
