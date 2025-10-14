package jbst.foundation.essense;

import jbst.foundation.domain.properties.base.DefaultUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

// TODO [YYL] merger Essense Constructor <-> Settings Service
public interface EssenceConstructor {
    long saveDefaultUsers(List<DefaultUser> defaultUsers);
    void saveInvitations(DefaultUser defaultUser, Set<SimpleGrantedAuthority> authorities);
}
