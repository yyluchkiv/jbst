package jbst.foundation.services.postgres;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static jbst.foundation.domain.random.JbstRandomEntities.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstPostgresInvitationsServiceTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstPostgresInvitationsRepository invitationsRepository() {
            return mock(JbstPostgresInvitationsRepository.class);
        }

        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean
        JbstPostgresInvitationsService invitationsService() {
            return new JbstPostgresInvitationsService(
                    this.invitationsRepository(),
                    this.jbstProperties()
            );
        }
    }

    private final JbstPostgresInvitationsRepository invitationsRepository;

    private final JbstPostgresInvitationsService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationsRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationsRepository
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void initInvitations() {
        // Arrange
        var user = entity(JbstPropertyUserOnInit.class);
        var authorities = set345(SimpleGrantedAuthority.class);

        // Act
        this.componentUnderTest.initInvitations(user, authorities);

        // Assert
        var userAC = ArgumentCaptor.forClass(List.class);
        verify(this.invitationsRepository).saveAll(userAC.capture());
        assertThat(userAC.getValue()).hasSize(10);
    }
}
