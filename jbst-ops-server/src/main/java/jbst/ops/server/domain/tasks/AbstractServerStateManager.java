package jbst.ops.server.domain.tasks;

import jbst.foundation.domain.states.classic.AbstractClassicStateManager;
import jbst.foundation.domain.states.classic.ClassicState;
import jbst.ops.server.domain.configs.ServerConfigs;

public class AbstractServerStateManager extends AbstractClassicStateManager {
    private final ServerConfigs serverConfigs;

    public AbstractServerStateManager(ClassicState state, ServerConfigs serverConfigs) {
        super(state);
        this.serverConfigs = serverConfigs;
    }

    @Override
    public String getLogKeyword() {
        return "[Ops, InfinityTimerTask] Server: `{}`. State: `{}` → `{}`";
    }

    @Override
    public String getLogId() {
        return this.serverConfigs.name() + "@" + this.serverConfigs.team();
    }
}
