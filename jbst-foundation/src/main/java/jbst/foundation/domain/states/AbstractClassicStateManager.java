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
public abstract class AbstractClassicStateManager {
    private final AtomicReference<ClassicState> state;

    protected AbstractClassicStateManager() {
        this.state = new AtomicReference<>(ClassicState.CREATED);
    }

    protected AbstractClassicStateManager(ClassicState state) {
        this.state = new AtomicReference<>(state);
    }

    // ================================================================================================================
    // States: Abstract
    // ================================================================================================================
    public abstract String getLogKeyword();
    public abstract String getLogId();

    public ClassicState getState() {
        return this.state.get();
    }
    // ================================================================================================================
    // States: Mutation
    // ================================================================================================================
    public final void setState(ClassicState state) {
        LOGGER.info(this.getLogKeyword(), this.getLogId(), this.state.get().asANSI(), state.asANSI());
        this.state.set(state);
    }

    public void start() {
        this.setState(ClassicState.STARTING);
    }

    public void onActivation() {
        this.setState(ClassicState.ACTIVE);
    }

    public void pause() {
        this.setState(ClassicState.PAUSING);
    }

    public void onPaused() {
        this.setState(ClassicState.PAUSED);
    }

    public void stop() {
        this.setState(ClassicState.STOPPING);
    }

    public void onTermination() {
        this.setState(ClassicState.TERMINATED);
    }

    public void complete() {
        this.setState(ClassicState.COMPLETING);
    }

    public void onComplete() {
        this.setState(ClassicState.COMPLETED);
    }

    // =================================================================================================================
    // CLASSES
    // =================================================================================================================
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ClassicStateGroupedMappings {
        private final Map<ClassicState, Long> values;
        private final boolean empty;

        public ClassicStateGroupedMappings(@NotNull List<ClassicState> values) {
            this.values = values.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(ClassicState.ORDINAL_COMPARATOR))
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

        public static ClassicStateGroupedMappings hardcoded() {
            return new ClassicStateGroupedMappings(List.of(ClassicState.CREATED, ClassicState.ACTIVE));
        }
    }
}
