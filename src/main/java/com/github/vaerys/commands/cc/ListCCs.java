package com.github.vaerys.commands.cc;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;
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
                return getUserCommands(command, user.longID);
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

    public String getUserCommands(CommandObject command, long userID) {
        IUser user = Globals.getClient().getUserByID(userID);
        int total = 0;
        command.setAuthor(user);
        int max = command.guild.customCommands.maxCCs(command.user, command.guild);
        XEmbedBuilder builder = new XEmbedBuilder(command);
        String title = "> Here are the custom commands for user: **@" + user.getName() + "#" + user.getDiscriminator() + "**.";
        List<String> list = new ArrayList<>();
        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
            if (c.getUserID() == userID) {
                list.add(command.guild.config.getPrefixCC() + c.getName());
                total++;
            }
        }
        builder.withTitle(title);
        String content = Utility.listFormatter(list, true);
        if (content.length() > 2000) {
            String path = Constants.DIRECTORY_TEMP + command.message.longID + ".txt";
            FileHandler.writeToFile(path, content, false);
            File file = new File(path);
            RequestHandler.sendFile(title, file, command.channel.get());
            return null;
        }
        builder.withDescription("```\n" + content + "```");
        builder.withFooterText("Total Custom commands: " + total + "/" + max + ".");
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    public String listCommands(int page, CommandObject command) {
        ArrayList<String> pages = new ArrayList<>();
        int counter = 0;
        int totalCCs = 0;
        ArrayList<String> list = new ArrayList<>();
        XEmbedBuilder builder = new XEmbedBuilder(command);

        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
            if (counter > 15) {
                pages.add("```" + Utility.listFormatter(list, true) + "```");
                list.clear();
                counter = 0;
            }
            list.add(command.guild.config.getPrefixCC() + c.getName());
            totalCCs++;
            counter++;
        }
        pages.add("`" + Utility.listFormatter(list, true) + "`");
        try {
            String title = "> Here is Page **" + page + "/" + pages.size() + "** of Custom Commands:";
            builder.appendField(title, pages.get(page - 1), false);
            builder.withFooterText("Total Custom Commands stored on this Server: " + totalCCs);
            RequestHandler.sendEmbedMessage("", builder, command.channel.get());
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
