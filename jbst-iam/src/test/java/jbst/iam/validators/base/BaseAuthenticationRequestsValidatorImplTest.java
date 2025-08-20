package jbst.iam.validators.base;

import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.iam.validators.BaseAuthenticationRequestsValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseAuthenticationRequestsValidatorImplTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        public BaseAuthenticationRequestsValidator authenticationRequestsValidator() {
            return new BaseBaseAuthenticationRequestsValidatorImpl();
        }
    }

    private static Stream<Arguments> validateAuthenticationStandardTest() {
        return Stream.of(
                Arguments.of(
                        new RequestUserLogin(null, Password.of("admin")),
                        invalidAttribute("username")
                ),
                Arguments.of(
                        new RequestUserLogin(Username.of("admin"), null),
                        invalidAttribute("password")
                ),
                Arguments.of(
                        new RequestUserLogin(Username.of("admin"), Password.of("admin")), null
                ),
                Arguments.of(
                        new RequestUserLogin(Username.of("user"), Password.of("password")), null
                )
        );
    }

    private final BaseAuthenticationRequestsValidator componentUnderTest;

    @ParameterizedTest
    @MethodSource("validateAuthenticationStandardTest")
    void validateAuthenticationStandardTest(RequestUserLogin requestUserLogin, String exceptionMessage) {
        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateAuthenticationStandard(requestUserLogin));

        // Assert
        if (nonNull(exceptionMessage)) {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(exceptionMessage);
        } else {
            assertThat(throwable).isNull();
        }
    }
}
