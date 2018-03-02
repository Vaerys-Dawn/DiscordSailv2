package com.github.vaerys.commands.admin;

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
import java.util.LinkedList;

public class ModNote extends Command {

    @Override
    public String execute(String args, CommandObject command) {

        // TODO: REFACTOR.

        // Start by breaking the arguments apart

        String userCall = new SplitFirstObject(args).getFirstWord();
        String argsRest = new SplitFirstObject(args).getRest();

        // Make sure both argument fields are not empty
        if (userCall == null) {
            return missingArgs(command);
        }
        if (argsRest == null) {
            argsRest = "list";
        }
        argsRest = argsRest.trim(); // trim excess spaces off so it doesn't be a derp.

        // make sure there is a valid user:
        UserObject user = Utility.getUser(command, userCall, false);
        if (user == null) return "> Could not find user.";

        // with a valid profile:
        ProfileObject profile = user.getProfile(command.guild);
        if (profile == null) return "> No profile found for " + user.displayName;

        // gather necessary extra bits
        long timestamp = command.message.getTimestamp().toEpochSecond();
        SplitFirstObject mode = new SplitFirstObject(argsRest);
        String modeString = mode.getFirstWord().toLowerCase();

        switch (modeString) {
            // Modes that do not require an index value
            case "list":
                return createListEmbed(profile, command);

            // Modes that require an index value:
            case "info":
            case "edit":
            case "del":
            case "delete":
            case "strike":
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
                    case "strike":
                        boolean strike = profile.modNotes.get(index - 1).getStrike();
                        if (strike) {
                            profile.modNotes.get(index - 1).setStrike(false);
                            return "> Strike cleared for note " + index + " for user " + user.displayName + ".";
                        } else {
                            profile.modNotes.get(index - 1).setStrike(true);
                            return "> Strike set for note " + index + " for user " + user.displayName + ".";
                        }
                    case "delete":
                    case "del":
                        profile.modNotes.remove(index - 1);
                        return "> Note #" + index + " for " + user.displayName + " deleted.";
                }
            default:
                if (profile.modNotes == null) profile.modNotes = new LinkedList<>();
                profile.modNotes.add(new ModNoteObject(argsRest, command.user.longID, timestamp));
                return String.format("> Note added for %s at index %d.", user.displayName, profile.modNotes.size());
        }
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

        return null; // no need to send a message, we're sending an embed instead.
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
        return SAILType.ADMIN;
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
