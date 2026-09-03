# Infotify — Architecture

Reference for maintaining the app. Written to be read by someone (including future you)
who has not seen the code for six months.

## Layers and the one rule that matters

```
ui (Compose)  ──▶  presentation (ViewModel, UiState / Intent / Effect)
                        │
                        ▼
                   domain (use cases, models, repository INTERFACES)
                        ▲
                        │  implements
                   data (repository impls, Retrofit, Room, DataStore)
```

**The domain never imports Android, Retrofit, Room, or `R`.** It defines interfaces; the
data layer implements them; Hilt binds the two in `di/RepositoryModule.kt`.

This is the rule the previous codebase broke: every use case imported a concrete class
from `data`, which made the domain impossible to test in isolation. If you add a use case
that needs a new capability, add a method to a domain interface — never reach into `data`.

### Package map

| Package | Holds |
| --- | --- |
| `core/result` | `Outcome`, `AppError` — the vocabulary of success and failure |
| `domain/model` | `Article`, `Category`, `Language`, `ThemeMode` |
| `domain/repository` | Interfaces only |
| `domain/usecase` | One small class per operation |
| `data/remote/newsdata` | Wire format + mapper. **Provider-specific code lives only here** |
| `data/local` | Room database, migrations, DataStore |
| `data/repository` | Interface implementations |
| `designsystem` | Theme, tokens, reusable components |
| `presentation/<screen>` | `Contract.kt`, `ViewModel.kt`, `Screen.kt` |

## State: four phases, never three

Every list screen distinguishes **Loading / Content / Empty / Error**. Search adds **Idle**
(before any query), which is not the same as Empty (a query that found nothing).

The old build had only success/loading/error, so an empty-but-valid API response was routed
through the error path and told the user to check their connection. Do not collapse these
again.

`AppError` is a closed set of named causes, so each one gets a message that matches what
actually happened:

| Error | When | User sees |
| --- | --- | --- |
| `NoConnection` | `IOException` | "You are offline" + Retry |
| `QuotaExceeded` | HTTP 429 | "Daily limit reached", **no** Retry button — retrying cannot help |
| `InvalidCredentials` | HTTP 401/403 | "News unavailable" — our problem, not theirs |
| `Server(code)` | other 4xx/5xx | Generic + Retry |
| `Unexpected(cause)` | parse failures, anything else | Generic + Retry |

> Catch order in `NewsRepositoryImpl` matters: `MalformedJsonException` **extends
> `IOException`**, so it is caught first. Without that, a corrupt payload is reported as
> "you are offline". A unit test pins this.

## MVI, only where it pays

| Screen | Pattern | Why |
| --- | --- | --- |
| Feed, Search, Bookmarks | Full `UiState` + `Intent` + `Effect` | Real state machines, many user actions, one-shot effects |
| Settings | `StateFlow` + direct methods | A preferences form has no state machine |
| Main (theme + splash gate) | `StateFlow` | One decision |

Do not add an `Intent` sealed interface to a screen that has two toggles. Boilerplate for
its own sake is a cost, not a standard.

### State vs Effect

Ask: **must this survive a rotation?** Yes → `UiState`. No → `Effect`.

Effects go through a `Channel` consumed with `receiveAsFlow()`: opening a Custom Tab,
sharing, showing a snackbar. Navigation between tabs is state (the back stack); opening an
article is an effect (it leaves the app).

## Data source

**NewsData.io**, `GET /api/1/latest`. One endpoint serves both feed and search — passing
`q` turns it into a query.

Constraints that shaped the design, not preferences:

- **No article body.** `content` returns `"ONLY AVAILABLE IN PAID PLANS"`, and no provider
  licenses full-text redistribution anyway. Reading therefore happens at the publisher via
  Custom Tabs. Do not build an in-app reader.
- **200 credits/day.** Hence the 10-minute OkHttp cache. Anything that refetches on every
  screen entry will burn the quota.
- **Cursor pagination** via `nextPage`, not offsets.
- **`duplicate` flag** provided by the API; the mapper drops flagged rows and de-dupes by URL.
- **`pubDate` is `"yyyy-MM-dd HH:mm:ss"` UTC**, not ISO 8601. `NewsDataMapper` parses it.

### Changing provider

`NewsRepository` is the seam. A new provider means a new package under `data/remote/`, a
new mapper, and one changed binding. Nothing above `data` should need to know.

**The key belongs on a server.** It currently ships in the APK via `BuildConfig`, read from
`local.properties` (gitignored) or the `NEWSDATA_API_KEY` CI secret. That is better than
the previous hardcoded-and-committed key, but a key in an APK is still extractable. The
intended end state is a small caching proxy that holds the key, which also solves the quota
and lets the provider change without shipping an app update.

## Persistence

Room v2, one table: `article` (bookmarks). Schemas are exported to `app/schemas/` so
migrations can be diffed.

`MIGRATION_1_2` preserves existing bookmarks by reading the legacy rows out and re-parsing
the old Gson `source` blob **in Kotlin** — SQL would need the JSON1 extension, which is not
guaranteed on every API 24 device.

`publishedAt` is stored as an ISO-8601 string, not epoch millis, precisely so the
NewsAPI-era rows carried across unchanged.

## Conventions

- One screen = one package with `Contract.kt`, `ViewModel.kt`, `Screen.kt`.
- `UiState` is immutable, has defaults for every field, and is annotated `@Immutable`.
- Composables are stateless by default; state is hoisted to the caller.
- No `LiveData` in new code.
- Use cases are named `VerbNoun` (`GetLatestNews`, `ToggleBookmark`).
- Dispatchers are injected (`@IoDispatcher`), never referenced statically.
- Strings live in resources, including category labels.

## Testing

- `NewsDataMapperTest` — wire-format edge cases: bad dates, nulls, duplicates, unknown categories.
- `NewsRepositoryImplTest` — MockWebServer; asserts every HTTP status maps to the right `AppError`.
- `FeedViewModelTest` — the four phases, category switching, append de-duplication, effects.

A migrated screen arrives with its state tests. A screen without them is not migrated.

## Dependency updates

Versions are still inline literals; moving to `libs.versions.toml` is the next housekeeping
step. Update one axis at a time — Kotlin + KSP, then AGP, then the Compose BOM, then
AndroidX. Never two major bumps in one PR.
