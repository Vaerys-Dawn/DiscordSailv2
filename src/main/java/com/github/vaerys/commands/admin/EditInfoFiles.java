package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 26/06/2017.
 */
public class EditInfoFiles extends Command {

    String modes =
            "**Modes:**\n" +
                    "> uploadImage - `Requires image file.`\n" +
                    "> removeImage - `Requires file name.`\n" +
                    "> listFiles/listImages - `Lists the server's image files.`\n" +
                    "> uploadInfo - `Requires \"" + Constants.FILE_INFO + "\" file.`\n" +
                    "> getInfoFile - `Post the server's Info.txt.`";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject object = new SplitFirstObject(args);
        switch (object.getFirstWord().toLowerCase()) {
            case "uploadimage":
                return InfoEditModes.uploadFile(command);
            case "removeimage":
                return InfoEditModes.removeFile(object.getRest(), command);
            case "listimages":
                return InfoEditModes.listFiles(command);
            case "listfiles":
                return InfoEditModes.listFiles(command);
            case "uploadinfo":
                return InfoEditModes.uploadInfo(command);
            case "getinfofile": {
                return InfoEditModes.getInfoFile(command);
            }
            default:
                return "Invalid Edit Mode.\n" + missingArgs(command);
        }
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for editing of the updateInfo command.\n**Tags:** " + Utility.listFormatter(TagList.getNames(TagType.INFO), true) + "\n" + modes;
    }

    @Override
    public void init() {

    }


    @Override
    protected String[] names() {
        return new String[]{"EditInfoFiles", "UpdateInfoFiles"};
    }

    @Override
    protected String usage() {
        return "[Mode] (args)";
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
        return new UpdateInfo().perms();
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
