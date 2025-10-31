package jbst.foundation.domain.properties.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MandatoryMapProperty {
    String propertyName();
    Class<? extends Enum<?>> keySetClass();
}
