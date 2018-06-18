package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.cctags.TagEmbedImage;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;
import java.util.Random;

public class RandomCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.channelHasSetting(ChannelSetting.CC_DENIED,command.channel.longID)) {
            return "> Custom Command usage has been disabled for this channel.";
        }
        Random random = new Random();
        int counter = 0;
        List<CCommandObject> commands = command.guild.customCommands.getCommandList();
        CCommandObject randCC = commands.get(random.nextInt(commands.size()));
        while (!command.channel.settings.contains(ChannelSetting.SHITPOST) && randCC.isShitPost() && command.guild.config.shitPostFiltering) {
            if (counter > 25) {
                return "> Your server has way to many shitpost commands, I couldn't find you a normal one.";
            }
            randCC = commands.get(random.nextInt(commands.size()));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
            }
            counter++;
        }
        String response = randCC.getContents(true);
        for (TagObject t : TagList.getType(TagType.CC)) {
            if (t.name.equals(new TagEmbedImage(0).name)) {
                response = t.handleTag(response + "\n`" + command.guild.config.getPrefixCC() + randCC.getName() + "`", command, args);
            } else {
                response = t.handleTag(response, command, args);
            }
        }
        return response;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you a random cc";
    }

    @Override
    protected String[] names() {
        return new String[]{"RandomCC", "RandCC"};
    }

    @Override
    protected String usage() {
        return "(args)";
    }

    @Override
    protected SAILType type() {
        return SAILType.CC;
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
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
