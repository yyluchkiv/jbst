package jbst.ops.server.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jbst.ops.server.domain.storage.AccessCode;
import jbst.ops.server.exceptions.SlackRuntimeException;
import jbst.ops.server.utils.MessagesUtils;

import java.io.InputStream;
import java.time.Duration;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorageService {

    // Utilities
    private final MessagesUtils messagesUtils;

    private final Cache<AccessCode, InputStream> cache = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(5L)).build();

    public final AccessCode saveStream(InputStream stream) {
        var accessCode = AccessCode.rnd();
        this.cache.put(accessCode, stream);
        return accessCode;
    }

    public final InputStream getStream(AccessCode accessCode) {
        var stream = this.cache.getIfPresent(accessCode);
        if (isNull(stream)) {
            throw new SlackRuntimeException(this.messagesUtils.getExpiredAccessCodeMessage(accessCode.value()));
        }
        return stream;
    }
}
