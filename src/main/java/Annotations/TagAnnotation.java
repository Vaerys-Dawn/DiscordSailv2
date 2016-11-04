package Annotations;

import Main.Constants;

import java.lang.annotation.*;

/**
 * Created by Vaerys on 06/10/2016.
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TagAnnotation {
    String name();
    String description();
    String usage();
    String type() default Constants.TAG_TYPE_ALL;
    int priority() default 10;
}
