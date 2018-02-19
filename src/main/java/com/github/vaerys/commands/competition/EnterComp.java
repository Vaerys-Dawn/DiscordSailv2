package com.github.vaerys.commands.competition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.CompObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

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

    protected static final String[] NAMES = new String[]{"EnterComp","Comp","Enter"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Enters your image into the Sail Competition.";
    }

    protected static final String USAGE = "[Image Link or Image File]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.COMPETITION;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.BOT_COMMANDS;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
