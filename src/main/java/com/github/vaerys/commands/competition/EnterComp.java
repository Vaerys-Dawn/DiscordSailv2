package com.github.vaerys.commands.competition;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CompObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EnterComp extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        GuildConfig guildconfig = command.guild.config;
        IMessage message = command.message.get();
        IUser author = command.user.get();

        if (guildconfig.compEntries) {
            String fileName;
            String fileUrl;
            if (message.getAttachments().size() > 0) {
                List<IMessage.Attachment> attatchments = message.getAttachments();
                IMessage.Attachment a = attatchments.get(0);
                fileName = a.getFilename();
                fileUrl = a.getUrl();
            } else if (!args.isEmpty()) {
                fileName = author.getName() + "'s Entry";
                fileUrl = args;
            } else {
                return "> Missing a File or Image link to enter into the competition.";
            }
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            command.guild.competition.newEntry(new CompObject(author.getDisplayName(command.guild.get()), author.getLongID(), fileName, fileUrl, dateFormat.format(cal.getTime())));
            return "> Thank you " + author.getDisplayName(command.guild.get()) + " For entering the Competition.";
        } else {
            return "> Competition Entries are closed.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"EnterComp", "Comp", "Enter"};
    }

    @Override
    public String description(CommandObject command) {
        return "Enters your image into the Sail Competition.";
    }

    @Override
    protected String usage() {
        return "[Image Link or Image File]";
    }

    @Override
    protected SAILType type() {
        return SAILType.COMPETITION;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.BOT_COMMANDS;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
