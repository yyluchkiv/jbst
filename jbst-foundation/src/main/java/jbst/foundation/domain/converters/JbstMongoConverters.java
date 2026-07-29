package jbst.foundation.domain.converters;

import lombok.experimental.UtilityClass;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Spring Security 7 renamed the SimpleGrantedAuthority constructor parameter (role → authority) while the
 * persistent field stayed `role`, so Spring Data MongoDB can no longer bind stored documents to the
 * constructor. These converters keep the historical {"role": "..."} document format — no data migration.
 */
@UtilityClass
public class JbstMongoConverters {

    @ReadingConverter
    public enum SimpleGrantedAuthorityReadConverter implements Converter<Document, SimpleGrantedAuthority> {
        INSTANCE;

        @Override
        public SimpleGrantedAuthority convert(Document source) {
            return new SimpleGrantedAuthority(source.getString("role"));
        }
    }

    @WritingConverter
    public enum SimpleGrantedAuthorityWriteConverter implements Converter<SimpleGrantedAuthority, Document> {
        INSTANCE;

        @Override
        public Document convert(SimpleGrantedAuthority source) {
            return new Document("role", source.getAuthority());
        }
    }
}
