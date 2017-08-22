package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
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
        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
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
            list.add(command.guild.config.getPrefixCC() + c.getName());
        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder();
        Utility.listFormatterEmbed(title,embedBuilder,list,true);
        embedBuilder.withColor(command.client.color);
        if (searched.size() < 40) {
            Utility.sendEmbedMessage("",embedBuilder,command.channel.get());
            return null;
        } else {
            String path = Constants.DIRECTORY_TEMP + command.message.stringID + ".txt";
            FileHandler.writeToFile(path, Utility.listFormatter(list,true),false);
            File file = new File(path);
            Utility.sendFile(title, file, command.channel.get());
            try {
                Thread.sleep(4000);
                Files.delete(Paths.get(path));
            } catch (IOException e) {
                Utility.sendStack(e);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
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
