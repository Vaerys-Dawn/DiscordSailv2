package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.TagHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class RandomCC implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        String prefixEmbedImage = "<embedImage>{";
        String tagDeleteMessage = "<delCall>";
        Random random = new Random();
        int counter = 0;
        List<CCommandObject> commands = command.guild.customCommands.getCommandList();
        CCommandObject randCC = commands.get(random.nextInt(commands.size()));
        while (!command.channel.settings.contains(CHANNEL_SHITPOST) && randCC.isShitPost() && command.guild.config.shitPostFiltering) {
            if (counter > 25) {
                return "> Your server has way to many shitpost commands, I couldn't find you a normal one.";
            }
            randCC = commands.get(random.nextInt(commands.size()));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;
        }
        String response = randCC.getContents(true);
        int argsCount = StringUtils.countMatches(response, "<args>");
        if (argsCount != 0) {
            if (args.length() * argsCount > Globals.argsMax) {
                return "> Args to large for this command. Max args size : " + Globals.argsMax;
            }
        }
        response = TagHandler.tagSystem(response, command, args);
        response = TagHandler.tagMentionRemover(response);
        response = response.replace("<DELCALL>", "<delCall>");
        response = response.replace("<EMBEDIMAGE>", "<embedImage>");
        response = response + "\n`" + command.guild.config.getPrefixCC() + randCC.getName() + "`";
        if (command.guild.customCommands.checkblackList(response) != null) {
            return command.guild.customCommands.checkblackList(response);
        }
        if (response.contains(tagDeleteMessage)) {
            response = response.replace(tagDeleteMessage, "");
//            Utility.deleteMessage(command.message.get());
        }
        if (response.contains("<embedImage>{")) {
            String imageURL = TagHandler.tagEmbedImage(response, prefixEmbedImage);
            if (imageURL != null || !imageURL.isEmpty()) {
                if (command.channel.get().getModifiedPermissions(command.user.get()).contains(Permissions.EMBED_LINKS)) {
                    response = response.replaceFirst(Pattern.quote(prefixEmbedImage + imageURL + "}"), "");
                    response = TagHandler.tagToCaps(response);
                    Utility.sendFileURL(response, imageURL, command.channel.get(), true);
                    return null;
                } else {
                    response = response.replaceFirst(Pattern.quote(prefixEmbedImage + imageURL + "}"), "<" + imageURL + ">");
                    return response;
                }
            }
        }
        return response;
    }

    @Override
    public String[] names() {
        return new String[]{"RandomCC","RandCC"};
    }

    @Override
    public String description() {
        return "Gives you a random cc";
    }

    @Override
    public String usage() {
        return "(args)";
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