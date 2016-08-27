package Annotations;

import java.lang.annotation.*;

/**
 * Created by Vaerys on 03/08/2016.
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AliasAnnotation {
    String[] alias();
}
