package jbst.foundation.domain.states;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Getter
public abstract class JbstStateManagerClassic {
    private final AtomicReference<JbstStateClassic> state;

    protected JbstStateManagerClassic() {
        this.state = new AtomicReference<>(JbstStateClassic.CREATED);
    }

    protected JbstStateManagerClassic(JbstStateClassic state) {
        this.state = new AtomicReference<>(state);
    }

    // ================================================================================================================
    // States: Abstract
    // ================================================================================================================
    public abstract String getLogKeyword();
    public abstract String getLogId();

    public JbstStateClassic getState() {
        return this.state.get();
    }
    // ================================================================================================================
    // States: Mutation
    // ================================================================================================================
    public final void setState(JbstStateClassic state) {
        LOGGER.info(this.getLogKeyword(), this.getLogId(), this.state.get().asANSI(), state.asANSI());
        this.state.set(state);
    }

    public void start() {
        this.setState(JbstStateClassic.STARTING);
    }

    public void onActivation() {
        this.setState(JbstStateClassic.ACTIVE);
    }

    public void pause() {
        this.setState(JbstStateClassic.PAUSING);
    }

    public void onPaused() {
        this.setState(JbstStateClassic.PAUSED);
    }

    public void stop() {
        this.setState(JbstStateClassic.STOPPING);
    }

    public void onTermination() {
        this.setState(JbstStateClassic.TERMINATED);
    }

    public void complete() {
        this.setState(JbstStateClassic.COMPLETING);
    }

    public void onComplete() {
        this.setState(JbstStateClassic.COMPLETED);
    }

    // =================================================================================================================
    // CLASSES
    // =================================================================================================================
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class GroupedMappings {
        private final Map<JbstStateClassic, Long> values;
        private final boolean empty;

        public GroupedMappings(@NotNull List<JbstStateClassic> values) {
            this.values = values.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(JbstStateClassic.ORDINAL_COMPARATOR))
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new
                            )
                    );
            this.empty = CollectionUtils.isEmpty(values);
        }

        public static GroupedMappings fixed() {
            return new GroupedMappings(List.of(JbstStateClassic.CREATED, JbstStateClassic.ACTIVE));
        }
    }
}
