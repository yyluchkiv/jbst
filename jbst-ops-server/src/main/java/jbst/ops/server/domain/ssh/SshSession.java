package jbst.ops.server.domain.ssh;

import com.jcraft.jsch.Session;
import jbst.foundation.domain.tuples.TuplePresence;
import lombok.*;

// Lombok
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class SshSession {
    private final TuplePresence<Session> session;
    private final TuplePresence<Throwable> throwable;

    public static SshSession success(Session session) {
        return new SshSession(
                TuplePresence.present(session),
                TuplePresence.absent()
        );
    }

    public static SshSession failure(Throwable throwable) {
        return new SshSession(
                TuplePresence.absent(),
                TuplePresence.present(throwable)
        );
    }
}
