package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.masterobjects.GlobalUserObject;
import com.github.vaerys.templates.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RequestData extends Command {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String execute(String args, CommandObject command) {
        if (getRequest(new GlobalUserObject(command.user.longID), command.guildChannel.getMessageChannel())) {
            return "\\> Check dms for a file containing your requested data, it may take a moment.";
        } else {
            return "\\> You have sent a sent a data request recently, you will need to wait 3 hours between data requests";
        }
    }

    private boolean getRequest(GlobalUserObject user, MessageChannel messageChannel) {
        if (Globals.getGlobalData().canSendDataRequest(user.longID) || user.checkIsCreator()) {
            Thread thread = new Thread(() -> {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ZipOutputStream stream = new ZipOutputStream(out)){
                    addZipEntry("custom_commands.json", user.getCustomCommands(), stream);
                    addZipEntry("characters.json", user.getCharacters(), stream);
                    addZipEntry("servers.json", user.getServers(), stream);
                    addZipEntry("daily_messages.json", user.getDailyMessages(), stream);
                    addZipEntry("profiles.json", user.getProfiles(), stream);
                    addZipEntry("reminders.json", user.getReminders(), stream);

                    stream.finish();
                    InputStream data = new ByteArrayInputStream(out.toByteArray());

                    user.get().openPrivateChannel().complete().sendFiles(FileUpload.fromData(data, "User Data.zip")).complete();
                } catch (ErrorResponseException | IOException e) {
                    messageChannel.sendMessage("\\> An error occurred when trying to process your data request").complete();
                }
            });
            thread.start();

            return true;
        } else {
            return false;
        }
    }

    private void addZipEntry (String fileName, Object toCompress, ZipOutputStream stream) throws IOException {
        if (toCompress == null) return;
        if (toCompress instanceof ArrayList && (((ArrayList<?>) toCompress).isEmpty())) return;
        ZipEntry entry = new ZipEntry(fileName);
        stream.putNextEntry(entry);
        stream.write(gson.toJson(toCompress).getBytes());
        stream.closeEntry();
    }

    @Override
    public String executeDm(String args, DmCommandObject command) {
        if (getRequest(command.globalUser, command.messageChannel.getMessageChannel())) {
            return "\\> Your request has been sent please wait, your data will be sent here when it is compiled.";
        }else {
            return "\\> You have sent a sent a data request recently, you will need to wait 3 hours between data requests";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"RequestData"};
    }

    @Override
    public String description(CommandObject command) {
        return "This command will send you a file of all of the data directly associated with your user id to your dms.";
    }

    @Override
    protected String usage() {
        return "[confirm]";
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    public String missingArgs(CommandObject command) {
        return "If you wish to request your data please include \"confirm\" in the command arguments\n\n" + super.missingArgs(command);
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    protected void init() {

    }
}
