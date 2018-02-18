package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

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

    @Override
    public String[] names() {
        return new String[]{"CharInfo", "InfoChar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives Information about a certain character.";
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
