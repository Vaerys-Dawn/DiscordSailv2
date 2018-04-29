package com.github.vaerys.commands.adminccs;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.AdminCCObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.cctags.TagEmbedImage;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

public class EditAdminCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject object = new SplitFirstObject(args);

        AdminCCObject cc = command.guild.adminCCs.getCommand(object.getFirstWord());
        if (cc == null) return "> Could not find any admin custom commands with that name.";

        StringHandler contents = new StringHandler(object.getRest());
        IMessage.Attachment attachment = null;
        if (command.message.getAttachments().size() != 0) {
            attachment = command.message.getAttachments().get(0);
        }
        if (attachment != null && attachment.getFilename().endsWith(".txt")) {
            contents.append(FileHandler.readFromFile(attachment));
        }
        TagEmbedImage tag = TagList.getTag(TagEmbedImage.class);
        if (attachment != null && Utility.isImageLink(attachment.getFilename())) {
            contents.append(tag.prefix).append(attachment.getUrl()).append(tag.suffix);
        }
        if (StringUtils.countMatches(object.getRest(), tag.prefix) > 1) {
            return "> Cannot edit Admin CC, Contents contains more than one <embedImage> tag.";
        }

        if (contents.isEmpty()) return "> Cannot edit Admin CC, Contents is empty.";
        if (contents.length() > 10000) return "> Cannot edit Admin CC, Contents is too long. (Max Chars 10000)";

        cc.setContents(contents.toString());
        return "> Admin custom command contents updated.";
    }

    @Override
    protected String[] names() {
        return new String[]{"EditAdminCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for editing of Admin Custom Commands.\n" +
                "[Example Tag usages.](https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/AdminCC-Tag-Examples)";
    }

    @Override
    protected String usage() {
        return "[Command Name] [Contents/File.txt]";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN_CC;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.MANAGE_CC;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER, Permissions.MANAGE_ROLES, Permissions.MANAGE_MESSAGES};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    protected void init() {

    }
}
