package com.github.vaerys.commands.characters;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CharacterObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class CharAvatar extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        CharacterObject charObject = command.guild.characters.getCharByName(args.split(" ")[0]);
        if (charObject == null) return "\\> Could not find any characters with that Character ID.";
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle(charObject.getEffectiveName());
        Member user = command.guild.getUserByID(charObject.getUserID());
        if (user == null) {
            builder.setFooter("Author: No longer on this server | Character ID: " + charObject.getName());
        } else {
            builder.setFooter("Author: " + user.getEffectiveName() + " | Character ID: " + charObject.getName());
        }
        command.guild.characters.validateRoles(command.guild.get());
        if (charObject.getRoleIDs().size() != 0) {
            builder.setColor(charObject.getColor(command.guild));
        }
        if (charObject.getAvatarURL() == null || charObject.getAvatarURL().isEmpty())
            return "\\> " + charObject.getEffectiveName() + " Does not have an avatar set up.";
        builder.setImage(charObject.getAvatarURL());
        builder.queue(command.guildChannel);
        return "";
    }

    @Override
    protected String[] names() {
        return new String[]{"CharAvatar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gets a larger version avatar of the character specified.";
    }

    @Override
    protected String usage() {
        return "[Character ID]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CHARACTER;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.CHARACTER;
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
        return false;
    }

    @Override
    public void init() {

    }
}
