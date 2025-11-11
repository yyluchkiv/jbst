package jbst.foundation.domain.concurrent;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public abstract class JbstWorker {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum JbstWorkerState {
        CREATED("Created"),
        OPERATIVE("Operative"),
        STOPPED("Stopped");

        private final String value;

        @JsonValue
        public String getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public JbstWorkerPermissions getPermissions() {
            return new JbstWorkerPermissions(
                    CREATED.equals(this) || STOPPED.equals(this),
                    this.isOperative()
            );
        }

        public boolean isOperative() {
            return OPERATIVE.equals(this);
        }
    }

    public record JbstWorkerPermissions(boolean start, boolean stop) { }

    public abstract void onTick();
    public abstract void onComplete();
    public abstract void start();
    public abstract void stop();
}
