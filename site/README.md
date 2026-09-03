# infotify.nativia.co

The public site: home, privacy policy, support. Plain static HTML — no PHP, no build step.

## Why the API is not here

`infotify.nativia.co` serves this site. The news proxy lives on a **separate host**,
`infotify-api.nativia.co`, with its own document root and its own `.htaccess`.

They were split deliberately, before the first release of the rewritten app:

- A brochure changes often; an API must not. Sharing one document root means one
  `.htaccess` governs both, and a typo while editing a page takes news away from every
  user.
- Their needs conflict. The site wants to be indexed and cached for a long time; the API
  wants `noindex`, a short cache and a rate limit. One host cannot hold two `robots.txt`
  policies.
- The base URL is compiled into every APK. Moving it was free only while the app was
  unreleased; afterwards, the old route could never be retired.

**Do not add PHP routing back to this root.** `DirectoryIndex index.html` in `.htaccess`
exists because the proxy used to live here and Apache prefers `index.php` by default.

## Stable URLs

`/privacy` is referenced by the app's About screen and by the Play Store listing. It must
keep working. The extensionless rewrite in `.htaccess` maps it to `privacy.html`.

## Deploying

Upload the contents of this directory to the document root of `infotify.nativia.co`.
Then check both hosts, API first:

    curl -s -o /dev/null -w "%{http_code}\n" https://infotify-api.nativia.co/v1/health
    curl -s -o /dev/null -w "%{http_code}\n" https://infotify.nativia.co/
    curl -s -o /dev/null -w "%{http_code}\n" https://infotify.nativia.co/privacy
    curl -s -o /dev/null -w "%{http_code}\n" https://infotify.nativia.co/support

Expect 200 for all four. The full API suite is `python3 server/test_proxy.py`.

## Facts the privacy page states, and where they come from

- **No analytics or crash reporting.** True as built: the dependency list is Compose, Hilt,
  Room, Retrofit/OkHttp/Gson, Coil, WorkManager and androidx.browser. Nothing else.
- **What the rate limiter retains.** One counter file per address per minute, named after a
  SHA-1 of the IP and the minute, held outside the web root, swept within a day. No paths,
  no search terms, no user agents, no article data. The page says plainly that hashing an
  IP is not anonymising it — the address space is small enough to work backwards.

If either changes, the page changes with it.

## Screenshots

`img/` holds shots of the app. They must match the version on the Play Store: shipping
images of an unreleased build shows people an interface they cannot reach.
