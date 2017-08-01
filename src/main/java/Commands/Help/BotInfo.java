package Commands.Help;


import Commands.CommandObject;
import Interfaces.Command;
import Main.Globals;
import Main.Utility;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class BotInfo implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        StringBuilder response = new StringBuilder();
        IUser creator = command.client.getUserByID(Globals.creatorID);
        builder.withTitle("Information about " + Globals.botName);
        response.append("Created by: **@" + creator.getName() + "#" + creator.getDiscriminator() + "**.");
        response.append("\nCreated entirely using Java 8 and the **[Discord4J Libraries](https://discord4j.com/)**.");
        response.append("\nSupport Sail on **[Patreon](https://www.patreon.com/DawnFelstar)**.");
        response.append("\nFind Sail on **[GitHub](https://github.com/Vaerys-Dawn/DiscordSailv2)**.");
        response.append("\nBot's Support Discord: **https://discord.gg/XSyQQrR**.");
        builder.withColor(Utility.getUsersColour(command.botUser, command.guild));
        builder.withDesc(response.toString());
        builder.withThumbnail(command.botUser.getAvatarURL());
        builder.withFooterText("Bot Version: " + Globals.version);
        Utility.sendEmbedMessage("", builder, command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"BotInfo"};
    }

    @Override
    public String description() {
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