package jbst.foundation.handlers;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.configurations.TestConfigurationHandlers;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static jbst.foundation.domain.exceptions.JbstExceptionResponse.Type.ERROR;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAuthenticationEntryPointTest {

    @Configuration
    @Import({
            TestConfigurationHandlers.class
    })
    static class ContextConfiguration {

    }

    private final JbstEventsPublisher eventsPublisher;
    private final ObjectMapper objectMapper;

    private final JbstAuthenticationEntryPoint componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.eventsPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.eventsPublisher
        );
    }

    @Test
    void commenceTest() throws IOException {
        // Arrange
        var response = mock(HttpServletResponse.class);
        var printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
        var request = mock(HttpServletRequest.class);
        var authenticationException = mock(AuthenticationException.class);
        when(authenticationException.getMessage()).thenReturn(randomString());

        // Act
        this.componentUnderTest.commence(request, response, authenticationException);

        // Assert
        assertAndVerifyBasicCommence(
                request,
                response,
                printWriter,
                authenticationException
        );
    }

    @Test
    void commenceBadCredentialsExceptionNotCachedEndpointTest() throws IOException {
        // Arrange
        var response = mock(HttpServletResponse.class);
        var printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
        var request = mock(HttpServletRequest.class);
        var badCredentialsException = mock(BadCredentialsException.class);
        when(badCredentialsException.getMessage()).thenReturn(randomString());

        // Act
        this.componentUnderTest.commence(request, response, badCredentialsException);

        // Assert
        assertAndVerifyBasicCommence(
                request,
                response,
                printWriter,
                badCredentialsException
        );
    }

    @Test
    void commenceBadCredentialsExceptionCachedEndpointTest() throws IOException {
        // Arrange
        var response = mock(HttpServletResponse.class);
        var printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(IPAddress.localhost().value());
        var badCredentialsException = mock(BadCredentialsException.class);
        when(badCredentialsException.getMessage()).thenReturn(randomString());

        // Act
        this.componentUnderTest.commence(request, response, badCredentialsException);

        // Assert
        assertAndVerifyBasicCommence(
                request,
                response,
                printWriter,
                badCredentialsException
        );

    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    @SuppressWarnings("unchecked")
    private void assertAndVerifyBasicCommence(
            HttpServletRequest request,
            HttpServletResponse response,
            PrintWriter printWriter,
            Exception exception
    ) throws IOException {
        var jsonAC = ArgumentCaptor.forClass(String.class);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).getWriter();
        verify(printWriter).write(jsonAC.capture());
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        HashMap<String, Object> json = this.objectMapper.readValue(jsonAC.getValue(), typeRef);
        assertThat(json)
                .hasSize(4)
                .containsKeys("jbsTimestamp", "jbstType", "jbstMessageOnClient", "jbstAttributes")
                .containsEntry("jbstType", ERROR.toString());
        var attributes = (Map<String, Object>) json.get("jbstAttributes");
        assertThat(attributes).containsEntry("jbstTrace", exception.getMessage());
        verify(exception, times(5)).getMessage();
        verifyNoMoreInteractions(
                request,
                response,
                exception
        );
    }
}
