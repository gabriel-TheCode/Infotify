# Infotify — Architecture

Reference for maintaining the app. Written to be read by someone — including future you —
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

This is the rule the original codebase broke: every use case imported a concrete class
from `data`, which made the domain impossible to test in isolation. If you add a use case
that needs a new capability, add a method to a domain interface — never reach into `data`.

### Package map

| Package | Holds |
| --- | --- |
| `core/result` | `Outcome`, `AppError` — the vocabulary of success and failure |
| `domain/model` | `Article`, `Topic`, `Region`, `Interests`, `Language`, `ThemeMode` |
| `domain/repository` | Interfaces only, plus `BriefingScheduler` |
| `domain/usecase` | One small class per operation |
| `data/remote/infotify` | The proxy's wire format and its mapper |
| `data/local` | Room database, migrations, DataStore, the offline feed cache |
| `data/repository` | Interface implementations |
| `designsystem` | Theme, tokens, reusable components |
| `presentation/<screen>` | `Contract.kt`, `ViewModel.kt`, `Screen.kt` |
| `notification` | The daily briefing worker and its WorkManager scheduler |

## The news pipeline

```
app ──▶ infotify-api.nativia.co ──▶ NewsData.io
```

The app talks only to Infotify's own proxy. **It carries no API key**, and the base URL is
`AppConstants.INFOTIFY_BASE_URL`.

The proxy exists for an arithmetic reason, not for tidiness. The provider's free tier
allows **200 requests a day for the entire app**, not per user — a key inside the APK
would burn that as soon as the app had users. The proxy holds the key and caches for ten
minutes, so many readers cost one upstream request.

It also normalises: ISO-8601 dates, duplicates removed, and no entry without a title, URL
or date. That work lives on the server so bad data can be fixed without an app release —
which is also why `FeedMapper` is thin and only defends against unexpected payloads.

The site at `infotify.nativia.co` is a **separate host** with its own document root. A
brochure changes often and an API must not; sharing one `.htaccess` would let a typo in a
page take news away from every user.

### Changing provider

`NewsRepository` is the seam, and the proxy is a second one. A new provider means changes
in `server/`, and quite possibly no app release at all.

## State: four phases, never three

Every list screen distinguishes **Loading / Content / Empty / Error**. Search adds **Idle**
(before any query), which is not the same as Empty (a query that found nothing).

The original build had only success/loading/error, so an empty-but-valid API response was
routed through the error path and told the user to check their connection. Do not collapse
these again.

`AppError` is a closed set of named causes, so each gets a message that matches what
actually happened:

| Error | When | User sees |
| --- | --- | --- |
| `NoConnection` | `IOException` | "You are offline" + Retry |
| `QuotaExceeded` | HTTP 429 | "Daily limit reached", **no** Retry — retrying cannot help |
| `InvalidCredentials` | HTTP 401/403 | "News unavailable" — our problem, not theirs |
| `Server(code)` | other 4xx/5xx | Generic + Retry |
| `Unexpected(cause)` | parse failures, anything else | Generic + Retry |

> Catch order in `NewsRepositoryImpl` matters: `MalformedJsonException` **extends
> `IOException`**, so it is caught first. Without that, a corrupt payload is reported as
> "you are offline". A unit test pins this.

## Interests

Two dimensions, because the provider has two.

- `Topic` — the 15 real category values, verified against the live API.
- `Region` — a set of country codes. This is the only way a region such as Africa can
  exist: it is not a category upstream, and neither is "Société".

**Five subjects maximum.** That is the provider's hard cap, not a design preference: a
query carrying more is rejected outright. `Interests.toggle` enforces it and the picker
disables further selection, so the limit reads as intent rather than as a bug.

All five travel in **one** request. A personalised feed therefore costs the same single
upstream credit as one category. `NewsRepositoryImplTest` pins this; if it ever became one
request per interest, the shared daily quota would be gone in minutes.

## Offline

The last successful **first page** of each feed is stored in `cached_article` and served
when the network fails.

- First pages only — a cached page 3 without 1 and 2 is not a feed, and appending already
  implies a live connection.
- Never search — a remembered result for a query typed minutes ago is not useful.
- Keyed by feed **and language**, so French is not overwritten by English.
- Replaced inside a transaction: process death between the delete and the insert would
  leave an empty cache and no network, worse than the stale page it replaced.

A remembered page returns `Outcome.Success` with `ArticlePage.cachedAt` set, and the feed
shows a banner naming its age. Serving yesterday's headlines as though they were live is a
worse failure than showing an error. Its `nextCursor` is dropped, because a cursor from a
finished session cannot be paged.

There are two cache layers. OkHttp's disk cache answers for five minutes without touching
the network at all; the Room cache is the fallback beneath it, for when that has expired.

## MVI, only where it pays

