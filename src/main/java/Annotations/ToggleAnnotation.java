package Annotations;

import sx.blah.discord.handle.obj.Permissions;

import java.lang.annotation.*;

/**
 * Created by Vaerys on 01/10/2016.
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToggleAnnotation {
    String name();
    Permissions[] permissions() default {Permissions.MANAGE_SERVER};
}
