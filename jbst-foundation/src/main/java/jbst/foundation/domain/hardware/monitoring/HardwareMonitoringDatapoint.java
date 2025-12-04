package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.hardware.bytes.ByteSize;
import jbst.foundation.domain.hardware.bytes.ByteUnit;
import jbst.foundation.domain.hardware.memories.CpuMemory;
import jbst.foundation.domain.hardware.memories.GlobalMemory;
import jbst.foundation.domain.hardware.memories.HeapMemory;
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
public class HardwareMonitoringDatapoint {
    private final Version version;

    private final ByteUnit unit;

    private final Tuple3<TuplePercentage, TuplePercentage, TuplePercentage> global;
    private final BigDecimal cpu;
    private final TuplePercentage heap;

    private final HardwareMonitoringMaxValues maxValues;

    private final long timestamp;

    public HardwareMonitoringDatapoint(
            @NotNull Version version,
            @NotNull GlobalMemory global,
            @NotNull CpuMemory cpu,
            @NotNull HeapMemory heap
    ) {
        this.version = version;
        this.unit = ByteUnit.GIGABYTE;

        var server = TuplePercentage.of(
                new ByteSize(global.getTotal().getBytes() - global.getAvailable().getBytes()).getBy(this.unit),
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

        this.maxValues = new HardwareMonitoringMaxValues(global, heap);

        this.timestamp = getCurrentTimestamp();
    }

    public static HardwareMonitoringDatapoint zeroUsage() {
        return new HardwareMonitoringDatapoint(
                Version.unknown(),
                GlobalMemory.zeroUsage(),
                CpuMemory.zeroUsage(),
                HeapMemory.zeroUsage()
        );
    }

    public static HardwareMonitoringDatapoint random() {
        return new HardwareMonitoringDatapoint(
                Version.random(),
                GlobalMemory.random(),
                CpuMemory.random(),
                HeapMemory.random()
        );
    }

    public HardwareMonitoringWidget getWidget(Map<HardwareName, BigDecimal> thresholds) {
        return new HardwareMonitoringWidget(
                this.version,
                this.tableView(thresholds)
        );
    }

    public HardwareMonitoringDatapointTableView tableView(
            Map<HardwareName, BigDecimal> thresholds
    ) {
        List<HardwareMonitoringDatapointTableRow> table = new ArrayList<>();

        table.add(
                new HardwareMonitoringDatapointTableRow(
                        HardwareName.CPU,
                        this.timestamp,
                        this.cpu,
                        "",
                        thresholds
                )
        );

        Function<Tuple3<HardwareName, TuplePercentage, ByteSize>, HardwareMonitoringDatapointTableRow> tableRowFnc = tuple3 -> {
            var hardwareName = tuple3.a();
            var percentage = tuple3.b().percentage();
            var readableValue = tuple3.b().value() + " " + this.unit.getSymbol() + " of " + tuple3.c().getBy(this.unit, 2) + " " + this.unit.getSymbol();
            return new HardwareMonitoringDatapointTableRow(
                    hardwareName,
                    this.timestamp,
                    percentage,
                    readableValue,
                    thresholds
            );
        };

        table.add(tableRowFnc.apply(new Tuple3<>(HardwareName.HEAP, this.heap, this.maxValues.getHeap())));
        table.add(tableRowFnc.apply(new Tuple3<>(HardwareName.SERVER, this.global.a(), this.maxValues.getServer())));
        table.add(tableRowFnc.apply(new Tuple3<>(HardwareName.SWAP, this.global.b(), this.maxValues.getSwap())));
        table.add(tableRowFnc.apply(new Tuple3<>(HardwareName.VIRTUAL, this.global.c(), this.maxValues.getVirtual())));

        return new HardwareMonitoringDatapointTableView(table);
    }
}
