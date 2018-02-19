package com.github.vaerys.commands.characters;

import java.util.ArrayList;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class CharInfo extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (CharacterObject object : command.guild.characters.getCharacters(command.guild.get())) {
            if (object.getName().equalsIgnoreCase(args)) {
                XEmbedBuilder builder = new XEmbedBuilder(command);
                builder.withTitle(object.getNickname());

                IUser user = command.guild.getUserByID(object.getUserID());
                builder.withFooterText("Author: " + user.getDisplayName(command.guild.get()) + " | Character ID: " + object.getName());

                ArrayList<IRole> roles = new ArrayList<>();
                ArrayList<String> roleNames = new ArrayList<>();
                for (Long roleId : object.getRoleIDs()) {
                    if (command.client.get().getRoleByID(roleId) != null) {
                        roles.add(command.client.get().getRoleByID(roleId));
                        roleNames.add(command.client.get().getRoleByID(roleId).getName());
                    }
                }
                if (roles.size() != 0) {
                    builder.withColor(Utility.getUsersColour(roles, command.guild.get()));
                }

                StringBuilder description = new StringBuilder();
                description.append("**Age:** " + object.getAge());
                description.append("\n**Gender:** " + object.getGender());
                if (roleNames.size() != 0) {
                    if (command.guild.characters.getRolePrefix() != null && !command.guild.characters.getRolePrefix().isEmpty()) {
                        description.append("\n" + command.guild.characters.getRolePrefix() + " " + Utility.listFormatter(roleNames, true));
                    } else {
                        description.append("\n" + Utility.listFormatter(roleNames, true));
                    }
                }
                description.append("\n**Bio:** " + object.getShortBio());
                if (object.getLongBioURL() != null && !object.getLongBioURL().isEmpty()) {
                    description.append("\n\n**[Long Description Link...](" + object.getLongBioURL() + ")**");
                }
                builder.withDesc(description.toString());
                if (object.getAvatarURL() != null && !object.getAvatarURL().isEmpty()) {
                    if (object.getAvatarURL().contains("\n") || object.getAvatarURL().contains(" ")) {
                        return "> An Error Occurred. Avatar url needs to be reset.";
                    }
                    builder.withThumbnail(object.getAvatarURL());
                }
                RequestHandler.sendEmbedMessage("", builder, command.channel.get());
                return null;
            }
        }
        return "> Character with that name not found.";
    }

    protected static final String[] NAMES = new String[]{"CharInfo", "InfoChar"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives Information about a certain character.";
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
