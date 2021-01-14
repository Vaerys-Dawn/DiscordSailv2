package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.commands.admin.Test;
import com.github.vaerys.commands.creator.directmessages.Echo;
import com.github.vaerys.commands.general.Hello;
import com.github.vaerys.commands.general.Patreon;
import com.github.vaerys.commands.help.Ping;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.objects.utils.SubCommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class NewCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_MAKE_CC)) {
            return "\\> " + command.user.mention() + ", You have been denied the creation of custom commands.";
        }
        if (command.guild.getChannelsByType(ChannelSetting.CC_DENIED).contains(command.guildChannel.get()))
            return "\\> This Channel has CCs Denied, You cannot create ccs here.";
        boolean isShitpost;
        boolean isLocked = false;
        SplitFirstObject splitFirst = new SplitFirstObject(args);
        List<TextChannel> shitpostChannels = command.guild.getChannelsByType(ChannelSetting.SHITPOST);

        isShitpost = shitpostChannels.contains(command.guildChannel);

        if (object.getSettings().contains(UserSetting.AUTO_SHITPOST)) {
            isShitpost = true;
        }

        String nameCC = splitFirst.getFirstWord();
        String argsCC = splitFirst.getRest();


        // cc validation
        if (handleNameFilter(command, nameCC)) {
            return "\\> Custom Commands cannot have the same name as built-in commands.";
        }
        if (!Pattern.matches("[\\p{Alnum}\\p{Punct}&&[^#@$*\\\\/`]]+", nameCC)) {
            return "\\> Custom Command names cannot contain special characters.";
        }
        if ((argsCC == null || argsCC.isEmpty()) && command.message.get().getAttachments().size() == 0) {
            return "\\> Custom command contents cannot be blank.";
        }
        if (nameCC.length() > 50) {
            return "\\> Command name too long.";
        }
        if (nameCC.isEmpty()) {
            return "\\> Command name cannot be empty.";
        }
        CCommandObject existing = command.guild.customCommands.getCommand(nameCC);
        if (existing != null){
            if (existing.getUserID() == command.user.longID) {
                return String.format("> You have already created the custom command **%s**, Please use `%s %s [contents]` instead.", nameCC, get(EditCC.class).getCommand(command), nameCC);
            }
            return "\\> Command name already in use.";
        }

        if (command.message.get().getAttachments().size() != 0) {
            String testLink = command.message.get().getAttachments().get(0).getUrl();
            if (Utility.isImageLink(testLink)) {
                if (argsCC == null || argsCC.isEmpty()) {
                    argsCC = "<embedImage>{" + testLink + "}";
                } else {
                    argsCC += "<embedImage>{" + testLink + "}";
                }
            } else {
                return "\\> Custom command attachment must be a valid Image.";
            }
        }
        if (StringUtils.countMatches(argsCC, "<embedImage>{") > 1) {
            return "\\> Custom Commands Cannot have multiple <embedImage> tags";
        }

        if (nameCC.contains("\n")) {
            return "\\> Command name cannot contain Newlines.";
        }
        if (argsCC.contains("<shitpost>")) {
            argsCC.replace("<shitpost>", "");
            isShitpost = true;
        }
        if (argsCC.contains("<lock>") && GuildHandler.testForPerms(command, Permission.MESSAGE_MANAGE)) {
            argsCC.replace("<lock>", "");
            isLocked = true;
        }

        // add new cc
        int counter = 0;
        int limitCCs;
        limitCCs = command.guild.customCommands.maxCCs(command.user, command.guild);

        if (counter < limitCCs) {
            command.guild.customCommands.addCommand(isLocked, command.user.longID, nameCC, argsCC, isShitpost);
            long remaining = limitCCs - counter - 1;
            String response = "\\> Command Added, you have ";
            if (remaining > 1) {
                response += remaining + " custom command slots left.\n";
            } else {
                response += remaining + " custom command slot left.\n";
            }
            response += Constants.PREFIX_INDENT + "You can run your new command by performing `" + command.guild.config.getPrefixCC() + nameCC + "`.";
            return response;

        } else {
            return "\\> You have run out of custom command slots. You can make room by deleting or editing old custom commands.";
        }
    }

    private boolean handleNameFilter(CommandObject command, String nameCC) {
        List<Command> exceptions = new ArrayList<Command>() {{
            add(Command.get(Test.class));
            add(Command.get(Hello.class));
            add(Command.get(Patreon.class));
            add(Command.get(Echo.class));
            add(Command.get(Ping.class));
            addAll(CommandList.getCommandsByType(SAILType.SLASH));
        }};
        // ccs cannot have names that match existing commands:
        List<Command> toTest = new ArrayList<>(command.guild.commands);
        toTest.removeAll(exceptions);

        for (Command c : toTest) {

            // get all commands names.
            List<String> names = new ArrayList<>(Arrays.asList(c.names));
            for (SubCommandObject sc : c.subCommands) {
                names.addAll(Arrays.asList(sc.getNames()));
            }

            // convert them to lowercase.
            ListIterator<String> li = names.listIterator();
            while (li.hasNext()) {
                li.set(li.next().toLowerCase());
            }

            // do check
            if (names.contains(nameCC.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String[] names() {
        return new String[]{"NewCC", "CCNew"};
    }

    @Override
    public String description(CommandObject command) {
        return "Creates a Custom Command. (Max Command Name length: 50 chars)\n" +
                "\n" +
                "Commands are created on a per server basis.";
    }

    @Override
    protected String usage() {
        return "[Command Name] [Contents/Image]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CC;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.MANAGE_CC;
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
