package Commands.CC;

import Commands.CommandObject;
import Handlers.FileHandler;
import Interfaces.Command;
import Main.Constants;
import Main.Utility;
import Objects.CCommandObject;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class SearchCCs implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<CCommandObject> searched = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (CCommandObject c : command.customCommands.getCommandList()) {
            StringBuilder toSearch = new StringBuilder();
            toSearch.append(c.getName().toLowerCase());
            toSearch.append(c.getContents(false).toLowerCase());
            if (c.isLocked()) {
                toSearch.append("<locked>");
            }
            if (c.isShitPost()) {
                toSearch.append("<shitpost>");
            }
            if ((toSearch.toString()).contains(args.toLowerCase())) {
                searched.add(c);
            }
        }
        String title = "> Here is your search:";
        ArrayList<String> list = new ArrayList<>();
        for (CCommandObject c : searched) {
            list.add(command.guildConfig.getPrefixCC() + c.getName());
        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder();
        Utility.listFormatterEmbed(title,embedBuilder,list,true);
        embedBuilder.withColor(Utility.getUsersColour(command.client.getOurUser(),command.guild));
        if (searched.size() < 40) {
            Utility.sendEmbedMessage("",embedBuilder,command.channel);
            return null;
        } else {
            String path = Constants.DIRECTORY_TEMP + command.messageSID + ".txt";
            FileHandler.writeToFile(path, Utility.listFormatter(list,true),false);
            File file = new File(path);
            Utility.sendFile(title, file, command.channel);
            try {
                Thread.sleep(4000);
                Files.delete(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SearchCCs"};
    }

    @Override
    public String description() {
        return "Allows you to search the custom command list.";
    }

    @Override
    public String usage() {
        return "[Search Params]";
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
