package jbst.foundation.services.postgres;

import jbst.foundation.domain.databases.postgres.entities.JbstPostgresInvitation;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import jbst.foundation.services.abstracts.JbstAbstractInvitationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Service
public class JbstPostgresInvitationsService extends JbstAbstractInvitationsService {

    private final JbstPostgresInvitationsRepository invitationsRepository;

    @Autowired
    public JbstPostgresInvitationsService(
            JbstPostgresInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
        this.invitationsRepository = invitationsRepository;
    }

    @Override
    public void initInvitations(JbstPropertyUserOnInit userOnInit, Set<SimpleGrantedAuthority> authorities) {
        var invitations = IntStream.range(0, 10)
                .mapToObj(i ->
                        new JbstPostgresInvitation(
                                userOnInit.getUsername(),
                                authorities
                        )
                )
                .toList();
        this.invitationsRepository.saveAll(invitations);
    }
}
