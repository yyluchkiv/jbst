package jbst.foundation.domain.states.classic;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

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
}
