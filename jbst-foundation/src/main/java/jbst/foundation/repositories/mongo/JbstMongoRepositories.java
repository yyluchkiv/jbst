package jbst.foundation.repositories.mongo;

public record JbstMongoRepositories(
        MongoJbstSettingsRepository settingsRepository,
        MongoJbstInvitationsRepository invitationsRepository,
        MongoJbstUsersTokensRepository usersTokensRepository,
        MongoJbstUsersRepository userRepository,
        MongoJbstUsersSessionsRepository userSessionRepository
) {
}
