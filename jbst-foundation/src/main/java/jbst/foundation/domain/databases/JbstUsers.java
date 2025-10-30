package jbst.foundation.domain.databases;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.ids.UserId;
import jbst.foundation.domain.plurals.Plurals;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Lombok
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class JbstUsers extends Plurals<JbstUser, UserId> {

    public JbstUsers(List<JbstUser> values) {
        super(values);
    }

    public static JbstUsers hardcoded() {
        return new JbstUsers(List.of(JbstUser.hardcoded()));
    }

    @JsonIgnore
    public Set<String> getUsernamesAsStrings() {
        return this.values.stream().map(user -> user.username().value()).collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<String> findUsernamesEnabled() {
        return this.values.stream().filter(JbstUser::enabled).map(user -> user.username().value()).collect(Collectors.toSet());
    }
}
