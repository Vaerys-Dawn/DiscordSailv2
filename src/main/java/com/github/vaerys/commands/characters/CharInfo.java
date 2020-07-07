package com.github.vaerys.commands.characters;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CharacterObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
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
                builder.setTitle(object.getNickname());

                IUser user = command.guild.getUserByID(object.getUserID());
                if (user == null) {
                    builder.setFooter("Author: No longer on this server | Character ID: " + object.getName());
                } else {
                    builder.setFooter("Author: " + user.getDisplayName(command.guild.get()) + " | Character ID: " + object.getName());
                }

                ArrayList<IRole> roles = new ArrayList<>();
                ArrayList<String> roleNames = new ArrayList<>();
                for (Long roleId : object.getRoleIDs()) {
                    if (command.client.get().getRoleByID(roleId) != null) {
                        roles.add(command.client.get().getRoleByID(roleId));
                        roleNames.add(command.client.get().getRoleByID(roleId).getName());
                    }
                }
                if (roles.size() != 0) {
                    builder.withColor(GuildHandler.getUsersColour(roles));
                } else {
                    builder.withColor(GuildHandler.getUsersColour(user, command.guild.get()));
                }

                StringBuilder description = new StringBuilder();
                description.append("**Age:** " + object.getAge());
                description.append("\n**Gender:** " + object.getGender());
                if (object.getHeight() != null || object.getWeight() != null) {
                    description.append("\n");
                    if (object.getHeight() != null) {
                        description.append("**Height:** " + object.getHeight() + indent);
                    }
                    if (object.getWeight() != null) {
                        description.append("**Weight:** " + object.getWeight());
                    }
                }
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
                builder.setDescription(description.toString());
                if (object.getAvatarURL() != null && !object.getAvatarURL().isEmpty()) {
                    if (object.getAvatarURL().contains("\n") || object.getAvatarURL().contains(" ")) {
                        return "\\> An Error Occurred. Avatar url needs to be reset.";
                    }
                    builder.withThumbnail(object.getAvatarURL());
                }
                RequestHandler.sendEmbedMessage("", builder, command.channel.get());
                return null;
            }
        }
        return "\\> Character with that name not found.";
    }

    @Override
    protected String[] names() {
        return new String[]{"CharInfo", "InfoChar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives Information about a certain character.";
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
