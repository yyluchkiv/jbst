package jbst.foundation.domain.dto.responses;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;
import static org.springframework.util.CollectionUtils.isEmpty;

public record ResponseUserSessionsTable(
        List<ResponseUserSession2> sessions,
        List<String> countries,
        boolean anyPresent,
        boolean anyProblem
) {
    public static ResponseUserSessionsTable of(List<ResponseUserSession2> sessions) {
        sessions.sort(comparing(ResponseUserSession2::current).reversed().thenComparing(ResponseUserSession2::where));
        var countries = sessions.stream()
                .map(ResponseUserSession2::country)
                .filter(StringUtils::hasLength)
                .distinct().sorted().collect(Collectors.toList());
        return new ResponseUserSessionsTable(
                sessions,
                countries,
                !isEmpty(sessions),
                sessions.stream().anyMatch(session -> !session.exception().isOk())
        );
    }

    public static ResponseUserSessionsTable random() {
        var sessions = new ArrayList<ResponseUserSession2>();
        sessions.add(ResponseUserSession2.hardcodedCurrent());
        IntStream.range(0, 100).forEach(element -> sessions.add(ResponseUserSession2.random()));
        return of(sessions);
    }
}
