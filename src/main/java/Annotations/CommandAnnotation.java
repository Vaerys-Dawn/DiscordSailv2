package Annotations;

import Main.Constants;
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
    String description();
    String usage() default "";
    String type();
    String channel() default Constants.CHANNEL_ANY;
    Permissions[] perms() default {Permissions.SEND_MESSAGES};
    boolean requiresArgs() default false;
    boolean doAdminLogging() default false;
    boolean doResponseGeneral() default false;
}
