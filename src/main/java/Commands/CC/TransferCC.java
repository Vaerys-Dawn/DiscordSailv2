package Commands.CC;

import Commands.CommandObject;
import Handlers.FileHandler;
import Interfaces.Command;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.CCommandObject;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.nio.file.Paths;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class TransferCC implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        String filePath = Constants.DIRECTORY_OLD_FILES + command.guildID + "_CustomCommands.json";
        IGuild guild = command.guild;
        IUser author = command.author;
        IChannel channel = command.channel;
        GuildConfig guildConfig = command.guildConfig;
        CustomCommands customCommands = command.customCommands;

        if (Paths.get(filePath).toFile().exists()) {
            BadCode.CustomCommands oldCommands = null;
            while (oldCommands == null) {
                oldCommands = (BadCode.CustomCommands) FileHandler.readFromJson(filePath, BadCode.CustomCommands.class);
            }
            CCommandObject transfering = oldCommands.convertCommand(args);
            if (transfering == null) {
                return Constants.ERROR_CC_NOT_FOUND;
            }
            boolean locked = transfering.isLocked();
            String userID = transfering.getUserID();
            if (guild.getUserByID(userID) == null) {
                Utility.sendMessage("> This command's old owner no longer is part of this server.\n" + Constants.PREFIX_INDENT +
                        author.getDisplayName(guild) + " will become the new owner of this command.\n" +
                        "> I am now attempting to transfer the command over.", channel);
                userID = author.getID();
            } else {
                Utility.sendMessage("> I am now attempting to transfer " + guild.getUserByID(userID).getDisplayName(guild) + "'s command.", channel);
            }
            String name = transfering.getName();
            String contents = transfering.getContents(false);
            contents = contents.replace("#author!#", "#username#");
            boolean shitpost = transfering.isShitPost();

            return customCommands.addCommand(locked, Globals.getClient().getUserByID(userID), name, contents, shitpost, guild, guildConfig);
        } else {
            return "> Your Server has no Legacy commands to transfer.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"TransferCC"};
    }

    @Override
    public String description() {
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
