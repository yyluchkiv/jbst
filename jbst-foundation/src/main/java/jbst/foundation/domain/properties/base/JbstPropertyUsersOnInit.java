package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyUsersOnInit extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
    private List<JbstPropertyUserOnInit> users;

    public static JbstPropertyUsersOnInit hardcoded() {
        return new JbstPropertyUsersOnInit(
                true,
                List.of(
                        new JbstPropertyUserOnInit(
                                Username.of("admin12"),
                                Password.of("password12"),
                                ZoneId.systemDefault(),
                                null,
                                false,
                                Set.of("admin")
                        )
                )
        );
    }

    public static JbstPropertyUsersOnInit random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static JbstPropertyUsersOnInit enabled() {
        return hardcoded();
    }

    public static JbstPropertyUsersOnInit disabled() {
        return new JbstPropertyUsersOnInit(false, new ArrayList<>());
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }

    public final Set<String> getAuthorities() {
        if (nonNull(this.users)) {
            return this.users.stream().map(JbstPropertyUserOnInit::getAuthorities)
                    .filter(Objects::nonNull)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    public final Set<Username> getUsernames() {
        if (nonNull(this.users)) {
            return this.users.stream()
                    .map(JbstPropertyUserOnInit::getUsername)
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }
}
