package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

public class TopUserForRole extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        // init index value
        int index = 1;

        // try to get role.
        Role role = GuildHandler.getRoleFromName(args, command.guild.get());
        if (role == null) {
            try {
                // if role get fails, try again, but this time assume the first "word" is the rank the globalUser wants to get.
                index = Integer.parseInt(new SplitFirstObject(args).getFirstWord());
            } catch (NumberFormatException e) {
                // not a valid number, can't find role, bork.
                return "\\> Invalid Role";
            }
            // remove index from string, try to get role again.
            args = new SplitFirstObject(args).getRest();

            role = GuildHandler.getRoleFromName(args, command.guild.get());
            if (role == null) return "\\> Invalid Role.";
        }

        Message working = command.guildChannel.sendMessage("`Working...`");

        // populate list with users with role defined.
        List<Long> userIDs = command.guild.get().getMembersWithRoles(role).stream().map(Member::getIdLong).collect(Collectors.toList());
        if (userIDs.isEmpty()) {
            working.delete().complete();
            return "\\> Could not find any users with that role!";
        }

        userIDs.removeIf(f -> PixelHandler.rank(command.guild.users, command.guild.get(), f) == -1);

        userIDs.sort((o1, o2) -> {
            long rank1 = PixelHandler.rank(command.guild.users, command.guild.get(), o1);
            long rank2 = PixelHandler.rank(command.guild.users, command.guild.get(), o2);
            return Long.compare(rank1, rank2);
        });
        if (userIDs.size() == 0) return "\\> Could not find any ranked users with that role.";

        if (index > userIDs.size()) {
            working.delete().complete();
            return "\\> There's only " + userIDs.size() + (userIDs.size() == 1 ? " user" : " users") + " with that role.";
        }

        if (index == 1) {
            // show an embed with the top (at most) 5 users instead.
            working.delete().complete();
            getEmbed(command, role, userIDs);
            return null;
        }

        ProfileObject topUserProfile = command.guild.users.getUserByID(userIDs.get(index - 1));
        UserObject topUser = topUserProfile.getUser(command.guild);

        NumberFormat nf = NumberFormat.getInstance();
        working.delete().complete();
        return "\\> @" + topUser.username + ", **Pixels:** " + nf.format(topUserProfile.getXP()) +
                ", **Level:** " + topUserProfile.getCurrentLevel() +
                ", **User ID:** " + topUser.longID;

    }

    private void getEmbed(CommandObject command, Role role, List<Long> userIDs) {
        XEmbedBuilder embed = new XEmbedBuilder(command);
        int showing = (userIDs.size() > 5 ? 5 : userIDs.size());

        embed.setTitle("Top " + (userIDs.size() == 1 ? " user" : showing + " users") + " for role " + role.getName());
        embed.setFooter("Total ranked users with this role: " + userIDs.size());
        ProfileObject userProfile;
        UserObject userObject;
        NumberFormat nf = NumberFormat.getInstance();

        String titlef = "#%s. %s";
        String contentf = "**XP:** %s\t**Level:** %s\n**UID:** %s";
        for (int i = 0; i < showing; i++) {
            userProfile = command.guild.users.getUserByID(userIDs.get(i));
            userObject = userProfile.getUser(command.guild);

            embed.addField(String.format(titlef, i + 1, userObject.displayName),
                    String.format(contentf, nf.format(userProfile.getXP()), userProfile.getCurrentLevel(), userObject.longID),
                    false);
        }

        embed.queue(command.guildChannel);
    }

    @Override
    protected String[] names() {
        return new String[]{"TopUserForRole", "TopUser"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gets the top user (Pixel wise) for a specific role.";
    }

    @Override
    protected String usage() {
        return "[Role Name]";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_ROLES};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
