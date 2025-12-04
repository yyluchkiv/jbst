package jbst.foundation.domain.tests;

import jbst.foundation.domain.constants.JbstConstants;
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
}
