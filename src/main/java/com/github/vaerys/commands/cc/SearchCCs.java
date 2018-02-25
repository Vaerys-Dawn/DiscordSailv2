package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class SearchCCs extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<CCommandObject> searched = new ArrayList<>();
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
        if (searched.size() == 0) {
            return "> Could not find any custom commands that contains **" + args + "**.";
        }

        String title = "> Here is your search:";
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String contents = Utility.listFormatter(searched.stream().map(cCommandObject -> cCommandObject.getName(command)).collect(Collectors.toList()), true);
        if (contents.length() > 2040) {
            List<String> blah = new ArrayList<>();
            StringBuilder complete = new StringBuilder();
            for (CCommandObject c : searched) {
                if (blah.size() == 8) {
                    complete.append(Utility.listFormatter(blah, true));
                    blah = new ArrayList<>();
                    complete.replace(complete.length() - 1, complete.length(), ",");
                    complete.append("\n");
                }
                blah.add(c.getName(command));
            }
            if (blah.size() != 0) {
                complete.append(Utility.listFormatter(blah, true));
            }
            if (complete.toString().endsWith(",\n")) {
                complete.replace(complete.length() - 2, complete.length() - 1, ".");
            }
            String path = Constants.DIRECTORY_TEMP + command.message.longID + ".txt";
            FileHandler.writeToFile(path, complete.toString(), false);
            File file = new File(path);
            RequestHandler.sendFile(title, file, command.channel.get());
            try {
                Thread.sleep(4000);
                Files.delete(Paths.get(path));
            } catch (IOException e) {
                Utility.sendStack(e);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
            }
            return null;
        } else {
            embedBuilder.withTitle(title);
            embedBuilder.withDesc("```\n" + contents + spacer + "```");
            RequestHandler.sendEmbedMessage("", embedBuilder, command.channel.get());
            return null;
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SearchCCs"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to search the custom command list.";
    }

    @Override
    protected String usage() {
        return "[Search Params]";
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
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
