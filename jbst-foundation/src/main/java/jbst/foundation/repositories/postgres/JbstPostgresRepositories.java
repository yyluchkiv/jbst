package jbst.foundation.repositories.postgres;

public record JbstPostgresRepositories(
        JbstPostgresSettingsRepository settingsRepository,
        JbstPostgresInvitationsRepository invitationsRepository,
        JbstPostgresUsersTokensRepository usersTokensRepository,
        JbstPostgresUsersRepository userRepository,
        JbstPostgresUsersSessionsRepository userSessionRepository
) {
}
