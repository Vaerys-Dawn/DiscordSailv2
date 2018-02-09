package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class PixelHelp extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("Decay") && command.guild.config.xpDecay) return decay(command);
        if (args.equalsIgnoreCase("Rules")) return rules(command);
        SplitFirstObject obe = new SplitFirstObject(args);
        String mode = obe.getFirstWord();
        switch (mode.toLowerCase()) {
            case "leveltoxp":
            case "level2xp":
            case "level->xp":
                return levelToXp(obe.getRest());
            case "xptolevel":
            case "xp2level":
            case "xp->level":
                return xpToLevel(obe.getRest());
            default:
                StringBuilder builder = new StringBuilder();
                XEmbedBuilder embed = new XEmbedBuilder(command);
                embed.withTitle("Pixel System Information.");
                embed.withDescription("Pixels are " + command.client.bot.displayName + "'s" +
                        " form of xp, you can gain " + (int) (command.guild.config.xpRate * command.guild.config.xpModifier) + "xp" +
                        " once per minute by sending a message that meets all of the specific message rules.\n\n");
                if (command.guild.config.getRewardRoles().size() != 0) {
                    for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                        IRole reward = command.guild.getRoleByID(r.getRoleID());
                        if (reward == null) {
                            break;
                        }
                        builder.append("\n> **" + reward.getName() + "** -> Level: **" + r.getLevel() + "**");
                    }
                    embed.appendField("Reward Roles:", builder.toString(), true);
                }
                if (command.guild.config.xpModifier != 1) {
                    embed.appendField("\n**Current Xp Modifier:**\n", "> **x" + command.guild.config.xpModifier + "**.\n", true);
                }
                int random = new Random().nextInt(50);
                if (random == 1) {
                    embed.withThumbnail(Constants.RANK_UP_IMAGE_URL);
                } else {
                    embed.withThumbnail(Constants.LEVEL_UP_IMAGE_URL);
                }
                embed.appendField("Pixel and Level Calculators:", getModes(command) + "\n\n" + Utility.getCommandInfo(this, command), false);
                RequestHandler.sendEmbedMessage("", embed, command.channel.get());
                return null;
        }
    }

    private String rules(CommandObject command) {
        List<IChannel> channels = command.guild.getChannelsByType(CHANNEL_XP_DENIED);
        List<String> channelMentions = Utility.getChannelMentions(channels);
        String rules = "**The rules for gaining pixels are:**\n" +
                "> Cannot gain pixels if the message starts with a command prefix.\n" +
                "> Cannot gain pixels if the message contains less than 10 chars.\n" +
                "> Cannot gain pixels if your profile has NoXp or XpDenied on it.\n" +
                "> When submitting an image only, ignore the above rules.\n";
        if (channelMentions.size() != 0)
            rules += "> Cannot gain pixels in any of these channels: \n**>> " + Utility.listFormatter(channelMentions, true) + " <<**\n";
        rules += "> You can only gain one chunk of pixels a minute.\n\n";
        return rules;
    }

    private String decay(CommandObject command) {
        String rules = "";
        if (command.guild.config.xpDecay) {
            long maxDecay = (long) ((8) * (Globals.avgMessagesPerDay * command.guild.config.xpRate * command.guild.config.xpModifier) / 8);
            long minDecay = (long) ((Globals.avgMessagesPerDay * command.guild.config.xpRate * command.guild.config.xpModifier) / 8);
            long minMessageCount = (long) (minDecay / (command.guild.config.xpRate * command.guild.config.xpModifier));
            long messageCount = (long) (maxDecay / (command.guild.config.xpRate * command.guild.config.xpModifier));
            rules += "**How pixel decay works:** \n" +
                    "> Pixel decay starts after 7 days of no messages sent.\n" +
                    "> The value for the first day of decay is " + minDecay + " pixels.\n" +
                    "> Decay then increases at a rate of " + minDecay + " pixels per day before reaching a max of " + maxDecay + " pixels per day.\n" +
                    "> Decay only reaches its maximum after 15 days of inactivity.\n";
//                    "> The max decay value per day is " + maxDecay + " pixels or about " + messageCount + " messages worth of pixels.\n";
            if (command.guild.config.getRewardRoles().size() != 0) {
                rules += "> There is a level floor below every reward role which sits at 100 pixels below the pixels required to receive that role.\n" +
                        "> If you reach a multiple of 30 days and you are at a reward's pixel floor it will decay you past the level floor.\n" +
                        "> Decay cannot decay you past the lowest reward role.\n" +
                        "> Decay does not affect you if you are below the lowest reward role.\n";
            }
            rules += "> Any message regardless of size or channel will reset the decay timer.\n\n";
            return rules;
        } else {
            return "> Decay is not currently active on this server.";
        }
    }

    private String levelToXp(String args) {
        try {
            long level = Long.parseLong(args);
            if (level < 0) {
                return "> Please use a positive number.";
            }
            if (level > Constants.LEVEL_CAP) {
                return "> No, I don't want to calculate the total xp for level " + NumberFormat.getInstance().format(level) + "!";
            }
            return "> Level: " + level + " = " + NumberFormat.getInstance().format(XpHandler.totalXPForLevel(level)) + " pixels.";
        } catch (NumberFormatException e) {
            return "> You must supply a valid number.";
        }
    }

    private String xpToLevel(String args) {
        try {
            long xp = Long.parseLong(args);
            if (xp < 0) {
                return "> Please use a positive number.";
            }
            if (xp > Constants.PIXELS_CAP) {
                return "> Its something over level 1000, could you leave me alone.";
            }
            return "> " + NumberFormat.getInstance().format(xp) + "XP = Level: " + XpHandler.xpToLevel(xp);
        } catch (NumberFormatException e) {
            return "> You must supply a valid number.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"PixelHelp", "HelpPixels"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you information about pixels." + getModes(command);
    }

    public String getModes(CommandObject command) {
        String modes = "\n**Modes:** " +
                "\n> LevelToXp, Level2Xp, Level->Xp.\n" +
                "`Gives the total pixels for that level.`\n" +
                "> XpToLevel, Xp2level, Xp->level.\n" +
                "`Gives the level for that amount of pixels.`\n" +
                "> Rules\n" +
                "`Gives information about the rules messages must follow to gain pixels.`\n";
        if (command.guild.config.xpDecay) {
            modes += "> Decay\n" +
                    "`Gives information about the decay system.`\n";
        }
        return modes;
    }

    @Override
    public String usage() {
        return ("(Mode) (args)");
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
    }

    @Override
    public String channel() {
        return CHANNEL_PIXELS;
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
    public void init() {

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
