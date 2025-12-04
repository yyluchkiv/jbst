package jbst.foundation.domain.states;

public record ClassicStatePermissions(
        boolean disabled,
        boolean startPermitted,
        boolean restartPermitted,
        boolean pausePermitted,
        boolean stopPermitted
) {
}
