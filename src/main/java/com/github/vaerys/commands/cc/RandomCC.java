package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.CCommandObject;
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
        Random random = new Random();
        int counter = 0;
        List<CCommandObject> commands = command.guild.customCommands.getCommandList();
        CCommandObject randCC = commands.get(random.nextInt(commands.size()));
        while (!command.channel.settings.contains(CHANNEL_SHITPOST) && randCC.isShitPost() && command.guild.config.shitPostFiltering) {
            if (counter > 25) {
                return "> Your server has way to many shitpost commands, I couldn't find you a normal one.";
            }
            randCC = commands.get(random.nextInt(commands.size()));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;
        }
        String response = randCC.getContents(true);
        for (TagObject t : TagList.getType(TagList.CC)) {
            if (t.name.equals(new TagEmbedImage(0).name)) {
                response = t.handleTag(response + "\n`" + command.guild.config.getPrefixCC() + randCC.getName() + "`", command, args);
            } else {
                response = t.handleTag(response, command, args);
            }
        }
        return response;
    }

    @Override
    public String[] names() {
        return new String[]{"RandomCC", "RandCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you a random cc";
    }

    @Override
    public String usage() {
        return "(args)";
    }

    @Override
    public String type() {
        return TYPE_CC;
    }

    @Override
    public String channel() {
        return null;
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