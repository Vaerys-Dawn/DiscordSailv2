package com.github.vaerys.commands.admin;

import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 26/06/2017.
 */
public class InfoEditModes {

    public static String uploadFile(CommandObject command) {
        if (command.message.getAttachments() == null || command.message.getAttachments().size() == 0) {
            return "> No file to upload found.";
        } else {
            try {
                IMessage.Attachment attachment = command.message.getAttachments().get(0);
                File file = new File(Utility.getGuildImageDir(command.guild.longID) + attachment.getFilename());
                File imagDir = new File(Utility.getGuildImageDir(command.guild.longID));
                File[] imageList = imagDir.listFiles();

                if (!Utility.isImageLink(attachment.getFilename())) {
                    return "> Cannot upload File. File type is invalid.";
                }

                //grab file
                final HttpURLConnection connection = (HttpURLConnection) new URL(attachment.getUrl()).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) " + "AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
                InputStream stream = connection.getInputStream();

                //save or update file
                if (file.exists()) {
                    file.delete();
                    Files.copy(stream, Paths.get(file.getPath()));
                    stream.close();
                    return "> File **" + attachment.getFilename() + "** updated";
                } else {
                    if (imageList.length >= 25) {
                        stream.close();
                        return "> Max images already reached, you will need to remove an old image to upload a new one.";
                    } else {
                        Files.copy(stream, Paths.get(file.getPath()));
                        stream.close();
                        return "> File **" + attachment.getFilename() + "** uploaded";
                    }
                }
            } catch (IOException e) {
                Utility.sendStack(e);
                return "> An error occurred trying to upload your file.";
            }
        }
    }

    public static String removeFile(String rest, CommandObject command) {
        File imagDir = new File(Utility.getGuildImageDir(command.guild.longID));
        File[] imageList = imagDir.listFiles();
        for (File f : imageList) {
            if (f.getName().equalsIgnoreCase(rest)) {
                f.delete();
                return "> File Removed.";
            }
        }
        return "> File Not Found.";
    }

    public static String listFiles(CommandObject command) {
        //send Embed
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("> Here are the files available to you:");
        File imagDir = new File(Utility.getGuildImageDir(command.guild.longID));
        File[] imageList = imagDir.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for (File f : imageList) {
            fileNames.add(f.getName());
        }
        builder.withDesc("```\n" + Utility.listFormatter(fileNames, true) + "```");
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    public static String uploadInfo(CommandObject command) {
        if (command.message.getAttachments() == null || command.message.getAttachments().size() == 0) {
            return "> No file to upload found.";
        } else {
            try {
                IMessage.Attachment attachment = command.message.getAttachments().get(0);
                File file = new File(Utility.getFilePath(command.guild.longID, Constants.FILE_INFO));

                if (!attachment.getFilename().equals(Constants.FILE_INFO)) {
                    return "> Cannot upload file, File name must be \"" + Constants.FILE_INFO + "\"";
                }

                //grab file
                final HttpURLConnection connection = (HttpURLConnection) new URL(attachment.getUrl()).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) " + "AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
                InputStream stream = connection.getInputStream();

                //save or update file
                if (file.exists()) {
                    file.delete();
                    Files.copy(stream, Paths.get(file.getPath()));
                    stream.close();
                    return "> Updated **" + Constants.FILE_INFO + "** file.";
                } else {
                    Files.copy(stream, Paths.get(file.getPath()));
                    stream.close();
                    return "> New **" + Constants.FILE_INFO + "** file uploaded.";
                }
            } catch (IOException e) {
                Utility.sendStack(e);
                return "> An error occurred trying to upload your file.";
            }
        }
    }

    public static String getInfoFile(CommandObject command) {
        String filePath = Utility.getFilePath(command.guild.longID, Constants.FILE_INFO);
        File file = new File(filePath);
        if (FileHandler.readFromFile(filePath).isEmpty()){
            FileHandler.writeToFile(FileHandler.readFromFile(Constants.INFO_TEMPLATE), filePath);
        }
        if (file.exists()) {
            RequestHandler.sendFile("> Here is your **" + Constants.FILE_INFO + "** file.", file, command.channel.get());
        } else {
            return "> Cannot send file, **" + Constants.FILE_INFO + "** file does not exist yet.";
        }
        return null;
    }
}
