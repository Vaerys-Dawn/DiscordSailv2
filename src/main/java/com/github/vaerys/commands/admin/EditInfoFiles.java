package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.TagType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 26/06/2017.
 */
public class EditInfoFiles extends Command {

    // using static as it will cause less memory to be used overall by orphaned data
    protected static String[] NAMES = new String[] {"EditInfoFiles", "UpdateInfoFiles"};
    protected static final String USAGE = "[Mode] (args)";
    protected static SAILType COMMAND_TYPE = SAILType.ADMIN;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new UpdateInfo().perms();
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = true;

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
                return "Invalid Edit Mode.\n" + Utility.getCommandInfo(this, command);
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
        return NAMES;
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

}
