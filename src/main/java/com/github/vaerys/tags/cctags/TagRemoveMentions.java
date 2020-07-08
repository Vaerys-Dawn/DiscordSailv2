package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Role;
import sx.blah.discord.handle.obj.IUser;

import java.util.regex.Pattern;

public class TagRemoveMentions extends TagObject {

    public TagRemoveMentions(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        boolean isRoleMention = false;
        String id;
        if (Pattern.compile("<@&[0-9]*>").matcher(from).find()) {
            id = StringUtils.substringBetween(from, "<@&", ">");
            isRoleMention = true;
        } else {
            id = StringUtils.substringBetween(from, "<@!", ">");
            if (id == null) {
                id = StringUtils.substringBetween(from, "<@", ">");
            }
        }
        if (!isRoleMention) {
            try {
                long userID = Long.parseUnsignedLong(id);
                IUser user = command.guild.getUserByID(userID);
                if (user != null) {
                    from = from.replace(user.mention(true), user.getDisplayName(command.guild.get()));
                    from = from.replace(user.mention(false), user.getDisplayName(command.guild.get()));
                } else {
                    throw new NumberFormatException("You shouldn't see this.");
                }
            } catch (NumberFormatException e) {
                from = from.replaceAll("<@!?" + id + ">", "null");
            }
        } else {
            //remove role mentions
            try {
                long roleID = Long.parseUnsignedLong(id);
                Role role = command.guild.getRoleById(roleID);
                if (role != null) {
                    from = from.replace("<@&" + id + ">", role.getName());
                } else {
                    throw new NumberFormatException("You shouldn't see this.");
                }
            } catch (NumberFormatException e) {
                from = from.replace("<@&" + id + ">", "null");
            }
        }
        return from;
    }

    @Override
    public boolean cont(String from) {
        return Pattern.compile("<@!?[0-9]*>").matcher(from).find() ||
                Pattern.compile("<@&[0-9]*>").matcher(from).find();
    }

    @Override
    public String tagName() {
        return "<removeMentions>";
    }

    @Override
    public int argsRequired() {
        return 0;
    }

    @Override
    public boolean isPassive() {
        return true;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String desc() {
        return "Removes mass mentions like Everyone and Here and turns role and user mentions into plain text.";
    }

    @Override
    public String handleTag(String from, CommandObject command, String args) {
        while (cont(from)) {
            from = execute(from, command, args);
        }
        from = from.replaceAll("(?i)@everyone", "**[REDACTED]**");
        from = from.replaceAll("(?i)@here", "**[REDACTED]**");
        return from;
    }
}
