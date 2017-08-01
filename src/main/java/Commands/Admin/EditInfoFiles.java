package Commands.Admin;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Constants;
import Main.Utility;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 26/06/2017.
 */
public class EditInfoFiles implements Command {

    String modes = "**Modes:**\n" +
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
                return InfoEditModes.uploadFile(command.message);
            case "removeimage":
                return InfoEditModes.removeFile(object.getRest(), command.message);
            case "listimages":
                return InfoEditModes.listFiles(command.message);
            case "listfiles":
                return InfoEditModes.listFiles(command.message);
            case "uploadinfo":
                return InfoEditModes.uploadInfo(command.message);
            case "getinfofile": {
                return InfoEditModes.getInfoFile(command.message);
            }
            default:
                return "Invalid Edit Mode.\n" + Utility.getCommandInfo(this, command);
        }
    }

    @Override
    public String[] names() {
        return new String[]{"EditInfoFiles", "UpdateInfoFiles"};
    }

    @Override
    public String description() {
        return "Allows for editing of the updateInfo command.\n" + modes;
    }

    @Override
    public String usage() {
        return "[Mode] (args)";
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
        return new UpdateInfo().perms();
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
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
