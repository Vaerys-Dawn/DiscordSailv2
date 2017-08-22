package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.RoleStatsObject;
import com.github.vaerys.pogos.GuildConfig;
import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class GetGuildInfo implements Command{

    IGuild guild;
    IUser author;
    GuildConfig config;

    @Override
    public String execute(String args, CommandObject command) {
        guild = command.guild.get();
        author = command.user.get();
        config = command.guild.config;
        String guildName = guild.getName();
        LocalDateTime creationDate = guild.getCreationDate();
        IUser guildOwner = guild.getOwner();
        IRegion region = guild.getRegion();
        List<IRole> roles = guild.getRoles();
        StringBuilder builder = new StringBuilder();
        ArrayList<String> cosmeticRoleStats = new ArrayList<>();
        ArrayList<String> modifierRoleStats = new ArrayList<>();
        ArrayList<String> trustedRoleStats = new ArrayList<>();
        int totalCosmetic = 0;
        int totalModified = 0;
        int totalTrusted = 0;
        boolean manageRoles = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_ROLES}, author, guild) || author.getStringID().equals(Globals.creatorID);
        boolean manageServer = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_SERVER}, author, guild) || author.getStringID().equals(Globals.creatorID);
        boolean manageChannels = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_CHANNELS}, author, guild) || author.getStringID().equals(Globals.creatorID);

        Utility.sendMessage("> Info will be sent to you via Direct Message.",command.channel.get());
        Utility.sendFileURL("",guild.getIconURL(),command.client.get().getOrCreatePMChannel(command.user.get()),false);
        builder.append("***[" + guildName.toUpperCase() + "]***");
        builder.append("\n\n> Guild ID : **" + guild.getLongID());
        builder.append("**\n> Creation Date : **" + creationDate.getYear() + " " + creationDate.getMonth() + " " + creationDate.getDayOfMonth() + " - " + creationDate.getHour() + ":" + creationDate.getMinute());
        builder.append("**\n> Guild Owner : **@" + guildOwner.getName() + "#" + guildOwner.getDiscriminator());
        builder.append("**\n> Command Prefix: **"+  config.getPrefixCommand());
        builder.append("**\n> Custom Command Prefix: **" + config.getPrefixCC() + "**");
        if (region != null) {
            builder.append("\n> Region : **" + region.getName() + "**");
        }
        builder.append("\n> Total Members: **" + guild.getUsers().size() + "**");
        if (manageServer) {
            ArrayList<String> toggles = new ArrayList<>();
            ArrayList<String> modules = new ArrayList<>();
            for (GuildToggle t : command.guild.toggles){
                String formatted = "\n> " + t.name() + " = **" + t.get(command.guild.config) + "**";
                if (t.isModule()){
                    modules.add(formatted);
                }else {
                    toggles.add(formatted);
                }
            }
            Collections.sort(toggles);
            Collections.sort(modules);
            builder.append("\n\n***[ADMIN INFO]***");
            builder.append("\n\n**GUILD TOGGLES**");
            toggles.forEach(builder::append);
            builder.append("\n\n**GUILD MODULES**");
            modules.forEach(builder::append);
            builder.append("\n\n**GUILD DATA**");
            builder.append("\n> Max Mentions: **" + config.maxMentionLimit + "**");
            builder.append("\n> Rate Limit: **" + config.messageLimit + "/10s**");
        }
        // TODO: 09/04/2017 add this back in, make it display channels better.
//        if (manageChannels) {
//            builder.append("\n\n***[CHANNELS]***");
//            for (ChannelTypeObject c : config.getChannels()) {
//                builder.append("\n> " + c.getType() + " = **#" + guild.getChannelByID(c.getStringID()).getName() + "**");
//            }
//        }

        builder.append("\n\n***[ROLES]***");
        ArrayList<RoleStatsObject> statsObjects = new ArrayList<>();
        for (IRole r : roles) {
            if (!r.isEveryoneRole()) {
                statsObjects.add(new RoleStatsObject(r, config, guild.getUsersByRole(r).size()));
            }
        }
        for (RoleStatsObject rso : statsObjects) {
            StringBuilder formatted = new StringBuilder();
            formatted.append("\n> **" + rso.getRoleName() + "**");
            if (manageRoles) {
                formatted.append(" Colour : \"**" + rso.getColour() + "**\",");
            }
            formatted.append(" Total Users: \"**" + rso.getTotalUsers() + "**\"");
            if (rso.isCosmetic()) {
                cosmeticRoleStats.add(formatted.toString());
                totalCosmetic += rso.getTotalUsers();
            }
            if (rso.isModifier()) {
                modifierRoleStats.add(formatted.toString());
                totalModified += rso.getTotalUsers();
            }
            if (rso.isTrusted()){
                trustedRoleStats.add(formatted.toString());
                totalTrusted += rso.getTotalUsers();
            }
        }
        Collections.sort(cosmeticRoleStats);
        Collections.sort(modifierRoleStats);
        builder.append("\n\n**TRUSTED ROLES**");
        for (String s : trustedRoleStats) {
            if (builder.length() > 1800) {
                Utility.sendDM(builder.toString(), author.getStringID());
                builder.delete(0, builder.length());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }
            }
            builder.append(s);
        }
        builder.append("\n > Total profiles : \"**" + totalTrusted + "**\"");
        builder.append("\n\n**COSMETIC ROLES**");
        for (String s : cosmeticRoleStats) {
            if (builder.length() > 1800) {
                Utility.sendDM(builder.toString(), author.getStringID());
                builder.delete(0, builder.length());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }
            }
            builder.append(s);
        }
        builder.append("\n > Total profiles : \"**" + totalCosmetic + "**\"");
        builder.append("\n\n**MODIFIER ROLES**");
        for (String s : modifierRoleStats) {
            if (builder.length() > 1800) {
                Utility.sendDM(builder.toString(), author.getStringID());
                builder.delete(0, builder.length());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }

            }
            builder.append(s);
        }
        builder.append("\n > Total profiles : \"**" + totalModified + "**\"");
        builder.append("\n\n------{END OF INFO}------");
        Utility.sendDM(builder.toString(), author.getStringID());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"GetGuildInfo"};
    }

    @Override
    public String description() {
        return "Sends Information about the server to your Direct Messages.";
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
