package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.JbstPropertyCheckbox;
import jbst.foundation.domain.properties.configs.security.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurity extends JbstProperty {
    @MandatoryProperty
    private final JbstPropertySecurityAuthorities authoritiesConfigs;
    @MandatoryProperty
    private final JbstPropertySecurityCookies cookiesConfigs;
    @MandatoryProperty
    private final JbstPropertySecurityEssence essenceConfigs;
    @MandatoryProperty
    private final JbstPropertySecurityJWT jwtTokensConfigs;
    @MandatoryProperty
    private final JbstPropertySecurityLogging loggingConfigs;
    @MandatoryProperty
    private final JbstPropertySecuritySessions sessionConfigs;
    @MandatoryProperty
    private final JbstPropertySecurityUsersEmails usersEmailsConfigs;
    @MandatoryProperty
    private final JbstPropertySecurityWebsockets websocketsConfigs;
    @NonMandatoryProperty
    private final JbstPropertySecurityUsersTokens usersTokensConfigs;

    public static JbstPropertySecurity hardcoded() {
        return new JbstPropertySecurity(
                JbstPropertySecurityAuthorities.hardcoded(),
                JbstPropertySecurityCookies.hardcoded(),
                JbstPropertySecurityEssence.hardcoded(),
                JbstPropertySecurityJWT.hardcoded(),
                JbstPropertySecurityLogging.hardcoded(),
                JbstPropertySecuritySessions.hardcoded(),
                JbstPropertySecurityUsersEmails.hardcoded(),
                JbstPropertySecurityWebsockets.hardcoded(),
                JbstPropertySecurityUsersTokens.hardcoded()
        );
    }

    public static JbstPropertySecurity of(JbstPropertySecurityLogging loggingConfigs) {
        return new JbstPropertySecurity(
                null,
                null,
                null,
                null,
                loggingConfigs,
                null,
                null,
                null,
                null
        );
    }

    public static JbstPropertySecurity of(JbstPropertySecuritySessions sessionConfigs) {
        return new JbstPropertySecurity(
                null,
                null,
                null,
                null,
                null,
                sessionConfigs,
                null,
                null,
                null
        );
    }

    public static JbstPropertySecurity disabledUsersEmailsConfigs() {
        return new JbstPropertySecurity(
                null,
                null,
                null,
                null,
                null,
                null,
                new JbstPropertySecurityUsersEmails(
                        "[jbst]",
                        JbstPropertyCheckbox.disabled(),
                        JbstPropertyCheckbox.disabled(),
                        JbstPropertyCheckbox.disabled()
                ),
                null,
                null
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "security-jwt-configs";
    }

    @Override
    public void assertProperties() {
        super.assertProperties();

        // Requirements: availableAuthorities vs. configuredAuthorities
        var expectedAuthorities = this.authoritiesConfigs.getAllAuthoritiesValues();
        var configuredAuthorities = this.essenceConfigs.getUsersOnInit().getAuthorities();
        var containsAll = expectedAuthorities.containsAll(configuredAuthorities);
        assertTrueOrThrow(containsAll, "Please verify `users-on-init.users.authorities`. Configuration provide unauthorized authority");

        // Requirements: availableAuthorities vs. required enum values
        var authorityClasses = this.getAbstractAuthorityClasses(this.authoritiesConfigs.getPackageName());
        var size = authorityClasses.size();
        assertTrueOrThrow(size == 1, "Please verify AbstractAuthority.class has only one sub enum. Found: `" + size + "`");
        var authorityClass = authorityClasses.iterator().next();
        Set<String> actualAuthorities = new HashSet<>();
        var abstractAuthorityClass = AbstractAuthority.class;
        var jbstAuthorities = Stream.of(abstractAuthorityClass.getDeclaredFields())
                .map(field -> {
                    try {
                        return field.get(abstractAuthorityClass).toString();
                    } catch (IllegalAccessException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        var serverAuthorities = Stream.of(authorityClass.getEnumConstants())
                .map(AbstractAuthority::getValue)
                .collect(Collectors.toSet());
        actualAuthorities.addAll(jbstAuthorities);
        actualAuthorities.addAll(serverAuthorities);
        assertTrueOrThrow(
                expectedAuthorities.equals(actualAuthorities),
                "Please verify AbstractAuthority sub enum configuration. Expected: `" + expectedAuthorities + "`. Actual: `" + actualAuthorities + "`"
        );
    }

    // =================================================================================================================
    // Private Method: Reflection on AbstractAuthority
    // =================================================================================================================
    @SuppressWarnings("unchecked")
    private Set<Class<? extends AbstractAuthority>> getAbstractAuthorityClasses(String packageName) {
        var beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
        var classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry, false);
        var tf = new AssignableTypeFilter(AbstractAuthority.class);
        classPathBeanDefinitionScanner.addIncludeFilter(tf);
        classPathBeanDefinitionScanner.scan(packageName);
        return Stream.of(beanDefinitionRegistry.getBeanDefinitionNames())
                .map(beanDefinitionRegistry::getBeanDefinition)
                .map(BeanDefinition::getBeanClassName)
                .map(className -> {
                    try {
                        return (Class<? extends AbstractAuthority>) Class.forName(className);
                    } catch (ClassNotFoundException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(clazz -> nonNull(clazz.getEnumConstants()))
                .collect(Collectors.toSet());
    }
}
