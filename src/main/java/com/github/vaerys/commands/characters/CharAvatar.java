package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
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
    public String[] names() {
        return new String[]{"CharAvatar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gets a larger version avatar of the character specified.";
    }

    @Override
    public String usage() {
        return "[Character ID]";
    }

    @Override
    public String type() {
        return TYPE_CHARACTER;
    }

    @Override
    public String channel() {
        return CHANNEL_CHAR;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
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
