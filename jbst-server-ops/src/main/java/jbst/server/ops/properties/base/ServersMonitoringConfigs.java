package jbst.server.ops.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.server.ops.domain.servers.ServerFileSystemMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.math.BigDecimal;
import java.time.ZoneId;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.utilities.numbers.BigDecimalUtility.is;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ServersMonitoringConfigs extends JbstProperty {
    @MandatoryProperty
    private final ZoneId zoneId;
    @MandatoryProperty
    private final Boolean hideIP;
    @MandatoryProperty
    private final BigDecimal fileSystemFilter;
    @MandatoryProperty
    private final BigDecimal fileSystemThreshold;

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }

    @Override
    public void assertPropertiesAsLeaf(String parentTreeName) {
        super.assertPropertiesAsLeaf(parentTreeName);
        assertTrueOrThrow(
                is(this.fileSystemThreshold, ">", this.fileSystemFilter),
                "Property 'file-system-threshold is expected to be greater than file-system-filter"
        );
    }

    public boolean isHideIP() {
        return this.hideIP;
    }

    public boolean isFileSystemProcessable(ServerFileSystemMetadata.FileSystemMetadataRow row) {
        return row.isUsePercentageAbove(this.fileSystemThreshold);
    }
}
