package jbst.foundation.domain.jsons.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

import static java.time.LocalDateTime.parse;
import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF11;
import static jbst.foundation.domain.time.JbstTime.getTimestamp;

public class DatetimeTimestampUTCDTF11Deserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return getTimestamp(parse(jsonParser.getText(), DTF11), UTC);
    }
}
