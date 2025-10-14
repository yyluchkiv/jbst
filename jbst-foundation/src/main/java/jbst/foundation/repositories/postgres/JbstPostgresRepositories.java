package jbst.foundation.repositories.postgres;

public record JbstPostgresRepositories(
        PostgresJbstSettingsRepository jbstSettingsRepository,
        PostgresInvitationsRepository invitationsRepository,
        PostgresUsersTokensRepository usersTokensRepository,
        PostgresUsersRepository userRepository,
        PostgresUsersSessionsRepository userSessionRepository
) {
}
