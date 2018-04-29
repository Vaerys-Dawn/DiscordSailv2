package com.github.vaerys.commands.adminccs;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Globals;
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

public class NewAdminCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.adminCCs.getCommands().size() == 20) {
            return "> This server has already hit the limit on how many Admin Custom Commands it can hold, you will need to delete old commands to make room.";
        }
        SplitFirstObject object = new SplitFirstObject(args);
        if (object.getFirstWord().length() > 50) {
            return "> Cannot create Admin CC, Command name length too long. (Max Chars 50)";
        }
        if (object.getFirstWord().contains("\n")) {
            return "> Cannot create Admin CC, Command name contains Newlines.";
        }
        if (command.guild.adminCCs.commandExists(object.getFirstWord())) {
            return "> Command name already in use.";
        }

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
            return "> Cannot create Admin CC, Contents contains more than one <embedImage> tag.";
        }

        if (contents.isEmpty()) return "> Cannot create Admin CC, Contents is empty.";
        if (contents.length() > 10000) return "> Cannot create Admin CC, Contents is too long. (Max Chars 10000)";

        AdminCCObject cc = command.guild.adminCCs.addCommand(command, object.getFirstWord(), contents.toString());

        return "> New Admin Custom Command Created, you can run the new command by performing `" + cc.getName(command) +
                "` (" + (20 - command.guild.adminCCs.getCommands().size()) + " Admin CC slots remain.)";
    }

    @Override
    public String[] names() {
        return new String[]{"NewAdminCC", "CreateAdminCC", "AddAdminCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows the creation of Admin Custom Commands. These commands allow for extra levels of interactions with " + Globals.botName + " and Discord.";
    }

    @Override
    public String usage() {
        return "[CommandName] [Contents/File.txt]";
    }

    @Override
    public SAILType type() {
        return SAILType.ADMIN_CC;
    }

    @Override
    public ChannelSetting channel() {
        return ChannelSetting.MANAGE_CC;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER, Permissions.MANAGE_ROLES, Permissions.MANAGE_MESSAGES};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}