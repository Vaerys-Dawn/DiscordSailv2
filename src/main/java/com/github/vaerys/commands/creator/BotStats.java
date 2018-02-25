package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.handlers.TimerHandler;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.List;

public class BotStats extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<Double> cpuUsage = TimerHandler.cpuUsage;
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - freeMemory;
        double usage = cpuUsage.stream().mapToDouble(value -> value).sum() / cpuUsage.size();
        // and make it look pretty
        NumberFormat nf = NumberFormat.getInstance();
        long ping = command.client.get().getShards().get(0).getResponseTime();
        double mb = 1048576.0;

        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(command.client.bot.username);
        builder.withTimestamp(command.client.bot.get().getCreationDate());
        builder.withFooterText("Creation Date");
        StringHandler handler = new StringHandler();
        handler.append("**Total Servers**: ").append(command.client.get().getGuilds().size());
        handler.append("\n**Total Active Threads**: ").append(Thread.activeCount());
        handler.append("\n**CPU Usage**: ").append(nf.format(usage * 100)).append("%");
        handler.append("\n**Memory Usage**: ");
        nf.setMaximumFractionDigits(1);
        handler.append(nf.format(totalMemory / mb)).append("MB total\t");
        handler.append(nf.format(usedMemory / mb)).append("MB used\t");
        handler.append(nf.format(freeMemory / mb)).append("MB free");
        handler.append("\n**Ping**: ").append(nf.format(ping)).append("ms");
        builder.withDesc(handler.toString());
        builder.withThumbnail(command.client.bot.getAvatarURL());
        builder.send(command.channel);
        return null;
    }

    protected static final String[] NAMES = new String[]{"BotStats"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives the General stats of the bot.";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
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

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
