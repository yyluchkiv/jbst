package jbst.foundation.domain.strings;

import jbst.foundation.domain.exceptions.ThrowableTrace;
import lombok.experimental.UtilityClass;

import java.io.PrintWriter;
import java.io.StringWriter;

@UtilityClass
public class JbstTraces {

    public static ThrowableTrace getTrace(Throwable throwable) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return new ThrowableTrace(sw.toString());
    }
}
