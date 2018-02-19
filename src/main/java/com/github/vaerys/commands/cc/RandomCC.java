package com.github.vaerys.commands.cc;

import java.util.List;
import java.util.Random;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.cctags.TagEmbedImage;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.templates.TagType;
import sx.blah.discord.handle.obj.Permissions;

public class RandomCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
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
    public void init() {

    }

    protected static final String[] ALIASES = new String[] {"RandomCC", "RandCC"};
    @Override
    protected String[] names() {
        return ALIASES;
    }

    protected static final String USAGE = "(args)";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CC;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRE_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRE_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }
}
