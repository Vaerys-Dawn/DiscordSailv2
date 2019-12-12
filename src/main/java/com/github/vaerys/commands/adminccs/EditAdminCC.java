package com.github.vaerys.commands.adminccs;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class EditAdminCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject object = new SplitFirstObject(args);

        AdminCCObject cc = command.guild.adminCCs.getCommand(object.getFirstWord());
        if (cc == null) return "\\> Could not find any admin custom commands with that name.";

        StringHandler contents = NewAdminCC.getContents(command, object.getRest());
        NewAdminCC.ResponseCode response = NewAdminCC.testContents(command, contents, cc);

        switch (response) {
            case EMPTY_CONTENTS:
                return "\\> Cannot edit Admin CC, Contents is empty.";
            case TOO_MANY_EMBEDS:
                return "\\> Cannot edit Admin CC, Contents contains more than one <embedImage> tag.";
            case NOT_ENOUGH_SLOTS:
                return "\\> This message should never run, but if you did get this message it means you somehow have gone over the slot limit for Admin Custom Commands.";
            case OVERLOADS_SLOTS:
                return "\\> There are not enough slots left for you to edit this Admin Custom Command. The new length of the command is too long.";
        }

        cc.setContents(contents.toString());
        int remainder = (20 - command.guild.adminCCs.getUsedSlots());
        return String.format("\\> Admin custom command contents updated. (%d Admin CC slot%s remain)", remainder, remainder != 1 ? "s" : "");
    }

    @Override
    protected String[] names() {
        return new String[]{"EditAdminCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for editing of Admin Custom Commands.\n" +
                "[Example Tag usages.](https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/AdminCC-Tag-Examples)";
    }

    @Override
    protected String usage() {
        return "[Command Name] [Contents/File.txt]";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN_CC;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER, Permissions.MANAGE_ROLES, Permissions.MANAGE_MESSAGES};
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
