package Annotations;

import Main.Constants;
import sx.blah.discord.handle.obj.Permissions;

import java.lang.annotation.*;

/**
 * Created by Vaerys on 15/01/2017.
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DualCommandAnnotation {
    String description();
    String usage() default "";
    String type();
    Permissions[] perms() default {Permissions.SEND_MESSAGES};
}
