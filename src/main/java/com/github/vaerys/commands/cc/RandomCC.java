package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.cctags.TagEmbedImage;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.Permission;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_USE_CCS)) {
            return "\\> Nothing interesting happens. `(ERROR: 403)`";
        }
        if (command.guild.channelHasSetting(ChannelSetting.CC_DENIED, command.guildChannel.longID)) {
            return "\\> Custom Command usage has been disabled for this messageChannel.";
        }
        Random random = Globals.getGlobalRandom();
        List<CCommandObject> commands = command.guild.customCommands.getCommandList().stream()
                .filter(c -> !command.guildChannel.settings.contains(ChannelSetting.SHITPOST) && c.isShitPost() && command.guild.config.shitPostFiltering)
                .collect(Collectors.toList());
        if (commands.isEmpty()) return "\\> Could not find any non shitpost commands for this channel...";
        CCommandObject randCC = commands.get(random.nextInt(commands.size()));
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
    protected Permission[] perms() {
        return new Permission[0];
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
        // does nothing
    }
}
