package Commands;

/**
 * Created by Vaerys on 05/02/2017.
 */
public interface DMCommand {
    String TYPE_GENERAL = "General";
    String TYPE_HELP = "Help";
    String TYPE_CREATOR = "Creator";

    //Channel Constants

    String spacer = "\u200B";
    String indent = "    ";
    String codeBlock = "```";
    String ownerOnly = ">> ONLY THE BOT'S OWNER CAN RUN THIS <<";

    String execute(String args, DMCommandObject command);

    //descriptors
    String[] names();
    String description();
    String usage();
    String type();
    boolean requiresArgs();
}
