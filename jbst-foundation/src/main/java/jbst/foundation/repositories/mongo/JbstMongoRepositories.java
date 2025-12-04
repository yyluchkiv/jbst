package jbst.foundation.repositories.mongo;

public record JbstMongoRepositories(
        JbstMongoSettingsRepository settingsRepository,
        JbstMongoInvitationsRepository invitationsRepository,
        JbstMongoUsersTokensRepository usersTokensRepository,
        JbstMongoUsersRepository userRepository,
        JbstMongoUsersSessionsRepository userSessionRepository
) {
}
