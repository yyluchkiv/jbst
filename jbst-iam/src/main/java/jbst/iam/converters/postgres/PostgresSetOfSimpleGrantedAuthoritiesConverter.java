package jbst.iam.converters.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.constants.JbstConstants;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;

@Converter
public class PostgresSetOfSimpleGrantedAuthoritiesConverter implements AttributeConverter<Set<SimpleGrantedAuthority>, String> {

    @Override
    public String convertToDatabaseColumn(Set<SimpleGrantedAuthority> authorities) {
        return !isEmpty(authorities) ? authorities.stream().map(SimpleGrantedAuthority::getAuthority).sorted().collect(joining(JbstConstants.Symbols.SEMICOLON)) : "";
    }

    @Override
    public Set<SimpleGrantedAuthority> convertToEntityAttribute(String value) {
        return hasLength(value) ? getSimpleGrantedAuthorities(Stream.of(value.split(JbstConstants.Symbols.SEMICOLON))) : new HashSet<>();
    }
}
