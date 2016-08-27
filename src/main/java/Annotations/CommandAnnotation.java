package Annotations;

import Main.Constants;
import Main.Globals;
import sx.blah.discord.handle.obj.Permissions;

import java.lang.annotation.*;

/**
 * Created by Vaerys on 03/08/2016.
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandAnnotation {
    String name();
    String type();
    String channel() default Constants.CHANNEL_ANY;
    Permissions[] perms() default {Permissions.SEND_MESSAGES};
    String description() default "No description set.";
    String usage() default Constants.NULL_VARIABLE;
    boolean requiresArgs() default false;
    boolean doResponseGeneral() default false;
    boolean doLogging() default false;
}
