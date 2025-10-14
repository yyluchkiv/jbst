package jbst.foundation.domain.system.reset_server;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.tests.io.TestsIOUtils;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import jbst.foundation.domain.tuples.TuplePercentage;
import org.junit.jupiter.api.Test;

import static jbst.foundation.utilities.reflections.ReflectionUtility.getGetters;
import static org.assertj.core.api.Assertions.assertThat;

class ResetServerStatusTest extends AbstractFolderSerializationRunner {

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void integrationTest() {
        // Act-1
        var status = new ResetServerStatus(9);

        // Assert-1
        assertThat(getGetters(status)).hasSize(6);
        assertThat(status.getState()).isEqualTo(ResetServerState.READY);
        assertThat(status.getStage()).isZero();
        assertThat(status.getStagesCount()).isEqualTo(9);
        assertThat(status.getPercentage()).isEqualTo(TuplePercentage.zero());
        assertThat(status.getDescription()).isEqualTo("Ready");
        assertThat(status.isStarted()).isFalse();
        assertThat(this.writeValueAsString(status)).isEqualTo(TestsIOUtils.readFile(this.getFolder(), "reset-server-status-1.json"));

        // Act-2
        status.reset();

        // Assert-2
        assertThat(status.getState()).isEqualTo(ResetServerState.RESETTING);
        assertThat(status.getStage()).isZero();
        assertThat(status.getStagesCount()).isEqualTo(9);
        assertThat(status.getPercentage()).isEqualTo(TuplePercentage.zero());
        assertThat(status.getDescription()).isEqualTo("Resetting");
        assertThat(status.isStarted()).isTrue();
        assertThat(this.writeValueAsString(status)).isEqualTo(TestsIOUtils.readFile(this.getFolder(), "reset-server-status-2.json"));

        // Act-3
        status.nextStage("[Ops] 1st stage");

        // Assert-3
        assertThat(status.getState()).isEqualTo(ResetServerState.RESETTING);
        assertThat(status.getStage()).isEqualTo(1);
        assertThat(status.getStagesCount()).isEqualTo(9);
        assertThat(status.getPercentage()).isEqualTo(TuplePercentage.progressTuplePercentage(1, 9));
        assertThat(status.getDescription()).isEqualTo("[Ops] 1st stage");
        assertThat(status.isStarted()).isTrue();
        assertThat(this.writeValueAsString(status)).isEqualTo(TestsIOUtils.readFile(this.getFolder(), "reset-server-status-3.json"));

        // Act-4
        status.complete(JbstConstants.ZoneIds.UKRAINE);

        // Assert-4
        assertThat(status.getState()).isEqualTo(ResetServerState.READY);
        assertThat(status.getStage()).isEqualTo(9);
        assertThat(status.getStagesCount()).isEqualTo(9);
        assertThat(status.getPercentage()).isEqualTo(TuplePercentage.progressTuplePercentage(9, 9));
        assertThat(status.getDescription()).startsWith("Successfully completed at ");
        assertThat(status.isStarted()).isFalse();

        // Act-5
        status.setFailureDescription(new NullPointerException("jbst NPE"));

        // Assert-5
        assertThat(status.getState()).isEqualTo(ResetServerState.READY);
        assertThat(status.getStage()).isEqualTo(9);
        assertThat(status.getStagesCount()).isEqualTo(9);
        assertThat(status.getPercentage()).isEqualTo(TuplePercentage.progressTuplePercentage(9, 9));
        assertThat(status.getDescription()).isEqualTo("jbst NPE. Please contact development team");
        assertThat(status.isStarted()).isFalse();
        assertThat(this.writeValueAsString(status)).isEqualTo(TestsIOUtils.readFile(this.getFolder(), "reset-server-status-4.json"));
    }
}
