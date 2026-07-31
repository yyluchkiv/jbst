# Registration via GitHub (OAuth) for jbst IAM

## Goal
After this task, jbst supports a third registration flow — "register with GitHub" — alongside the
existing `register0` (open) and `register1` (invitation-code) flows. A new endpoint
`POST /registration/register-github` accepts a GitHub OAuth authorization code, exchanges it for an
access token, fetches the GitHub user profile (login + primary email) via an extended
`JbstGithub` Feign client, and creates a jbst user with a generated internal password. The feature
is off by default and enabled/configured via a new `jbst.security.github` property block in
`JbstProperties`. Both Mongo and Postgres backends work identically; no database schema change.

## Assumptions
- The server-side "authorization code → token → profile" flow is used: the frontend performs the
  GitHub redirect and posts the resulting `code` to the new endpoint; jbst never renders GitHub UI.
- GitHub-registered users are stored as ordinary `JbstUser`s (GitHub login as username, GitHub
  primary email as email, random BCrypt password) — no new persisted fields, so no Liquibase change.
- Default authorities for GitHub-registered users come from configuration
  (`jbst.security.github.authorities`), mirroring how invitation authorities work.
- Registration endpoints are `denyAll()` by default today; the new endpoint follows the same
  pattern and is opened only when the feature is enabled.

## Stack / constraints
- Java 21, Spring Boot, existing OpenFeign infrastructure (`jbst-foundation/feigns`).
- GitHub OAuth Apps endpoints: `POST https://github.com/login/oauth/access_token`,
  `GET https://api.github.com/user`, `GET https://api.github.com/user/emails`.
- No public-API breakage in `jbst-foundation` (japicmp gate) — new classes/methods only.
- No Liquibase changelog edits.

## Affected files
```
jbst-foundation/src/main/java/jbst/foundation/
├── domain/properties/JbstProperties.java                          (modified — wire new github block under security)
├── domain/properties/base/JbstPropertySecurityGithub.java         (new — enabled, client-id, client-secret, authorities; fixed() factory)
├── domain/dto/requests/JbstRequestUserRegistrationGithub.java     (new — code, zoneId)
├── domain/events/JbstEventRegistrationGithub.java                 (new)
├── domain/events/JbstEventRegistrationGithubFailure.java          (new)
├── incidents/domain/registration/IncidentRegistrationGithub.java  (new — plus failure variant, mirroring register1 incidents)
├── feigns/github/JbstGithub.java                                  (modified — add OAuth token exchange + user/emails operations; register as bean)
├── resources/base/JbstRegistrationResource.java                   (modified — add POST /register-github)
├── services/JbstRegistrationService.java                          (modified — add registerGithub)
├── services/abstracts/JbstAbstractRegistrationService.java        (modified — token exchange, profile fetch, user creation)
├── services/mongo/JbstMongoRegistrationService.java               (modified — wire new dependency)
├── services/postgres/JbstPostgresRegistrationService.java         (modified — wire new dependency)
├── validators/JbstRegistrationValidator.java                      (modified — add validateRegistrationRequestGithub)
├── validators/abtracts/JbstAbstractRegistrationValidator.java     (modified — feature-enabled + username/email uniqueness checks)
├── configurations/JbstConfigurationSecurityJwt.java               (modified — matcher for /registration/register-github: permitAll when enabled, denyAll otherwise)
jbst-foundation/src/test/java/jbst/foundation/
├── resources/base/JbstRegistrationResourceTest.java               (modified — endpoint tests)
├── services/abstracts/JbstAbstractRegistrationServiceTest.java    (modified — registerGithub tests, mocked Feign)
├── validators/abstracts/JbstAbstractRegistrationValidatorTest.java (modified — validator tests)
jbst-server-iam/src/main/resources/
├── application.yml                                                (modified — jbst.security.github block, disabled by default)
├── application-dev.yml                                            (modified — dev example values, Jasypt-encrypted secret)
CHANGELOG.md                                                       (modified — feat line)
```

## Ordered steps
1. Add `JbstPropertySecurityGithub` (enabled, client-id, client-secret, authorities, `fixed()`
   factory) and wire it into `JbstProperties.Security`; add the YAML block (disabled) to the IAM
   server's `application.yml` and dev example values to `application-dev.yml`.
2. Extend `JbstGithub` Feign client with the OAuth token-exchange operation and
   `GET /user` + `GET /user/emails` operations; register it as a Spring bean in the foundation
   configuration so services can inject it.
3. Add the `JbstRequestUserRegistrationGithub` DTO, registration/failure events, and the matching
   incident classes, mirroring the existing register1 set.
4. Implement `registerGithub` in `JbstRegistrationService` / `JbstAbstractRegistrationService`:
   exchange code → token, fetch profile + primary verified email, create `JbstUser` with configured
   authorities and a random BCrypt password; wire the Mongo and Postgres service subclasses.
5. Add `validateRegistrationRequestGithub` to the validator hierarchy: reject when the feature is
   disabled, when the GitHub login collides with an existing username, or the email is taken.
6. Add `POST /registration/register-github` to `JbstRegistrationResource` (validator → service →
   event → incident → extension hook, same shape as register1) and the security matcher in
   `JbstConfigurationSecurityJwt` (permitAll when `jbst.security.github.enabled`, else denyAll).
7. Extend the three existing registration test classes (resource, service, validator) covering:
   happy path, feature disabled, GitHub API failure, username/email collision.
8. Update `CHANGELOG.md` with a `feat:` line (two trailing spaces).

## Risks
- GitHub's token-exchange endpoint lives on `github.com`, not `api.github.com`, so a single Feign
  target can't serve both. Mitigation: use two Feign definitions inside `JbstGithub` (one per host),
  following the multi-target pattern already used by other jbst feigns.
- A GitHub account with no public/verified email would produce a user with a null email and break
  email-based flows. Mitigation: validator rejects registration when no verified primary email is
  returned, with a clear error message.
- Changing `JbstRegistrationService`'s interface adds a method to a published public API; while
  additive changes are binary-compatible, japicmp must confirm. Mitigation: run `./mvnw clean verify`
  locally and inspect `jbst-foundation/target/japicmp/` before shipping.

## Verification
```bash
./compile-all.sh
./mvnw test -Dtest=JbstRegistrationResourceTest
./mvnw test -Dtest=JbstAbstractRegistrationServiceTest
./mvnw test -Dtest=JbstAbstractRegistrationValidatorTest
./mvnw clean verify
```
Smoke test (dev, after `docker/run-mongo.sh` + `run-mongodb.sh`, with the feature enabled and a
test OAuth app configured):
```bash
curl -X POST http://localhost:3002/api/jbst/security/registration/register-github -H 'Content-Type: application/json' -d '{"code":"<github-oauth-code>","zoneId":"Europe/Kyiv"}'
```
Done when: `./mvnw clean verify` passes and a GitHub OAuth code posted to `/registration/register-github` creates a login-capable jbst user on both Mongo and Postgres profiles.
