package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

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
            return "\\> Could not find any custom commands that contains **" + args + "**.";
        }

        String title = "> Here is your search:";
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String contents = Utility.listFormatter(searched.stream().map(cCommandObject -> cCommandObject.getName(command)).collect(Collectors.toList()), true);
        if (contents.length() > 2040) {
            List<String> blah = new ArrayList<>();
            StringBuilder complete = new StringBuilder();
            complete.append("\\> Search for \"" + args + "\", Results found: " + searched.size() + "\n");
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
            command.channel.queueFile(title, complete.toString().getBytes(), String.format("Search_%s.txt", args));
            return null;
        } else {
            embedBuilder.setTitle(title);
            embedBuilder.setDescription("```\n" + contents + spacer + "```");
            embedBuilder.queue(command);
            embedBuilder.setFooter("Results Found: " + searched.size());
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
    protected Permission[] perms() {
        return new Permission[0];
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
