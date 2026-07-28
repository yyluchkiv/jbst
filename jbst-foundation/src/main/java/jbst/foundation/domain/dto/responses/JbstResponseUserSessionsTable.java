package jbst.foundation.domain.dto.responses;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;
import static org.springframework.util.CollectionUtils.isEmpty;

public record JbstResponseUserSessionsTable(
        List<JbstResponseUserSession2> sessions,
        List<String> countries,
        boolean anyPresent,
        boolean anyProblem
) {
    public static JbstResponseUserSessionsTable of(List<JbstResponseUserSession2> sessions) {
        sessions.sort(comparing(JbstResponseUserSession2::current).reversed().thenComparing(JbstResponseUserSession2::where));
        var countries = sessions.stream()
                .map(JbstResponseUserSession2::country)
                .filter(StringUtils::hasLength)
                .distinct().sorted().collect(Collectors.toList());
        return new JbstResponseUserSessionsTable(
                sessions,
                countries,
                !isEmpty(sessions),
                sessions.stream().anyMatch(session -> !session.exception().isOk())
        );
    }

    public static JbstResponseUserSessionsTable random() {
        var sessions = new ArrayList<JbstResponseUserSession2>();
        sessions.add(JbstResponseUserSession2.fixedCurrent());
        IntStream.range(0, 100).forEach(element -> sessions.add(JbstResponseUserSession2.random()));
        return of(sessions);
    }
}
