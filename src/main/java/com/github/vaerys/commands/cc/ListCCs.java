package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class ListCCs extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (args.length() > 3) {
            UserObject user = Utility.getUser(command, args, true);
            if (user != null) {
                return getUserCommands(command, user);
            }
        }
        try {
            int page;
            if (args == null || args.isEmpty()) {
                page = 1;
            } else {
                page = Integer.parseInt(args.split(" ")[0]);
            }
            return listCommands(page, command);
        } catch (NumberFormatException e) {
            return "> what are you doing, why are you trying to search for the " + args + " page... \n" +
                    Constants.PREFIX_INDENT + "pretty sure you cant do that...";
        }
    }

    public String getUserCommands(CommandObject command, UserObject user) {
        if (user == null) return "> Could not find user.";
        int total = 0;
//        command.setAuthor(user);
        int max = command.guild.customCommands.maxCCs(user, command.guild);
        XEmbedBuilder builder = new XEmbedBuilder(command);
        String title = "> Here are the custom commands for user: @" + user.username + ".";
        List<String> list = new ArrayList<>();
        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
            if (c.getUserID() == user.longID) {
                list.add(command.guild.config.getPrefixCC() + c.getName());
                total++;
            }
        }
        builder.setTitle(title);
        String content = Utility.listFormatter(list, true);
        if (content.length() > 2000) {
            String fileName = String.format("Commands_%s.txt", command.user.name);
            command.channel.queueFile(title, content.getBytes(), fileName);
            return null;
        }
        builder.setDescription("```\n" + content + "```");
        builder.setFooter("Total Custom commands: " + total + "/" + max + ".");
        builder.queue(command);
        return null;
    }

    public String listCommands(int page, CommandObject command) {
        ArrayList<String> pages = new ArrayList<>();
        int counter = 0;
        int totalCCs = 0;
        ArrayList<String> list = new ArrayList<>();
        XEmbedBuilder builder = new XEmbedBuilder(command);

        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
            if (counter > 30) {
                pages.add("```" + Utility.listFormatter(list, true) + "```");
                list.clear();
                counter = 0;
            }
            list.add(command.guild.config.getPrefixCC() + c.getName());
            totalCCs++;
            counter++;
        }
        pages.add("```" + Utility.listFormatter(list, true) + "```");
        try {
            String title = "> Here is Page **" + page + "/" + pages.size() + "** of Custom Commands:";
            builder.addField(title, pages.get(page - 1), false);
            builder.setFooter("Total Custom Commands stored on this Server: " + totalCCs);
            builder.queue(command);
            return null;
        } catch (IndexOutOfBoundsException e) {
            return "> That Page does not exist.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"CClist", "ListCCs", "ListCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Generates a list of custom commands based on parameters.";
    }

    @Override
    protected String usage() {
        return "(Page Number/@User)";
    }

    @Override
    protected SAILType type() {
        return SAILType.CC;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.CC_INFO;
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

    }
}
