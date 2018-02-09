package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.pogos.CustomCommands;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.nio.file.Paths;

/**
 * Created by Vaerys on 01/02/2017.
 */
@Deprecated
public class TransferCC extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        String filePath = Constants.DIRECTORY_OLD_FILES + command.guild.longID + "_CustomCommands.json";
        IGuild guild = command.guild.get();
        IUser author = command.user.get();
        IChannel channel = command.channel.get();
        CustomCommands customCommands = command.guild.customCommands;

        if (Paths.get(filePath).toFile().exists()) {
            com.github.vaerys.oldcode.CustomCommands oldCommands = null;
            if (oldCommands == null) {
                oldCommands = (com.github.vaerys.oldcode.CustomCommands) FileHandler.readFromJson(filePath, com.github.vaerys.oldcode.CustomCommands.class);
            }
            CCommandObject transfering = oldCommands.convertCommand(args);
            if (transfering == null) {
                return Constants.ERROR_CC_NOT_FOUND;
            }
            boolean locked = transfering.isLocked();
            long userID = transfering.getUserID();
            if (guild.getUserByID(userID) == null) {
                RequestHandler.sendMessage("> This command's old owner no longer is part of this server.\n" + Constants.PREFIX_INDENT +
                        author.getDisplayName(guild) + " will become the new owner of this command.\n" +
                        "> I am now attempting to transfer the command over.", channel);
                userID = author.getLongID();
            } else {
                RequestHandler.sendMessage("> I am now attempting to transfer " + guild.getUserByID(userID).getDisplayName(guild) + "'s command.", channel);
            }
            String name = transfering.getName();
            String contents = transfering.getContents(false);
            contents = contents.replace("#author!#", "#username#");
            boolean shitpost = transfering.isShitPost();

            command.setAuthor(Globals.getClient().getUserByID(userID));
            return customCommands.addCommand(locked, name, contents, shitpost, command);
        } else {
            return "> Your Server has no Legacy commands to transfer.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"TransferCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Transfers a legacy command to the new system.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
    }

    @Override
    public String type() {
        return TYPE_CC;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

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
