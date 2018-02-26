package com.github.vaerys.commands.help;


import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class BotInfo extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        StringBuilder response = new StringBuilder();
        IUser creator = command.client.fetchUser(153159020528533505L);
        IUser andriel = command.client.fetchUser(175442602508812288L);
        IUser c0bra = command.client.fetchUser(222041304761237505L);
        builder.withTitle("Information about " + Globals.botName);
        response.append("Created by: **@" + creator.getName() + "#" + creator.getDiscriminator() + "**.");
        response.append("\nCreated entirely using Java 8 and the **[Discord4J Libraries](https://discord4j.com/)**.");
        response.append("\nSupport " + Globals.botName + " on **[Patreon](https://www.patreon.com/DawnFelstar)**.");
        response.append("\nFind " + Globals.botName + " on **[GitHub](https://github.com/Vaerys-Dawn/DiscordSailv2)**.");
        response.append("\nBot's Support Discord: **https://discord.gg/XSyQQrR**.");
        response.append("\n\nContributors: **@" + andriel.getName() + "#" + andriel.getDiscriminator() + "**, ");
        response.append("**@").append(c0bra.getName()).append("#").append(c0bra.getDiscriminator()).append("**.");
        builder.withDesc(response.toString());
        builder.withThumbnail(command.client.bot.getAvatarURL());
        builder.withFooterText("Bot Version: " + Globals.version);
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"Info", "BotInfo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives Information about the bot.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
