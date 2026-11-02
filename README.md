# Feature Flags API

A small REST API to manage feature flags and roll features out progressively to a percentage of users.

A flag has a unique `key`, an `enabled` switch and a `rolloutPercentage` (0 to 100). Client applications ask the API whether a flag is active for a given user, and the answer is **deterministic**: the same user always gets the same answer.

## Stack

- Java 21, Maven (wrapper included)
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Actuator)
- H2 in-memory database
- JUnit 5

## Run

Requires a JDK 21.

```bash
./mvnw spring-boot:run
```

The API listens on `http://localhost:8081`. The H2 database is in memory, so data is lost when the application stops.

Health check:

```bash
curl http://localhost:8081/actuator/health
# {"groups":["liveness","readiness"],"status":"UP"}
```

## API

| Method | Route | Responses |
|---|---|---|
| `GET` | `/api/flags` | 200 |
| `POST` | `/api/flags` | 201, 400 (invalid body), 409 (key already exists) |
| `GET` | `/api/flags/{key}` | 200, 404 |
| `PUT` | `/api/flags/{key}` | 200, 400, 404 |
| `DELETE` | `/api/flags/{key}` | 204, 404 |
| `GET` | `/api/flags/{key}/evaluate?userId=…` | 200, 400 (missing `userId`), 404 |
| `GET` | `/actuator/health` | 200 |

Validation: `key` must not be blank, `rolloutPercentage` must be between 0 and 100.

## Examples

Create a flag enabled for 30% of users:

```bash
curl -X POST http://localhost:8081/api/flags \
  -H "Content-Type: application/json" \
  -d '{"key":"new-dashboard","enabled":true,"rolloutPercentage":30}'
# {"key":"new-dashboard","enabled":true,"rolloutPercentage":30,"id":1}
```

List and read flags:

```bash
curl http://localhost:8081/api/flags
curl http://localhost:8081/api/flags/new-dashboard
```

Evaluate the flag for some users:

```bash
curl "http://localhost:8081/api/flags/new-dashboard/evaluate?userId=42"
# false
curl "http://localhost:8081/api/flags/new-dashboard/evaluate?userId=66"
# true
```

Change the percentage (or switch the flag off):

```bash
curl -X PUT http://localhost:8081/api/flags/new-dashboard \
  -H "Content-Type: application/json" \
  -d '{"key":"new-dashboard","enabled":false,"rolloutPercentage":50}'
```

Delete it:

```bash
curl -X DELETE http://localhost:8081/api/flags/new-dashboard
```

## How the evaluation works

A flag is active for a user when:

1. the flag is `enabled`, **and**
2. `Math.floorMod(Objects.hash(userId, key), 100) < rolloutPercentage`.

The hash turns each `(userId, key)` pair into a fixed number between 0 and 99 (a "bucket"). The user is in the rollout if their bucket is below the percentage.

- **Stable**: a user's bucket never changes, so the answer never flips between calls.
- **Gradual**: raising the percentage from 30 to 50 keeps every user who already had the feature.
- **0% means nobody, 100% means everybody**, and a disabled flag is always `false`.
- **Independent per flag**: the key is part of the hash, so two flags do not always pick the same users.
- **Statistical, not exact**: every user has the same fixed chance of being in the rollout. Over many users the share converges to the percentage, but a small group can deviate.

No user data is stored: the answer is recomputed from `userId` and `key` on each call.

## Tests

```bash
./mvnw verify
```

Unit tests cover the rule on the entity: 0% is always `false`, 100% is always `true`, the same user always gets the same answer, and a disabled flag is always `false`. A Spring context test checks that the application starts.

## Possible next steps

- React front end to manage flags
- PostgreSQL instead of H2
- DORA metrics (deployment frequency, change failure rate, …) to measure the impact of rollouts
- A stronger hash (e.g. Murmur3) for a more even distribution
- Separate request/response objects (DTOs) from the JPA entity
- CI with GitHub Actions
