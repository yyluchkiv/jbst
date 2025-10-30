package jbst.foundation.domain.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface JbstModificationBeta {
    String releaseVersion();
}
