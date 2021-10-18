package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class Present extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (!command.guild.config.modulePixels && !command.guild.config.xpGain) return "\\> Cannot give gift right now :C";
        Message message = command.guildChannel.sendMessage("\\> Click Me :D");
        message.addReaction(Utility.getReaction("gift").getEmote()).complete();
        Globals.getGlobalData().setPresentId(message.getIdLong());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"Present"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives a present";
    }

    @Override
    protected String usage() {
        return null;
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
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    protected void init() {

    }
}
