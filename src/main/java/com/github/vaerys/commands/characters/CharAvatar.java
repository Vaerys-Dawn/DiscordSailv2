package com.github.vaerys.commands.characters;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class CharAvatar extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        CharacterObject charObject = command.guild.characters.getCharByName(args.split(" ")[0]);
        if (charObject == null) return "> Could not find any characters with that Character ID.";
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(charObject.getNickname());
        IUser user = command.guild.getUserByID(charObject.getUserID());
        builder.withFooterText("Author: " + user.getDisplayName(command.guild.get()) + " | Character ID: " + charObject.getName());
        command.guild.characters.validateRoles(command.guild.get());
        if (charObject.getRoleIDs().size() != 0) {
            builder.withColor(charObject.getColor(command.guild));
        }
        if (charObject.getAvatarURL() == null || charObject.getAvatarURL().isEmpty())
            return "> " + charObject.getNickname() + " Does not have an avatar set up.";
        builder.withImage(charObject.getAvatarURL());
        builder.send(command.channel);
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
