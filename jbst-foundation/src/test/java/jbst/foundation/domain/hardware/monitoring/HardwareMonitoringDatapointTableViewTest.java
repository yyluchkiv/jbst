package jbst.foundation.domain.hardware.monitoring;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HardwareMonitoringDatapointTableViewTest {

    private static Stream<Arguments> constructorTest() {
        return Stream.of(
                Arguments.of(
                        new HardwareMonitoringDatapointTableView(
                                List.of()
                        ),
                        false,
                        false
                ),
                Arguments.of(
                        new HardwareMonitoringDatapointTableView(
                                List.of(
                                        HardwareMonitoringDatapointTableRow.random(),
                                        HardwareMonitoringDatapointTableRow.random()
                                )
                        ),
                        true,
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource("constructorTest")
    void constructorTest(HardwareMonitoringDatapointTableView tableView, boolean expectedAnyPresent, boolean expectedAnyProblem) {
        // Act + Assert
        assertThat(tableView).isNotNull();
        assertThat(tableView.isAnyPresent()).isEqualTo(expectedAnyPresent);
        assertThat(tableView.isAnyProblem()).isEqualTo(expectedAnyProblem);
    }
}
