package jbst.foundation.domain.triggers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.JbstEnumValue;
import lombok.AllArgsConstructor;

import static jbst.foundation.domain.enums.JbstEnumsCreator.findEnumByValueIgnoreCaseOrThrow;

@SuppressWarnings("unused")
public class JbstTriggers {

    public interface Abstract {
        Username getUsername();
        TriggerType getTriggerType();
        String getReadableDetails();
    }

    public record Auto(Username username) implements Abstract {

        @Override
        public Username getUsername() {
            return this.username;
        }

        @Override
        public TriggerType getTriggerType() {
            return TriggerType.AUTO;
        }

        @JsonValue
        @Override
        public String getReadableDetails() {
            return this.getTriggerType().getValue() + " trigger, username: " + this.getUsername();
        }
    }

    public record Cron() implements Abstract {

        @Override
        public Username getUsername() {
            return Username.cron();
        }

        @Override
        public TriggerType getTriggerType() {
            return TriggerType.CRON;
        }

        @JsonValue
        @Override
        public String getReadableDetails() {
            return this.getTriggerType().getValue() + " trigger";
        }
    }

    public record User(Username username) implements Abstract {

        @Override
        public Username getUsername() {
            return this.username;
        }

        @Override
        public TriggerType getTriggerType() {
            return TriggerType.USER;
        }

        @JsonValue
        @Override
        public String getReadableDetails() {
            return this.getTriggerType().getValue() + " trigger, username: " + this.username;
        }
    }

    @AllArgsConstructor
    public enum TriggerType implements JbstEnumValue<String> {
        AUTO("Auto"),
        CRON("Cron"),
        USER("User");

        private final String value;

        @JsonCreator
        public static TriggerType find(String value) {
            return findEnumByValueIgnoreCaseOrThrow(TriggerType.class, value);
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

        @SuppressWarnings("unused")
        public boolean isAuto() {
            return AUTO.equals(this);
        }

        public boolean isCron() {
            return CRON.equals(this);
        }

        public boolean isUser() {
            return USER.equals(this);
        }
    }
}
