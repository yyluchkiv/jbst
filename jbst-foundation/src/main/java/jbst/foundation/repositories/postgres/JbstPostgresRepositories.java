package jbst.foundation.repositories.postgres;

public record JbstPostgresRepositories(
        PostgresJbstSettingsRepository settingsRepository,
        PostgresJbstInvitationsRepository invitationsRepository,
        PostgresJbstUsersTokensRepository usersTokensRepository,
        PostgresJbstUsersRepository userRepository,
        PostgresJbstUsersSessionsRepository userSessionRepository
) {
}
