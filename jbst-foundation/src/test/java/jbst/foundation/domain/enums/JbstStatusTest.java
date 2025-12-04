package jbst.foundation.domain.enums;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

@Slf4j
class JbstStatusTest {

    @Test
    void print() {
        // Act
        Stream.of(JbstStatus.values()).forEach(status -> LOGGER.warn("{} → {}", status, status.asANSI()));
    }
}
