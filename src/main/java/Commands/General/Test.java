package Commands.General;

import Commands.Command;
import Commands.CommandObject;
import Main.Utility;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        EmbedBuilder builder = new EmbedBuilder();
        ArrayList<IRole> cosmeticRoles = new ArrayList<>();
        ArrayList<String> roleNames = new ArrayList<>();
        builder.withTitle(command.authorDisplayName);
        builder.withThumbnail(command.author.getAvatarURL());
        long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - command.author.getCreationDate().atZone(ZoneOffset.UTC).toEpochSecond();
        cosmeticRoles.addAll(command.authorRoles.stream().filter(role -> command.guildConfig.isRoleCosmetic(role.getID())).collect(Collectors.toList()));
        if (cosmeticRoles.size() > 0){
            builder.withColor(Utility.getUsersColour(cosmeticRoles,command.guild));
        }else {
            builder.withColor(command.authorColour);
        }
        roleNames.addAll(cosmeticRoles.stream().map(IRole::getName).collect(Collectors.toList()));
        builder.withDesc("Age: " + Utility.formatTimeDifference(difference) +
                "\nGender: ERROR\n" +
                Utility.listFormatter(roleNames,true) +
                "\nDesc: "+ args);
        Utility.sendEmbededMessage("",builder.build(),command.channel);
        return null;
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
