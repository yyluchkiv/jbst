package jbst.foundation.domain.http;

import jbst.foundation.domain.base.Email;
import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class JbstHttpRequestFields {
    private static final Pattern CAMEL_CASE_LETTERS_AND_NUMBERS_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");

    public static boolean containsCamelCaseLettersAndNumbers(String field) {
        return CAMEL_CASE_LETTERS_AND_NUMBERS_PATTERN.matcher(field).matches();
    }

    public static boolean isEmail(Email email) {
        return isEmail(email.value());
    }

    public static boolean isEmail(String field) {
        return EMAIL_PATTERN.matcher(field).matches();
    }
}
