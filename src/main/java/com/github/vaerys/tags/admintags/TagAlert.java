package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

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
            MessageChannel destination = command.guild.getChannelByID(id);
            if (destination == null) {
                Member user = command.guild.getUserByID(id);
                if (user != null) destination = new UserObject(user, command.guild).getDmChannel();
            }
            if (destination == null) return replaceFirstTag(from, error);
            String message = getContents(from);
            if (!message.contains("<mention>")) return replaceFirstTag(from, error + "(Missing <mention> tag)");
            message = message.replace("<mention>", command.user.mention());
            message = message.replace("<ccName>", cc.getName(command));
            message = message.replaceAll("(?i)@(here|everyone)", "[REDACTED]");
            message = message.replaceAll("<@&[0-9]*>","[REDACTED]");
            destination.sendMessage(message).queue();
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
        return "Removes itself and sends an alert to the specified globalUser's Dms or if it's a Channel ID send it to that messageChannel." +
                " The contents of the message requires <mention> to be placed somewhere in the text, you may also place <ccName> in the contents as well to show the Custom Command Name.\n" +
                "Only the first of this tag will run and the rest will be removed.";
    }
}
