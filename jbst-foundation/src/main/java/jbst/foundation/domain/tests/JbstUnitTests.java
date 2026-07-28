package jbst.foundation.domain.tests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.jsons.JbstObjectMappers;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import jbst.foundation.domain.base.ObjectId;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstEnumValue;
import jbst.foundation.domain.enums.JbstEnumsCreator;
import jbst.foundation.domain.plurals.JbstPlurable;
import jbst.foundation.domain.plurals.JbstPlurals;
import lombok.*;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.readAllLines;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Files.PATH_DELIMITER;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;

@UtilityClass
public class JbstUnitTests {

    @SuppressWarnings("unused")
    public static class Classes {
        @NoArgsConstructor
        @Getter
        @Setter
        public static class ClassDefaultConstructor {
            private String string;
        }

        @Getter
        public static class ClassDefaultConstructorNoSetters {
            private String string;
        }

        @Getter
        public static class ClassDefaultConstructorUnexpectedMethods {
            private String string;
            public void badNamingMethod1(Integer wrongParameterType1, Long wrongParameterType2) {}
            public void badNamingMethod2() {}
        }

        @NoArgsConstructor
        @Getter
        public static class ClassDefaultConstructorUnexpectedSetter {
            private String string;
            public void setException1(Integer wrongParameterType1, Long wrongParameterType2) {}
            public void setException2() {}
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @EqualsAndHashCode
        @ToString
        public static class ClassNestChild1 {
            private Integer nest1Value1;
            private BigDecimal nest1Value2;
            private JbstUnitTests.Enums.EnumUnderTests nest1Value3;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @EqualsAndHashCode
        @ToString
        public static class ClassNestChild2 {
            private Short nest2Value1;
            private LocalDate nest2Value2;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @EqualsAndHashCode
        @ToString
        public static class ClassNestParent {
            private String value1;
            private Long value2;
            private int value3;
            private BigDecimal value4;
            private ClassNestChild1 child1;
            private ClassNestChild2 child2;
        }

        @AllArgsConstructor
        @Getter
        @Setter
        public static class ClassNoDefaultConstructor {
            private String string;
        }

        @Getter
        @Setter
        public static class ClassPrivateConstructor {
            private ClassPrivateConstructor() {}
            private String string;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        public static class ClassWithAllArgsAndDefaultConstructors {
            private String stringValue;
        }

        public record TestObject(ObjectId id, String name) implements JbstPlurable<ObjectId> {

            public static TestObject random() {
                return new TestObject(ObjectId.random(), randomString());
            }

            @Override
            public ObjectId getId() {
                return this.id;
            }
        }

        @Getter
        @EqualsAndHashCode(callSuper = true)
        @ToString
        public static class TestObjects extends JbstPlurals<TestObject, ObjectId> {

            public TestObjects(List<TestObject> values) {
                super(values);
            }

            public static TestObjects random(int size) {
                return new TestObjects(
                        IntStream.range(0, size)
                                .mapToObj(i -> TestObject.random())
                                .toList()
                );
            }

            public Set<String> getNames() {
                return this.values.stream().map(TestObject::name).collect(Collectors.toSet());
            }
        }
    }

    public static class Enums {
        public enum EnumNoValuesUnderTests {}
        public enum EnumOneValueUnderTests { ONE_VALUE }
        public enum EnumUnderTests { EXAMPLE_1, EXAMPLE_2, EXAMPLE_3, EXAMPLE_4 }

        @AllArgsConstructor
        public enum JbstEnumValue1 implements JbstEnumValue<String> {
            JBST("jbst"),
            TESTS("Tests");

            private final String value;

            @JsonCreator
            public static JbstEnumValue1 findBotType(String value) {
                return JbstEnumsCreator.findEnumByValueIgnoreCaseOrThrow(JbstEnumValue1.class, value);
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
        public enum JbstEnumValue2 implements JbstEnumValue<String> {
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
        public enum JbstEnumValue3 implements JbstEnumValue<Integer> {
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
            protected static final ObjectMapper OBJECT_MAPPER = JbstObjectMappers.jackson2CompatibleBuilder()
                    .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                    .build();

            protected static final ObjectMapper PLAIN_OBJECT_MAPPER = JbstObjectMappers.jackson2Compatible();

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

        public static abstract class BaseFolderFile extends JbstUnitTests.Runners.Base {
            protected abstract String getFolder();
            protected abstract String getFileName();
            protected final String readFile() {
                return read(this.getFolder(), this.getFileName());
            }
        }
    }
}
