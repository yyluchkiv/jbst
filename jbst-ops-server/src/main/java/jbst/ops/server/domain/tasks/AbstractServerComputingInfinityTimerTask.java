package jbst.ops.server.domain.tasks;

import jbst.foundation.domain.states.classic.ClassicState;
import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.ops.server.domain.configs.ServerConfigs;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static jbst.foundation.domain.time.SchedulerConfiguration.EVERY_30_SECONDS;

@Slf4j
public abstract class AbstractServerComputingInfinityTimerTask {
    public static final SchedulerConfiguration EVERY_1_HOUR = new SchedulerConfiguration(1L, 60L, TimeUnit.MINUTES);

    private final AbstractServerStateManager stateManager;

    private final ScheduledExecutorService onlineSES = newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService sshSES = newSingleThreadScheduledExecutor();

    public abstract void onlineTick();
    public abstract void sshTick();

    public final AbstractServerStateManager getLock() {
        return this.stateManager;
    }

    protected AbstractServerComputingInfinityTimerTask(ServerConfigs serverConfigs) {
        this.stateManager = new AbstractServerStateManager(ClassicState.CREATED, serverConfigs);
    }

    public void start(boolean isSshRequired) {
        synchronized (this.getLock()) {
            if (!this.stateManager.getState().getPermissions().startPermitted()) {
                return;
            }
            this.stateManager.start();

            this.onlineSES.scheduleWithFixedDelay(
                    this::onlineTick,
                    EVERY_30_SECONDS.initialDelay(),
                    EVERY_30_SECONDS.delay(),
                    EVERY_30_SECONDS.unit()
            );

            if (isSshRequired) {
                this.sshSES.scheduleWithFixedDelay(
                        this::sshTick,
                        EVERY_1_HOUR.initialDelay(),
                        EVERY_1_HOUR.delay(),
                        EVERY_1_HOUR.unit()
                );
            }

            this.stateManager.onActivation();
        }
    }
}
