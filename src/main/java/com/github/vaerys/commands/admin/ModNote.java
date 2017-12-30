package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ModNoteObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.Permissions;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class ModNote implements Command {

    private static final Logger logger = LoggerFactory.getLogger(ModNote.class);

    @Override
    public String execute(String args, CommandObject command) {
        // Start by breaking the arguments apart
        String userCall = new SplitFirstObject(args).getFirstWord();
        String argsRest = new SplitFirstObject(args).getRest();

        // Make sure both argument fields are not empty
        if (argsRest == null || userCall == null) {
            return missingArgs(command);
        }

        // make sure there is a valid user:
        UserObject user = Utility.getUser(command, userCall, false);
        if (user == null) return "> Could not find user.";

        // with a valid profile:
        ProfileObject profile = user.getProfile(command.guild);
        if (profile == null) return "> No profile found for " + user.displayName;

        // gather necessary extra bits
        long timestamp = command.message.get().getTimestamp().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
        logger.debug("message ts: " + timestamp);
        SplitFirstObject mode = new SplitFirstObject(argsRest);

        // Handle Command execution
        if (mode.getFirstWord().equalsIgnoreCase("list")) {
            return createListEmbed(profile, command);

        } else if (mode.getFirstWord().equalsIgnoreCase("info") ||
                mode.getFirstWord().equalsIgnoreCase("edit") ||
                mode.getFirstWord().equalsIgnoreCase("del") ||
                mode.getFirstWord().equalsIgnoreCase("delete")) {

            if (mode.getRest() == null) return missingArgs(command);
            String modeArg = new SplitFirstObject(mode.getRest()).getFirstWord();

            int index;
            try {
                index = Integer.parseInt(modeArg);
                if (profile.modNotes == null || profile.modNotes.size() == 0) {
                    return "> " + user.displayName + " doesn't have any notes yet.";
                }
                if (index <= 0 || index > profile.modNotes.size()) {
                    return "> Index out of bounds.";
                }
            } catch (NumberFormatException e) {
                return "> I wasn't able to understand what you asked me.";
            }

            switch (mode.getFirstWord()) {
                case "info":
                    createInfoEmbed(profile, command, index - 1);
                    return null;

                case "edit":
                    String newNote = new SplitFirstObject(mode.getRest()).getRest();

                    profile.modNotes.get(index - 1).editNote(newNote, command.user.longID, timestamp);
                    return "> Note #" + index + " edited for user " + user.displayName + ".";

                case "delete":
                case "del":
                    profile.modNotes.remove(index - 1);
                    return "> Note #" + index + " for " + user.displayName + " deleted.";
            }
        } else {
            if (profile.modNotes == null) profile.modNotes = new ArrayList<>();
            profile.modNotes.add(new ModNoteObject(argsRest, command.user.longID, timestamp));
            return String.format("> Note added for %s at index %d.", user.displayName, profile.modNotes.size());
        }
        return "> What?";
    }

    private String createListEmbed(ProfileObject user, CommandObject command) {
        if (user.modNotes == null || user.modNotes.size() == 0) {
            return "> " + user.getUser(command.guild).displayName + " doesn't have any notes yet.";
        }

        UserObject userObject = user.getUser(command.guild);
        XEmbedBuilder builder = new XEmbedBuilder(command);

        builder.withColor(userObject.color);
        builder.withAuthorName("Notes for " + userObject.displayName);
        builder.withThumbnail(userObject.get().getAvatarURL());

        // get all notes and put together the bits and bobs
        int counter = 0;
        String noteLine = "**Note #%d:**\n%s\n";
        StringBuilder content = new StringBuilder();

        for (ModNoteObject noteObject : user.modNotes) {
            String shortNote = Utility.truncateString(noteObject.getNote(), 35);
            content.append(String.format(noteLine, ++counter, shortNote));
        }
        builder.withDesc(content.toString());

        // finalize and send message:
        builder.withFooterText("Total Notes: " + user.modNotes.size());
        builder.send(command.channel.get());

        return null; // no need to send a message, we're sending an embed instead.
    }

    private void createInfoEmbed(ProfileObject user, CommandObject command, int index) {
        UserObject userObject = user.getUser(command.guild);
        XEmbedBuilder builder = new XEmbedBuilder(userObject);
        ModNoteObject noteObject = user.modNotes.get(index);

        // title and avatar of user in question.
        builder.withAuthorName("Note " + (index + 1) + " - " + userObject.displayName);
        builder.withAuthorIcon(userObject.get().getAvatarURL());

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
            String editFieldText = "\n\n*Last edited by %s %s ago.*";
            long diff = (System.currentTimeMillis() / 1000) - noteObject.getLastEditedTimestamp();
            builder.appendDesc(String.format(editFieldText, editor.displayName, Utility.formatTime(diff, true)));
        }

        builder.send(command.channel.get());
    }

    //region Command Details
    @Override
    public String[] names() {
        return new String[]{"ModNote", "Punish", "RecordIncident"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows staff members to create and manage notes about users\n\n" +
                "**Modes**\n" +
                "> `list` - List any notes attached to **[@User]**.\n" +
                "> `edit [index] [new note]` - Edit an existing note.\n" +
                "> `info [index]` - Show details of a specific note.\n" +
                "> `delete [index]` - Remove an existing note.\n";
    }

    @Override
    public String usage() {
        return "[@User] (Mode)|[Note ...]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    //region dual-mode(unused)
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
    //endregion
    //endregion
}
