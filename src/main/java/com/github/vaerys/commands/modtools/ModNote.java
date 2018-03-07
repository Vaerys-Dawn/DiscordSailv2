package com.github.vaerys.commands.modtools;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ModNoteObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ModNote extends Command {

    private static final List<String> specialModes = new ArrayList<>(Arrays.asList(
            "edit",
            "info",
            "delete", "del", "remove", "rem",
            "strike"));

    @Override
    public String execute(String args, CommandObject command) {

        // Start by breaking the arguments apart
        SplitFirstObject argsSplitter = new SplitFirstObject(args);

        String userCall = argsSplitter.getFirstWord();
        String opts = argsSplitter.getRest();
        // empty user arg is not allowed;
        if (userCall.isEmpty()) return missingArgs(command);
        if (opts == null || opts.isEmpty()) opts = "list";

        UserObject user = Utility.getUser(command, userCall, false);
        ProfileObject profile = user.getProfile(command.guild);

        //if (user == null) return "Could not find user.";
        if (profile == null) return "> No profile found for " + user.displayName + ".";

        long timestamp = command.message.getTimestamp().toEpochSecond();
        String mode = new SplitFirstObject(opts).getFirstWord().toLowerCase();

        if (mode.equals("list")) {
            return createListEmbed(profile, command);
        } else if (specialModes.contains(mode)) {
            // these modes require special handling:
            String modeOpts = new SplitFirstObject(opts).getRest();
            if (modeOpts.isEmpty()) return missingArgs(command);

            // try to parse an index:
            int index;
            try {
                index = Integer.parseInt(modeOpts);
                if (profile.modNotes == null || profile.modNotes.size() == 0) {
                    return "> " + user.displayName + " doesn't have any notes yet.";
                }
                if (index <= 0 || index > profile.modNotes.size()) {
                    return "> Index out of bounds.";
                }
            } catch (NumberFormatException e) {
                return "> I wasn't able to understand what you asked me.";
            }

            // cache modNotes list
            List<ModNoteObject> modNotes = profile.modNotes;
            switch (mode) {
                // "edit" mode
                case "edit":
                    String newNote = new SplitFirstObject(modeOpts).getRest();

                    modNotes.get(index - 1).editNote(newNote, command.user.longID, timestamp);
                    return "> Note #" + index + " edited for user " + user.displayName + ".";

                // "info" mode
                case "info":
                    createInfoEmbed(profile, command, index - 1);
                    return null;

                // strike
                case "strike":
                    boolean strike = modNotes.get(index - 1).getStrike();
                    if (strike) {
                        modNotes.get(index - 1).setStrike(false);
                        return "> Strike cleared for note " + index + " for user " + user.displayName + ".";
                    } else {
                        modNotes.get(index - 1).setStrike(true);
                        return "> Strike set for note " + index + " for user " + user.displayName + ".";
                    }

                    // "delete" command
                case "delete":
                case "del":
                case "remove":
                case "rem":
                    modNotes.remove(index - 1);
                    return "> Note #" + index + " for " + user.displayName + " deleted.";
            }
        } else {
            if (profile.modNotes == null) profile.modNotes = new LinkedList<>();
            profile.modNotes.add(new ModNoteObject(opts, command.user.longID, timestamp));
            return String.format("> Note added for %s at index %d.", user.displayName, profile.modNotes.size());
        }
        return "> This code should be unreachable.";
    }

    private String createListEmbed(ProfileObject user, CommandObject command) {
        if (user.modNotes == null || user.modNotes.size() == 0) {
            return "> " + user.getUser(command.guild).displayName + " doesn't have any notes yet.";
        }

        UserObject userObject = user.getUser(command.guild);
        XEmbedBuilder builder = new XEmbedBuilder(command);

        builder.withColor(userObject.color);
        builder.withTitle("Notes for " + userObject.displayName);
        builder.withThumbnail(userObject.get().getAvatarURL());

        // get all notes and put together the bits and bobs
        int counter = 0;
        String noteLine = "**Note #%d:**\n%s\n";
        StringHandler content = new StringHandler();

        for (ModNoteObject noteObject : user.modNotes) {
            String shortNote = Utility.truncateString(Utility.removeFun(noteObject.getNote()), 55);
            if (noteObject.getStrike()) {
                content.append("⚠ ");
            }
            content.append(String.format(noteLine, ++counter, shortNote));
        }
        builder.withDesc(content.toString());

        // finalize and send message:
        builder.withFooterText("Total Notes: " + user.modNotes.size());
        builder.send(command.channel);
        return null;
    }

    private void createInfoEmbed(ProfileObject user, CommandObject command, int index) {
        UserObject userObject = user.getUser(command.guild);
        XEmbedBuilder builder = new XEmbedBuilder(userObject);
        ModNoteObject noteObject = user.modNotes.get(index);

        // title and avatar of user in question.
        if (noteObject.getStrike()) {
            builder.withTitle("⚠ Note " + (index + 1) + " - " + userObject.displayName);
        } else {
            builder.withTitle("Note " + (index + 1) + " - " + userObject.displayName);
        }
        builder.withThumbnail(userObject.get().getAvatarURL());

        // add note to embed
        builder.appendDesc(noteObject.getNote());
        // Get a UserObject from the stored ID to add to the embed.
        UserObject creator = new UserObject(command.guild.getUserByID(noteObject.getCreatorId()), command.guild);
        builder.withFooterText("Created by " + creator.displayName);
        builder.withFooterIcon(creator.get().getAvatarURL());
        builder.withTimestamp(noteObject.getTimestamp() * 1000);

        if (noteObject.getEditorId() != -1) {
            // get editor's info and display it?
            UserObject editor = new UserObject(command.guild.getUserByID(noteObject.getEditorId()), command.guild);
            String editFieldText = "\n\n*Last edited by %s %s*";
            long diff = command.message.getTimestamp().toEpochSecond() - noteObject.getLastEditedTimestamp();
            if (diff >= 86400 * 7) { // 7d
                String editDate = new SimpleDateFormat("dd/MMM/yyyy").format(noteObject.getLastEditedTimestamp() * 1000);
                builder.appendDesc(String.format(editFieldText, editor.displayName, "on " + editDate));
            } else {
                builder.appendDesc(String.format(editFieldText, editor.displayName, Utility.formatTimeDifference(diff)));
            }
        }

        builder.send(command.channel);
    }

    @Override
    public String description(CommandObject command) {
        return "Allows staff members to create and manage notes about users\n\n" +
                "**Modes**\n" +
                "> `list` - List any notes attached to **[@User]**.\n" +
                "> `edit [index] [new note]` - Edit an existing note.\n" +
                "> `strike [index]` - Toggle the strike status of an existing note.\n" +
                "> `info [index]` - Show details of a specific note.\n" +
                "> `delete [index]` - Remove an existing note.\n";
    }

    @Override
    public void init() {

    }

    @Override
    protected String[] names() {
        return new String[]{"ModNote", "Punish", "ModNotes"};
    }

    @Override
    protected String usage() {
        return "[@User] (Mode)|[Note...]";
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

}
