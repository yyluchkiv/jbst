package jbst.foundation.services.mongo;

import jbst.foundation.domain.databases.mongo.JbstMongoInvitation;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.repositories.mongo.JbstMongoInvitationsRepository;
import jbst.foundation.services.abstracts.JbstAbstractInvitationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.IntStream;

@Service
public class JbstMongoInvitationsService extends JbstAbstractInvitationsService {

    private final JbstMongoInvitationsRepository invitationsRepository;

    @Autowired
    public JbstMongoInvitationsService(
            JbstMongoInvitationsRepository invitationsRepository,
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
                        new JbstMongoInvitation(
                                userOnInit.getUsername(),
                                authorities
                        )
                )
                .toList();
        this.invitationsRepository.saveAll(invitations);
    }
}
