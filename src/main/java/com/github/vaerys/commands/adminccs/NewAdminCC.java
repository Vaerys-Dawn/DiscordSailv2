package com.github.vaerys.commands.adminccs;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.cctags.TagEmbedImage;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Message;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Arrays;
import java.util.List;

public class NewAdminCC extends Command {

    protected enum ResponseCode {
        TOO_MANY_EMBEDS,
        EMPTY_CONTENTS,
        NOT_ENOUGH_SLOTS,
        OVERLOADS_SLOTS,
        OKAY
    }

    @Override
    public String execute(String args, CommandObject command) {

        SplitFirstObject object = new SplitFirstObject(args);
        if (object.getFirstWord().length() > 50) {
            return "\\> Cannot create Admin CC, Command name length too long. (Max Chars 50)";
        }
        if (object.getFirstWord().contains("\n")) {
            return "\\> Cannot create Admin CC, Command name contains Newlines.";
        }
        if (command.guild.adminCCs.commandExists(object.getFirstWord())) {
            return "\\> Command name already in use.";
        }

        StringHandler contents = getContents(command, object.getRest());
        ResponseCode response = testContents(command, contents, null);

        switch (response) {
            case EMPTY_CONTENTS:
                return "\\> Cannot create Admin CC, Contents is empty.";
            case TOO_MANY_EMBEDS:
                return "\\> Cannot create Admin CC, Contents contains more than one <embedImage> tag.";
            case NOT_ENOUGH_SLOTS:
                return "\\> This server has already hit the limit on how many Admin Custom Commands it can hold, you will need to delete or edit old commands to make room.";
            case OVERLOADS_SLOTS:
                return "\\> There are not enough slots left for you to create this Admin Custom Command. The length of the command is too long.";
        }

        AdminCCObject cc = command.guild.adminCCs.addCommand(command, object.getFirstWord(), contents.toString());


        int remainder = (20 - command.guild.adminCCs.getUsedSlots());
        return String.format("\\> New Admin Custom Command Created, you can run the new command by performing `%s`. (%d Admin CC slot%s remain)",
                cc.getName(command), remainder, remainder != 1 ? "s" : "");
    }


    public static StringHandler getContents(CommandObject command, String args) {
        StringHandler contents = new StringHandler(args);
        Message.Attachment attachment = null;
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
        StringHandler temp = new StringHandler();
        List<String> lines = Arrays.asList(contents.split("\n"));
        for (String line : lines) {
            if (!temp.isEmpty()) temp.append("\n");
            if (!line.startsWith("//")) {
                temp.append(line);
            }
        }
        contents = temp;
        return contents;
    }

    public static ResponseCode testContents(CommandObject command, StringHandler contents, AdminCCObject cc) {
        TagEmbedImage tag = TagList.getTag(TagEmbedImage.class);
        int usedSlots = command.guild.adminCCs.getUsedSlots();
        if (usedSlots >= 20) return ResponseCode.NOT_ENOUGH_SLOTS;
        if (contents.isEmpty()) return ResponseCode.EMPTY_CONTENTS;
        int ccSlots = (contents.length() / 10000) + 1;
        if (StringUtils.countMatches(contents.toString(), tag.prefix) > 1) return ResponseCode.TOO_MANY_EMBEDS;
        if (cc != null) {
            if (ccSlots + usedSlots - cc.getSlots() > 20) return ResponseCode.OVERLOADS_SLOTS;
        } else {
            if (ccSlots + usedSlots > 20) return ResponseCode.OVERLOADS_SLOTS;
        }
        return ResponseCode.OKAY;
    }

    @Override
    public String[] names() {
        return new String[]{"NewAdminCC", "CreateAdminCC", "AddAdminCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows the creation of Admin Custom Commands. These commands allow for extra levels of interactions with " + Globals.botName + " and Discord.\n" +
                "[Example Tag usages.](https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/AdminCC-Tag-Examples)";
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
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permission[]{Permissions.MANAGE_SERVER, Permissions.MANAGE_ROLES, Permissions.MANAGE_MESSAGES};
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