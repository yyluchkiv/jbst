package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.hardware.bytes.JbstByteSize;
import jbst.foundation.domain.hardware.bytes.JbstByteUnit;
import jbst.foundation.domain.hardware.memories.JbstCpuMemory;
import jbst.foundation.domain.hardware.memories.JbstGlobalMemory;
import jbst.foundation.domain.hardware.memories.JbstHeapMemory;
import jbst.foundation.domain.tuples.Tuple3;
import jbst.foundation.domain.tuples.TuplePercentage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstHardwareMonitoringDatapoint {
    private final Version version;

    private final JbstByteUnit unit;

    private final Tuple3<TuplePercentage, TuplePercentage, TuplePercentage> global;
    private final BigDecimal cpu;
    private final TuplePercentage heap;

    private final JbstHardwareMonitoringMaxValues maxValues;

    private final long timestamp;

    public JbstHardwareMonitoringDatapoint(
            @NotNull Version version,
            @NotNull JbstGlobalMemory global,
            @NotNull JbstCpuMemory cpu,
            @NotNull JbstHeapMemory heap
    ) {
        this.version = version;
        this.unit = JbstByteUnit.GIGABYTE;

        var server = TuplePercentage.of(
                new JbstByteSize(global.getTotal().getBytes() - global.getAvailable().getBytes()).getBy(this.unit),
                global.getTotal().getBy(this.unit),
                2,
                1
        );
        var swap = TuplePercentage.of(
                global.getSwapUsed().getBy(this.unit),
                global.getSwapTotal().getBy(this.unit),
                2,
                1
        );
        var virtual = TuplePercentage.of(
                global.getVirtualUsed().getBy(this.unit),
                global.getVirtualTotal().getBy(this.unit),
                2,
                1
        );

        this.global = new Tuple3<>(
                server,
                swap,
                virtual
        );

        this.cpu = cpu.getValue();

        this.heap = TuplePercentage.of(
                heap.getUsed().getBy(this.unit),
                heap.getMax().getBy(this.unit),
                2,
                1
        );

        this.maxValues = new JbstHardwareMonitoringMaxValues(global, heap);

        this.timestamp = getCurrentTimestamp();
    }

    public static JbstHardwareMonitoringDatapoint zeroUsage() {
        return new JbstHardwareMonitoringDatapoint(
                Version.unknown(),
                JbstGlobalMemory.zeroUsage(),
                JbstCpuMemory.zeroUsage(),
                JbstHeapMemory.zeroUsage()
        );
    }

    public static JbstHardwareMonitoringDatapoint random() {
        return new JbstHardwareMonitoringDatapoint(
                Version.random(),
                JbstGlobalMemory.random(),
                JbstCpuMemory.random(),
                JbstHeapMemory.random()
        );
    }

    public JbstHardwareMonitoringWidget getWidget(Map<JbstHardwareName, BigDecimal> thresholds) {
        return new JbstHardwareMonitoringWidget(
                this.version,
                this.tableView(thresholds)
        );
    }

    public JbstHardwareMonitoringDatapointTableView tableView(
            Map<JbstHardwareName, BigDecimal> thresholds
    ) {
        List<JbstHardwareMonitoringDatapointTableRow> table = new ArrayList<>();

        table.add(
                new JbstHardwareMonitoringDatapointTableRow(
                        JbstHardwareName.CPU,
                        this.timestamp,
                        this.cpu,
                        "",
                        thresholds
                )
        );

        Function<Tuple3<JbstHardwareName, TuplePercentage, JbstByteSize>, JbstHardwareMonitoringDatapointTableRow> tableRowFnc = tuple3 -> {
            var hardwareName = tuple3.a();
            var percentage = tuple3.b().percentage();
            var readableValue = tuple3.b().value() + " " + this.unit.getSymbol() + " of " + tuple3.c().getBy(this.unit, 2) + " " + this.unit.getSymbol();
            return new JbstHardwareMonitoringDatapointTableRow(
                    hardwareName,
                    this.timestamp,
                    percentage,
                    readableValue,
                    thresholds
            );
        };

        table.add(tableRowFnc.apply(new Tuple3<>(JbstHardwareName.HEAP, this.heap, this.maxValues.getHeap())));
        table.add(tableRowFnc.apply(new Tuple3<>(JbstHardwareName.SERVER, this.global.a(), this.maxValues.getServer())));
        table.add(tableRowFnc.apply(new Tuple3<>(JbstHardwareName.SWAP, this.global.b(), this.maxValues.getSwap())));
        table.add(tableRowFnc.apply(new Tuple3<>(JbstHardwareName.VIRTUAL, this.global.c(), this.maxValues.getVirtual())));

        return new JbstHardwareMonitoringDatapointTableView(table);
    }
}
