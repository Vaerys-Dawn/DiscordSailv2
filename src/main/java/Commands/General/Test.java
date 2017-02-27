package Commands.General;

import Commands.Command;
import Commands.CommandObject;
import Main.Utility;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        for (UserTypeObject u : command.guildUsers.getUsers()) {
            if (u.getID().equals(command.authorID)) {

                EmbedBuilder builder = new EmbedBuilder();
                List<IRole> roles = command.authorRoles;
                ArrayList<String> roleNames = new ArrayList<>();

                //sets title to user Display Name;
                builder.withTitle(command.authorDisplayName);

                //sets thumbnail to user Avatar.
                builder.withThumbnail(command.author.getAvatarURL());

                //gets the age of the account.
                long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - command.author.getCreationDate().atZone(ZoneOffset.UTC).toEpochSecond();


                //sets sidebar colour
                builder.withColor(command.authorColour);

                //collect role names;
                roleNames.addAll(roles.stream().filter(role -> !role.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList()));

                //builds desc
                builder.withDesc("Account Created: " + Utility.formatTimeDifference(difference) +
                        "\nGender: " + u.getGender() +
                        "\nRoles : " + Utility.listFormatter(roleNames, true) +
                        "\n\n*" + u.getQuote() + "*");

                // TODO: 27/02/2017 when xp system is implemented put xp and rank on the user card.

                //adds ID
                builder.withFooterText("User ID: " + u.getID());

                //sends Message
                Utility.sendEmbededMessage("", builder.build(), command.channel);
                return null;
            }
        }
        return "> An Error occurred.";
    }

    @Override
    public String[] names() {
        return new String[]{"Test", "Testing"};
    }

    @Override
    public String description() {
        return "Tests Things.";
    }

    @Override
    public String usage() {
        return "[Lol this command has no usages XD]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
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
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public String dualDescription() {
        return "This is another test.";
    }

    @Override
    public String dualUsage() {
        return "[Blep]";
    }

    @Override
    public String dualType() {
        return TYPE_ADMIN;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[]{Permissions.MANAGE_CHANNELS};
    }
}
