package Commands.CC;

import Commands.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.CCommandObject;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static Main.Globals.commands;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class ListCCs implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        IMessage message = command.message;
        if (message.getMentions().size() > 0) {
            return getUserCommands(command, command.message.getMentions().get(0).getID());
        }
        IUser mentionedUser = Globals.getClient().getUserByID(args);
        if (mentionedUser != null) {
            return getUserCommands(command, mentionedUser.getID());
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

    public String getUserCommands(CommandObject command, String userID) {
        IUser user = Globals.getClient().getUserByID(userID);
        int total = 0;
        int max = command.customCommands.maxCCs(user, command.guild, command.guildConfig);
        EmbedBuilder builder = new EmbedBuilder();
        String title = "> Here are the custom commands for user: **@" + user.getName() + "#" + user.getDiscriminator() + "**.";
        ArrayList<String> list = new ArrayList<>();
        for (CCommandObject c: command.customCommands.getCommandList()){
            if (c.getUserID().equals(userID)){
                list.add(command.guildConfig.getPrefixCC() + c.getName());
                total ++;
            }
        }
        Utility.listFormatterEmbed(title,builder,list,true);
        builder.withColor(Utility.getUsersColour(command.client.getOurUser(), command.guild));
        builder.withFooterText("Total Custom commands: " + total + "/" + max + ".");
        Utility.sendEmbededMessage("", builder.build(), command.channel);
        return null;
    }

    public String listCommands(int page, CommandObject command) {
        ArrayList<String> pages = new ArrayList<>();
        int counter = 0;
        int totalCCs = 0;
        ArrayList<String> list = new ArrayList<>();
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(Utility.getUsersColour(command.client.getOurUser(), command.guild));

        for (CCommandObject c : command.customCommands.getCommandList()) {
            if (counter > 15) {
                pages.add("`" + Utility.listFormatter(list, true) + "`");
                list.clear();
                counter = 0;
            }
            list.add(command.guildConfig.getPrefixCC() + c.getName());
            totalCCs++;
            counter++;
        }
        pages.add("`" + Utility.listFormatter(list, true) + "`");
        try {
            String title = "> Here is Page **" + page + "/" + pages.size() + "** of Custom Commands:";
            builder.appendField(title, pages.get(page - 1), false);
            builder.withFooterText("Total Custom Commands stored on this Server: " + totalCCs);
            Utility.sendEmbededMessage("", builder.build(), command.channel);
            return null;
        } catch (IndexOutOfBoundsException e) {
            return "> That Page does not exist.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"CClist", "ListCCs"};
    }

    @Override
    public String description() {
        return "Generates a list of custom commands based on parameters.";
    }

    @Override
    public String usage() {
        return "[Page Number]/@User/[UserID]";
    }

    @Override
    public String type() {
        return TYPE_CC;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
