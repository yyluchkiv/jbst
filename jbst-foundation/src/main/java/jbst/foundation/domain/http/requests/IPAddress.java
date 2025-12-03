package jbst.foundation.domain.http.requests;

import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomIPv4;

public record IPAddress(@NotNull String value) {

    public IPAddress(String value) {
        this.value = nonNull(value) ? value : localhost().value();
    }

    public static IPAddress localhost() {
        return new IPAddress("127.0.0.1");
    }

    public static IPAddress hardcoded() {
        return new IPAddress("8.8.8.8");
    }

    public static IPAddress random() {
        return new IPAddress(randomIPv4());
    }
}
