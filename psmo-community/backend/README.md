# community

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                               | Description                                                 |
| ----------------------------------------------------|------------------------------------------------------------- |
| [Routing](https://start.ktor.io/p/routing-default) | Allows to define structured routes and associated handlers. |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
| -----------------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew build`                       | Build everything                                                     |
| `./gradlew buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `./gradlew buildImage`                  | Build the docker image to use with the fat JAR                       |
| `./gradlew publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `./gradlew run`                         | Run the server                                                       |
| `./gradlew runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

## Telegram Login Callback

- Endpoint: `POST /api/auth/telegram`
- Body: `application/x-www-form-urlencoded` payload directly from the Telegram Login Widget
- Expected fields include `id`, `first_name`, `username`, `photo_url`, `auth_date`, `hash`, etc.
- Server validates Telegram signature using `telegram.botToken` and rejects expired payloads (default tolerance 86,400 seconds).
- Configure secrets via `TELEGRAM_BOT_TOKEN` (mandatory) and optional `TELEGRAM_AUTH_TOLERANCE`; the YAML merely references those environment variables.
- On success the backend persists/updates the Telegram user profile, issues a JWT access token, and returns both the normalized user object and token metadata.

### Authenticated Profile API

- Endpoint: `GET /api/me`
- Requires: `Authorization: Bearer <access_token>` header (token from Telegram login response)
- Returns the persisted user profile (same shape as Telegram auth response) when the JWT is valid.
