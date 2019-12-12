package com.github.vaerys.commands.adminccs;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;
import java.util.stream.Collectors;

public class ListAdminCCs extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<AdminCCObject> adminCCs = command.guild.adminCCs.getCommands();
        if (adminCCs.size() == 0) return "\\> There currently are no Admin Custom Commands right now, come back later.";
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("\\> Here are all of the available Admin Custom Commands.");
        List<String> commandNames = adminCCs.stream().map(c -> c.getName(command)).collect(Collectors.toList());
        builder.withDesc("```\n" + Utility.listFormatter(commandNames, true) + "```");
        builder.send(command);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ListAdminCCs","AdminCCList"};
    }

    @Override
    public String description(CommandObject command) {
        return "Lists all of the Admin Custom Commands.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public SAILType type() {
        return SAILType.ADMIN_CC;
    }

    @Override
    public ChannelSetting channel() {
        return ChannelSetting.MANAGE_CC;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
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
    public void init() {

    }
}