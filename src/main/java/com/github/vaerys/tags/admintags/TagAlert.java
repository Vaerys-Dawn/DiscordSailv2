package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;
import sx.blah.discord.handle.obj.TextChannel;
import sx.blah.discord.handle.obj.IUser;

public class TagAlert extends TagAdminSubTagObject {

    public TagAlert(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String getSubPrefix(String from) {
        return "<" + tagName() + ":" + getSubTag(from) + ">(";
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        try {
            long id = Long.parseUnsignedLong(getSubTag(from));
            TextChannel destination = command.guild.getChannelByID(id);
            if (destination == null) {
                IUser user = command.guild.getUserByID(id);
                if (user != null) destination = UserObject.getNewUserObject(id, command.guild).getDmChannel();
            }
            if (destination == null) return replaceFirstTag(from, error);
            String message = getContents(from);
            if (!message.contains("<mention>")) return replaceFirstTag(from, error + "(Missing <mention> tag)");
            message = message.replace("<mention>", command.user.mention());
            message = message.replace("<ccName>", cc.getName(command));
            message = message.replaceAll("(?i)@(here|everyone)", "[REDACTED]");
            message = message.replaceAll("<@&[0-9]*>","[REDACTED]");
            RequestHandler.sendMessage(message, destination);
            return removeAllTag(from);
        } catch (NumberFormatException e) {
            return replaceFirstTag(from, error);
        }
    }

    @Override
    protected String prefix() {
        return "<" + tagName() + ":([\\w| ]+?)>(";
    }

    @Override
    protected String suffix() {
        return ")</alert>";
    }

    @Override
    protected String subTagUsage() {
        return "userID/channelID";
    }

    @Override
    public String tagName() {
        return "alert";
    }

    @Override
    protected int argsRequired() {
        return 1;
    }

    @Override
    protected String usage() {
        return "Message";
    }

    @Override
    protected String desc() {
        return "Removes itself and sends an alert to the specified user's Dms or if it's a Channel ID send it to that channel." +
                " The contents of the message requires <mention> to be placed somewhere in the text, you may also place <ccName> in the contents as well to show the Custom Command Name.\n" +
                "Only the first of this tag will run and the rest will be removed.";
    }
}
