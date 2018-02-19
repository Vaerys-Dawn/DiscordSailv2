package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
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

    protected static final String[] NAMES = new String[]{"CharAvatar"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gets a larger version avatar of the character specified.";
    }

    protected static final String USAGE = "[Character ID]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CHARACTER;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.CHARACTER;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
