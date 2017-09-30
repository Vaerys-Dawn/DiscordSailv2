package com.github.vaerys.commands.help;


import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class BotInfo implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        StringBuilder response = new StringBuilder();
        IUser creator = command.client.get().fetchUser(153159020528533505L);
        builder.withTitle("Information about " + Globals.botName);
        response.append("Created by: **@" + creator.getName() + "#" + creator.getDiscriminator() + "**.");
        response.append("\nCreated entirely using Java 8 and the **[Discord4J Libraries](https://discord4j.com/)**.");
        response.append("\nSupport " + Globals.botName + " on **[Patreon](https://www.patreon.com/DawnFelstar)**.");
        response.append("\nFind " + Globals.botName + " on **[GitHub](https://github.com/Vaerys-Dawn/DiscordSailv2)**.");
        response.append("\nBot's Support Discord: **https://discord.gg/XSyQQrR**.");
        response.append("\n\nBy inviting " + Globals.botName + " to your server you are to understand that " + Globals.botName + " will only store data on user's request. " +
                "The only exception to this is the bot's profile system which stores the user's Id after they send a messages to the server, " +
                "this is so that " + Globals.botName + " can associate later user created data to that user.");
        builder.withColor(command.client.color);
        builder.withDesc(response.toString());
        builder.withThumbnail(command.client.bot.getAvatarURL());
        builder.withFooterText("Bot Version: " + Globals.version);
        Utility.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"BotInfo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives Information about the bot.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_HELP;
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