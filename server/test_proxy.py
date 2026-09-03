#!/usr/bin/env python3
"""
Black-box tests for the Infotify news proxy.

These exercise the deployed endpoint rather than a local copy, because the proxy runs on
shared hosting with no PHP available locally — and because what matters is the behaviour
of what is actually serving the app.

The suite is deliberately frugal with upstream credits: the free plan allows 200 a day for
the whole app, so every test either reuses a cached query or is rejected before the proxy
calls upstream. Only the first test can cost a credit.

    python3 server/test_proxy.py [base_url]
"""

import json
import subprocess
import sys

BASE = sys.argv[1] if len(sys.argv) > 1 else "https://infotify-api.nativia.co"
TIMEOUT = 30

failures = []
passes = 0


def request(path):
    """
    Returns (status, headers, parsed_body_or_text).

    Uses curl rather than urllib because some Python installs ship without a CA bundle,
    and quietly disabling certificate verification inside a suite that asserts security
    properties would make those assertions meaningless.
    """
    result = subprocess.run(
        ["curl", "-sS", "-D", "-", "--max-time", str(TIMEOUT),
         "-A", "infotify-proxy-tests/1.0", f"{BASE}{path}"],
        capture_output=True, text=True, check=False,
    )
    raw = result.stdout
    head, _, body = raw.partition("\r\n\r\n")
    if not _:
        head, _, body = raw.partition("\n\n")

    status = 0
    headers = {}
    for line in head.splitlines():
        if line.upper().startswith("HTTP/"):
            parts = line.split()
            if len(parts) > 1 and parts[1].isdigit():
                status = int(parts[1])
        elif ":" in line:
            key, _, value = line.partition(":")
            headers[key.strip()] = value.strip()

    try:
        return status, headers, json.loads(body)
    except json.JSONDecodeError:
        return status, headers, body


def check(name, condition, detail=""):
    global passes
    if condition:
        passes += 1
        print(f"  ok   {name}")
    else:
        failures.append(f"{name} — {detail}")
        print(f"  FAIL {name}  {detail}")


def section(title):
    print(f"\n{title}")


# ---------------------------------------------------------------------------
section("health")
# ---------------------------------------------------------------------------
status, _, body = request("/v1/health")
check("health responds 200", status == 200, f"got {status}")
check("health reports ok", isinstance(body, dict) and body.get("status") == "ok", str(body)[:80])


# ---------------------------------------------------------------------------
section("envelope shape")
# ---------------------------------------------------------------------------
# One shared query reused throughout, so the whole suite costs at most one credit.
FEED = "/v1/feed?language=en&categories=technology,science,health,sports,business"
status, headers, body = request(FEED)

check("feed responds 200", status == 200, f"got {status}")
check("envelope has articles", isinstance(body, dict) and "articles" in body, str(body)[:80])
check("envelope has nextCursor key", isinstance(body, dict) and "nextCursor" in body)
check("envelope has cachedAt", isinstance(body, dict) and "cachedAt" in body)

articles = body.get("articles", []) if isinstance(body, dict) else []
check("page is capped at 10", len(articles) <= 10, f"got {len(articles)}")

if articles:
    a = articles[0]
    expected = {"id", "title", "description", "url", "imageUrl", "publishedAt",
                "source", "categories"}
    check("article carries exactly the documented keys", set(a.keys()) == expected,
          f"got {sorted(a.keys())}")
    check("source is an object with id/name/iconUrl",
          isinstance(a.get("source"), dict) and
          {"id", "name", "iconUrl"} == set(a["source"].keys()),
          str(a.get("source"))[:80])

    # The whole point of normalising server-side: the client must never receive an entry
    # it cannot open or date.
    check("no article lacks a url", all(x.get("url") for x in articles))
    check("no article lacks a title", all(x.get("title") for x in articles))
    check("no article lacks a publishedAt", all(x.get("publishedAt") for x in articles))
    check("dates are ISO-8601 Z, not the provider's format",
          all(x["publishedAt"].endswith("Z") and "T" in x["publishedAt"] for x in articles),
          articles[0].get("publishedAt"))
    check("blanks are null, never empty strings",
          all(x.get("description") != "" and x.get("imageUrl") != "" for x in articles))
    check("urls are unique — duplicates were dropped",
          len({x["url"] for x in articles}) == len(articles))
    check("source name is never blank",
          all((x.get("source") or {}).get("name") for x in articles))


# ---------------------------------------------------------------------------
section("cache — the reason this proxy exists")
# ---------------------------------------------------------------------------
_, headers2, _ = request(FEED)
cache = headers2.get("X-Infotify-Cache") or headers2.get("x-infotify-cache")
check("an identical request is served from cache",
      cache in ("HIT", "STALE"),
      f"X-Infotify-Cache: {cache!r} — without this, N users cost N upstream credits")


# ---------------------------------------------------------------------------
section("parameter allowlist — nothing reaches upstream verbatim")
# ---------------------------------------------------------------------------
status, _, body = request("/v1/feed?language=zz")
check("an unknown language is rejected", status == 400, f"got {status}")
check("rejection names the field",
      isinstance(body, dict) and "language" in str(body.get("detail", "")),
      str(body)[:80])

status, _, body = request("/v1/feed?language=en&categories=notareal,alsofake")
check("unknown categories are dropped rather than forwarded", status == 200, f"got {status}")

# Six categories: the provider rejects more than five outright, so the proxy truncates.
# A 200 here proves the cap is enforced before the upstream call, not after it fails.
six = "/v1/feed?language=en&categories=technology,science,health,sports,business,politics"
status, _, _ = request(six)
check("a sixth category is truncated, not passed through", status == 200, f"got {status}")


# ---------------------------------------------------------------------------
section("routing and secrets")
# ---------------------------------------------------------------------------
status, _, _ = request("/v1/nope")
check("unknown routes are 404", status == 404, f"got {status}")

status, _, body = request("/config.php")
check("config.php is not served", status in (403, 404), f"got {status}")
check("the API key never appears in a response", "pub_" not in str(body))

_, _, feed_body = request(FEED)
check("the API key never leaks through the feed", "pub_" not in json.dumps(feed_body))


# ---------------------------------------------------------------------------
print(f"\n{passes} passed, {len(failures)} failed")
for failure in failures:
    print(f"  - {failure}")
sys.exit(1 if failures else 0)
