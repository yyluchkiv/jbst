package jbst.server.ops.properties.base;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.server.ops.domain.servers.ServerFileSystemMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.math.BigDecimal;
import java.time.ZoneId;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.utilities.numbers.BigDecimalUtility.isFirstValueGreater;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ServersMonitoringConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final ZoneId zoneId;
    @MandatoryProperty
    private final Boolean hideIP;
    @MandatoryProperty
    private final BigDecimal fileSystemFilter;
    @MandatoryProperty
    private final BigDecimal fileSystemThreshold;

    public boolean isHideIP() {
        return this.hideIP;
    }

    @Override
    public void assertProperties(PropertyId propertyId) {
        super.assertProperties(propertyId);
        assertTrueOrThrow(
                isFirstValueGreater(this.fileSystemThreshold, this.fileSystemFilter),
                "Attribute `fileSystemThreshold` is expected to be greater than `fileSystemFilter`"
        );
    }

    public boolean isFileSystemProcessable(ServerFileSystemMetadata.FileSystemMetadataRow row) {
        return row.isUsePercentageAbove(this.fileSystemThreshold);
    }
}
