package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 04/04/2017.
 */
public class CCEditModes {

    public static String lock(CCommandObject c, CommandObject command, IUser author, IGuild guild) {
        if (Utility.testForPerms(author, guild, Permissions.MANAGE_MESSAGES)) {
            c.toggleLocked();
            return "> Lock for **" + c.getName() + "** is now " + c.isLocked() + ".";
        } else {
            return command.user.notAllowed;
        }
    }

    public static String shitPost(CCommandObject c, CommandObject command, IUser author, IGuild guild) {
        if (Utility.testForPerms(author, guild, Permissions.MANAGE_MESSAGES)) {
            c.toggleShitPost();
            return "> Shitpost for **" + c.getName() + "** is now " + c.isShitPost() + ".";
        } else {
            return command.user.notAllowed;
        }
    }

    public static String deleteTag(CCommandObject c) {
        String delCall = "<delCall>";
        if (c.getContents(false).contains(delCall)) {
            return "> Command will already delete the calling message.";
        } else {
            c.setContents(c.getContents(false) + delCall);
            return "> Tag added";
        }
    }


    public static String replace(CCommandObject command, String content, CommandObject object) {
        if (content == null || content.isEmpty() && object.message.get().getAttachments().size() == 0) {
            return "> Missing content to replace with.";
        }

        command.setContents(content);
        return "> Command Edited.";
    }

    public static String toEmbed(CCommandObject commmand) {
        String contents = commmand.getContents(false);
        if (contents.contains(" ") || contents.contains("\n")) {
            return "> Failed to add embed tag.";
        }
        if (contents.contains("<embedImage>{")) {
            return "> Command already has an EmbedImage Tag, cannot add more than one.";
        }
        commmand.setContents("<embedImage>{" + contents + "}");
        return "> Embed tag added.";
    }

    public static String append(CCommandObject command, String content, CommandObject commandObject) {
        if (content == null || content.isEmpty()) {
            return "> Missing content to append.";
        }
        if ((command.getContents(false) + content).length() > 2000) {
            return "> Cannot append content, would make command to large.";
        }
        if (StringUtils.countMatches(command.getContents(false) + content + " " + content, "<embedImage>{") > 1) {
            if (commandObject.message.get().getAttachments().size() != 0) {
                return "> Custom commands cannot contain more than one image.";
            }
            return "> Custom Commands Cannot have multiple <embedImage> tags";
        }
        command.setContents(command.getContents(false) + content);
        return "> Content appended to end of command.";
    }
}
