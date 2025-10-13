package jbst.foundation.domain.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.CLASS)
public @interface JbstDeletionScheduled {
    String reason() default "";
    String version();
}
