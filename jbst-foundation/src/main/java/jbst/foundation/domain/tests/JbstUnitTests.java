package jbst.foundation.domain.tests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.EnumValue;
import jbst.foundation.domain.enums.JbstEnumsCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.file.Paths;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.readAllLines;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Files.PATH_DELIMITER;

@UtilityClass
public class JbstUnitTests {

    public static class Enums {
        public enum EnumNoValuesUnderTests {}
        public enum EnumOneValueUnderTests { ONE_VALUE }
        public enum EnumUnderTests { EXAMPLE_1, EXAMPLE_2, EXAMPLE_3, EXAMPLE_4 }

        @AllArgsConstructor
        public enum EnumValue1 implements EnumValue<String> {
            JBST("jbst"),
            TESTS("Tests");

            private final String value;

            @JsonCreator
            public static EnumValue1 findBotType(String value) {
                return JbstEnumsCreator.findEnumByValueIgnoreCaseOrThrow(EnumValue1.class, value);
            }

            @JsonValue
            @Override
            public String getValue() {
                return this.value;
            }

            @Override
            public String toString() {
                return this.value;
            }
        }

        @AllArgsConstructor
        @Getter
        public enum EnumValue2 implements EnumValue<String> {
            JBST("jbst"),
            TESTS("Tests"),
            UNKNOWN("Unknown");

            private final String value;

            @Override
            public String getValue() {
                return this.value;
            }
        }

        @AllArgsConstructor
        public enum EnumValue3 implements EnumValue<Integer> {
            EMAIL_SENT(0),
            CANCELLED(1),
            AWAITING_APPROVAL(2),
            REJECTED(3),
            PROCESSING(4),
            FAILURE(5),
            COMPLETED(6),
            UNKNOWN(-1);

            private final int value;

            @Override
            public Integer getValue() {
                return this.value;
            }
        }
    }

    public static class IO {
        @SneakyThrows
        public static String read(String folder, String fileName) {
            var path = folder + PATH_DELIMITER + fileName;
            var resource = JbstUnitTests.IO.class.getClassLoader().getResource(path);
            if (isNull(resource)) {
                throw new IllegalArgumentException("Please check resource exists. Path: `" + path + "`");
            }
            var file = new File(resource.getFile());
            var lines = readAllLines(Paths.get(file.getAbsolutePath()), defaultCharset());
            return String.join(JbstConstants.Symbols.NEWLINE, lines);
        }
    }

    public static class Runners {
        public static abstract class Base {
            protected static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                    .build();

            protected static final ObjectMapper PLAIN_OBJECT_MAPPER = JsonMapper.builder()
                    .build();

            @SneakyThrows
            protected final String writeValueAsString(Object object) {
                return OBJECT_MAPPER
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(object);
            }

            @SuppressWarnings("unused")
            @SneakyThrows
            protected final String writeValueAsPlainString(Object object) {
                return PLAIN_OBJECT_MAPPER
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(object);
            }
        }

        public static abstract class BaseFolder extends JbstUnitTests.Runners.Base {
            protected abstract String getFolder();
        }
    }
}
