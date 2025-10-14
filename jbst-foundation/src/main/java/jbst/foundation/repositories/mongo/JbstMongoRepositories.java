package jbst.foundation.repositories.mongo;

public record JbstMongoRepositories(
        MongoJbstSettingsRepository jbstSettingsRepository,
        MongoInvitationsRepository invitationsRepository,
        MongoUsersTokensRepository usersTokensRepository,
        MongoUsersRepository userRepository,
        MongoUsersSessionsRepository userSessionRepository
) {
}
