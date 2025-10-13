package jbst.iam.repositories.mongodb;

public record JbstMongoRepositories(
        MongoJbstSettingsRepository jbstSettingsRepository,
        MongoInvitationsRepository invitationsRepository,
        MongoUsersTokensRepository usersTokensRepository,
        MongoUsersRepository userRepository,
        MongoUsersSessionsRepository userSessionRepository
) {
}
