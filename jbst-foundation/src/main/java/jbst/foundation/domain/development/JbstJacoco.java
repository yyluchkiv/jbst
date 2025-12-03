package jbst.foundation.domain.development;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;

@SuppressWarnings("unused")
@UtilityClass
public class JbstJacoco {

    public static void classCoverageHook(Class<?> clazz) {
        try {
            // default constructor
            clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e1) {
            try {
                // private constructor
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (ReflectiveOperationException e2) {
                // ignore
            }
        }
    }
}
