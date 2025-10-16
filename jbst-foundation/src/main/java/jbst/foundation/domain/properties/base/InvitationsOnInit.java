package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class InvitationsOnInit extends AbstractTogglePropertyConfigs {
    @MandatoryProperty
    private final boolean enabled;

    public static InvitationsOnInit hardcoded() {
        return new InvitationsOnInit(true);
    }

    public static InvitationsOnInit random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static InvitationsOnInit enabled() {
        return hardcoded();
    }

    public static InvitationsOnInit disabled() {
        return new InvitationsOnInit(false);
    }
}
