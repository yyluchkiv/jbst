package jbst.foundation.domain.hardware.monitoring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static org.springframework.util.CollectionUtils.isEmpty;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstHardwareMonitoringDatapointTableView {
    private final List<JbstHardwareMonitoringDatapointTableRow> rows;
    private final boolean anyPresent;
    private final boolean anyProblem;

    public JbstHardwareMonitoringDatapointTableView(@NotNull List<JbstHardwareMonitoringDatapointTableRow> rows) {
        this.rows = rows;
        this.anyPresent = !isEmpty(rows);
        this.anyProblem = rows.stream().anyMatch(JbstHardwareMonitoringDatapointTableRow::isThresholdReached);
    }

    @JsonIgnore
    public Map<JbstHardwareName, JbstHardwareMonitoringDatapointTableRow> getMappedRows() {
        return this.rows.stream().collect(Collectors.toMap(JbstHardwareMonitoringDatapointTableRow::getHardwareName, identity()));
    }
}
