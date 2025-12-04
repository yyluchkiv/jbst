package jbst.foundation.domain.ssh;

import com.jcraft.jsch.Session;
import jbst.foundation.domain.tuples.TuplePresence;
import lombok.*;

// Lombok
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class JbstSshSession {
    private final TuplePresence<Session> session;
    private final TuplePresence<Throwable> throwable;

    public static JbstSshSession success(Session session) {
        return new JbstSshSession(
                TuplePresence.present(session),
                TuplePresence.absent()
        );
    }

    public static JbstSshSession failure(Throwable throwable) {
        return new JbstSshSession(
                TuplePresence.absent(),
                TuplePresence.present(throwable)
        );
    }
}
