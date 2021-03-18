package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.handlers.TimerHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.text.NumberFormat;
import java.util.List;

public class BotStats extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<Double> cpuUsage = TimerHandler.cpuUsage;
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - freeMemory;
        long userCount = command.client.get().getUsers().size();
        double usage = cpuUsage.stream().mapToDouble(value -> value).sum() / cpuUsage.size();
        // and make it look pretty
        NumberFormat nf = NumberFormat.getInstance();
        long pingGate = command.client.get().getGatewayPing();
        long pingRest = command.client.get().getRestPing().complete();
        double mb = 1048576.0;

        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle(command.client.bot.username);
        builder.setTimestamp(command.client.bot.get().getTimeCreated());
        builder.setFooter("Creation Date");
        StringHandler handler = new StringHandler();
        handler.append("**Total Servers**: ").append(command.client.get().getGuilds().size());
        handler.append(INDENT + "**Total Users**: " + userCount);
        handler.append("\n**Total Active Threads**: ").append(Thread.activeCount());
        handler.append("\n**CPU Usage**: ").append(nf.format(usage * 100)).append("%");
        handler.append("\n**Memory Usage**: ");
        nf.setMaximumFractionDigits(1);
        handler.append(nf.format(totalMemory / mb)).append("MB total\t");
        handler.append(nf.format(usedMemory / mb)).append("MB used\t");
        handler.append(nf.format(freeMemory / mb)).append("MB free");
        handler.append(String.format("%n**Gateway Ping:** %sms, **Rest Ping:** %sms", nf.format(pingGate), nf.format(pingRest)));
        builder.setDescription(handler.toString());
        builder.setThumbnail(command.client.bot.avatarURL);
        builder.queue(command.guildChannel);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"BotStats"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives the General stats of the bot.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {
        // does nothing
    }
}
