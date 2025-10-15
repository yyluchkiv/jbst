package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.services.BaseRegistrationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseRegistrationService implements BaseRegistrationService {

    // Repositories
    private final JbstInvitationsRepository invitationsRepository;
    private final JbstUsersRepository usersRepository;
    // Password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void register0(RequestUserRegistration0 request) {
        var hashPassword = this.bCryptPasswordEncoder.encode(request.password().value());
        this.usersRepository.saveAs(request, Password.of(hashPassword));
    }

    @Override
    public void register1(RequestUserRegistration1 request) {
        var invitation = this.invitationsRepository.findByCodeAsAny(request.code());
        var hashPassword = this.bCryptPasswordEncoder.encode(request.password().value());
        invitation = new JbstInvitation(
                invitation.id(),
                invitation.owner(),
                invitation.authorities(),
                invitation.code(),
                request.username()
        );
        this.usersRepository.saveAs(request, Password.of(hashPassword), invitation);
        this.invitationsRepository.saveAs(invitation);
    }
}