| Screen | Pattern | Why |
| --- | --- | --- |
| Feed, Search, Bookmarks | Full `UiState` + `Intent` + `Effect` | Real state machines, many actions, one-shot effects |
| Settings, Onboarding, Interests | `StateFlow` + direct methods | A form has no state machine |
| Main (theme + splash gate) | `StateFlow` | One decision |

Do not add an `Intent` sealed interface to a screen with two toggles. Boilerplate for its
own sake is a cost, not a standard.

### State vs Effect

Ask: **must this survive a rotation?** Yes → `UiState`. No → `Effect`.

Effects go through a `Channel` consumed with `receiveAsFlow()`: opening a Custom Tab,
sharing, showing a snackbar. Navigating between tabs is state; opening an article is an
effect, because it leaves the app.

### Derived feeds

`FeedViewModel` derives its content from `(mode, topic, interests, language)` through
`flatMapLatest`. That is deliberate: language was once read with `.first()` at request time
and never observed, so changing it in Settings did nothing. `flatMapLatest` also cancels
the in-flight request on any input change, so a stale response cannot overwrite a newer
one.

## Persistence

Room, currently **version 3**. Schemas are exported to `app/schemas/` so migrations can be
diffed.

| Migration | Does |
| --- | --- |
| 1 → 2 | Rebuilds `article` for the new bookmark shape, **preserving existing rows** by reading them out and re-parsing the old Gson `source` blob in Kotlin (SQL would need the JSON1 extension, not guaranteed on every device) |
| 2 → 3 | Adds `cached_article`. Purely additive: a destructive migration would take the bookmarks with it |

`publishedAt` is stored as an ISO-8601 string rather than epoch millis, precisely so the
NewsAPI-era rows carried across unchanged.

## The daily briefing

WorkManager, at an hour the user picks, default 07:30 local. `SetDailyBriefing` both
persists the preference and schedules the work, so the two cannot disagree — a switch that
persists without scheduling would be a lie.

**The silence rule:** nothing published in the user's subjects since the last briefing
means **no notification**. A daily "there is news today" that fires regardless teaches
people to ignore the app, then to uninstall it. The notification carries the headline
itself plus a count, so something is read even when it is not opened.

`POST_NOTIFICATIONS` is requested at the end of onboarding, after the value has been shown,
and checked again before every post.

## Identity

Bricolage Grotesque for the wordmark and headlines, Schibsted Grotesk for everything else.
Both variable, both SIL OFL, both bundled — no downloadable-font round trip and no
licensing risk. (The original build shipped Apple's San Francisco, which is not licensed
for Android.)

`minSdk 26` is partly a typographic decision: variable-font axes are only honoured from
API 26.

The launcher icon, the splash icon and the wordmark are the same letter — the Bricolage
"i" with its dot detached. The dot is offset to the right of the stem: that asymmetry is
what keeps the silhouette from resolving into a generic information glyph.

Motion: the dot falls into place. It appears at the splash and nowhere else.

## Conventions

- One screen = one package with `Contract.kt`, `ViewModel.kt`, `Screen.kt`.
- `UiState` is immutable, `@Immutable`, with defaults for every field.
- Composables are stateless by default; state is hoisted to the caller.
- No `LiveData` in new code.
- Use cases are named `VerbNoun` (`GetLatestNews`, `ToggleBookmark`).
- Dispatchers are injected (`@IoDispatcher`), never referenced statically.
- Strings live in resources, including topic and region labels.
- Screens outside the `Scaffold` must apply `WindowInsets.safeDrawing` themselves —
  edge-to-edge is on, and onboarding once rendered its button under the navigation bar.

## Testing

38 unit tests.

- `FeedMapperTest` — defensive parsing of the proxy envelope.
- `NewsRepositoryImplTest` — every HTTP status maps to the right `AppError`; interests
  travel in one request.
- `OfflineFallbackTest` — the cache is written, served when the network dies, scoped per
  feed and language, and never used for search or later pages.
- `InterestsTest` — the five-subject cap and the region toggle.
- `FeedViewModelTest` — the four phases, language reactivity, append de-duplication,
  effects, and the cache stamp reaching the UI.
- `server/test_proxy.py` — 25 black-box tests against the deployed proxy.

A migrated screen arrives with its state tests. A screen without them is not migrated.

## Release

R8 is on: 24.7 MB → 3.47 MB. The keep rules cover what R8 cannot infer — Gson DTOs, the
Room entity, Retrofit interfaces, `@Serializable` navigation routes, and the worker.

> A keep rule naming a package that no longer exists protects nothing. One did, and with
> R8 enabled Gson would have found renamed fields and every article would have arrived
> null: no crash, no stack trace, an empty feed. Check the rules when you move a package.

Build `assembleReleaseTest` to install and exercise the minified app — it is the release
configuration signed with the debug key, so real release signing stays out of local
testing.

## Dependency updates

Versions are still inline literals; moving to `libs.versions.toml` is the next housekeeping
step, along with kapt → KSP. Update one axis at a time — Kotlin + KSP, then AGP, then the
Compose BOM, then AndroidX. Never two major bumps in one PR.
