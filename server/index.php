<?php
/**
 * Infotify news proxy — infotify.nativia.co
 *
 * Why this exists: the NewsData.io free plan allows 200 credits per DAY for the whole
 * API key. Shipping that key inside the APK means every installed copy of the app draws
 * from the same 200 credits, so the app saturates at roughly 200 openings a day across
 * all users, and a daily notification (1 credit per user per day) is arithmetically
 * impossible.
 *
 * This proxy fixes that: it holds the key server-side, and caches each distinct query
 * for 10 minutes. A thousand users asking for the same feed inside that window cost one
 * credit, not a thousand.
 *
 * It also normalises the payload into Infotify's own envelope, so switching news provider
 * later is a server change, not an app release.
 *
 * Endpoints:
 *   GET /v1/feed?categories=technology,science&language=fr[&country=][&page=]
 *   GET /v1/feed?q=climat&language=fr[&page=]
 *   GET /v1/health
 */

declare(strict_types=1);

require __DIR__ . '/config.php';

header('Content-Type: application/json; charset=utf-8');
header('X-Content-Type-Options: nosniff');

$path = parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?: '/';
$path = rtrim($path, '/');

switch ($path) {
    case '/v1/health':
        respond(200, ['status' => 'ok', 'time' => gmdate('c')]);
        break;
    case '/v1/feed':
        handleFeed();
        break;
    default:
        respond(404, ['error' => 'not_found']);
}

// ---------------------------------------------------------------------------
// Feed
// ---------------------------------------------------------------------------

function handleFeed(): void
{
    if (!rateLimitOk()) {
        respond(429, ['error' => 'rate_limited']);
    }

    try {
        $query = buildUpstreamQuery();
    } catch (InvalidArgumentException $e) {
        respond(400, ['error' => 'bad_request', 'detail' => $e->getMessage()]);
        return;
    }

    $cacheKey = sha1(json_encode($query));
    $cached = cacheRead($cacheKey, CACHE_TTL_SECONDS);

    if ($cached !== null) {
        header('X-Infotify-Cache: HIT');
        respondRaw(200, $cached);
        return;
    }

    $upstream = fetchUpstream($query);

    if ($upstream === null) {
        // Upstream is down or the quota is spent. A stale answer beats an error screen:
        // yesterday's headlines are still headlines, an empty state is nothing.
        $stale = cacheRead($cacheKey, STALE_TTL_SECONDS);
        if ($stale !== null) {
            header('X-Infotify-Cache: STALE');
            respondRaw(200, $stale);
            return;
        }
        respond(502, ['error' => 'upstream_unavailable']);
        return;
    }

    if (isset($upstream['status']) && $upstream['status'] === 'error') {
        $code = $upstream['results']['code'] ?? '';
        // Surface quota exhaustion honestly so the app can say "come back tomorrow"
        // rather than "check your connection".
        $status = ($code === 'RateLimitExceeded' || $code === 'TooManyRequests') ? 429 : 502;
        respond($status, ['error' => 'upstream_error', 'code' => $code]);
        return;
    }

    $payload = json_encode(normalise($upstream), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    cacheWrite($cacheKey, $payload);

    header('X-Infotify-Cache: MISS');
    respondRaw(200, $payload);
}

/**
 * Builds the upstream query from a strict allowlist.
 *
 * Nothing the client sends is forwarded verbatim. Without this, anyone who found the
 * endpoint could spend the quota on arbitrary queries.
 */
function buildUpstreamQuery(): array
{
    $query = ['language' => readLanguage()];

    $categories = readList('categories', VALID_CATEGORIES, MAX_CATEGORIES);
    $countries  = readList('country', VALID_COUNTRIES, MAX_COUNTRIES);
    $search     = readSearch();

    if ($search !== null) {
        $query['q'] = $search;
    }
    if ($categories !== []) {
        $query['category'] = implode(',', $categories);
    }
    if ($countries !== []) {
        $query['country'] = implode(',', $countries);
    }

    $page = $_GET['page'] ?? null;
    if (is_string($page) && $page !== '') {
        if (!preg_match('/^[A-Za-z0-9_-]{1,120}$/', $page)) {
            throw new InvalidArgumentException('invalid page cursor');
        }
        $query['page'] = $page;
    }

    $query['size'] = PAGE_SIZE;
    $query['removeduplicate'] = 1;

    return $query;
}

function readLanguage(): string
{
    $language = $_GET['language'] ?? DEFAULT_LANGUAGE;
    if (!is_string($language) || !in_array($language, VALID_LANGUAGES, true)) {
        throw new InvalidArgumentException('invalid language');
    }
    return $language;
}

/** Reads a comma-separated parameter, keeping only known values, capped at $max. */
function readList(string $name, array $allowed, int $max): array
{
    $raw = $_GET[$name] ?? '';
    if (!is_string($raw) || $raw === '') {
        return [];
    }
    $values = array_values(array_unique(array_filter(
        array_map('trim', explode(',', strtolower($raw))),
        static fn(string $v): bool => in_array($v, $allowed, true)
    )));

    if (count($values) > $max) {
        // The provider rejects more than 5; truncating is friendlier than a 400 the
        // user cannot act on.
        $values = array_slice($values, 0, $max);
    }
    return $values;
}

function readSearch(): ?string
{
    $q = $_GET['q'] ?? null;
    if (!is_string($q)) {
        return null;
    }
    $q = trim($q);
    if ($q === '') {
        return null;
    }
    if (mb_strlen($q) > MAX_QUERY_LENGTH) {
        $q = mb_substr($q, 0, MAX_QUERY_LENGTH);
    }
    return $q;
}

// ---------------------------------------------------------------------------
// Upstream
// ---------------------------------------------------------------------------

function fetchUpstream(array $query): ?array
{
    $query['apikey'] = NEWSDATA_API_KEY;
    $url = UPSTREAM_BASE . '?' . http_build_query($query);

    $ch = curl_init($url);
    curl_setopt_array($ch, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT        => UPSTREAM_TIMEOUT_SECONDS,
        CURLOPT_CONNECTTIMEOUT => 5,
        CURLOPT_FOLLOWLOCATION => false,
        CURLOPT_USERAGENT      => 'Infotify-Proxy/1.0 (+https://infotify.nativia.co)',
    ]);
    $body = curl_exec($ch);
    $httpCode = (int) curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    if ($body === false || $httpCode === 0) {
        return null;
    }

    $decoded = json_decode((string) $body, true);
    return is_array($decoded) ? $decoded : null;
}

/**
 * Converts the provider payload into Infotify's own envelope.
 *
 * Cleaning happens here rather than on the device so it can be fixed without shipping an
 * app update, and so a page never arrives with three unusable entries out of ten.
 */
function normalise(array $upstream): array
{
    $articles = [];
    $seen = [];

    foreach (($upstream['results'] ?? []) as $item) {
        if (!is_array($item) || ($item['duplicate'] ?? false) === true) {
            continue;
        }

        $url = trim((string) ($item['link'] ?? ''));
        $title = trim((string) ($item['title'] ?? ''));
        $publishedAt = toIso8601($item['pubDate'] ?? null);

        // An entry with no link, no title or no date is not something the app can open.
        if ($url === '' || $title === '' || $publishedAt === null || isset($seen[$url])) {
            continue;
        }
        $seen[$url] = true;

        $articles[] = [
            'id'          => (string) ($item['article_id'] ?? $url),
            'title'       => $title,
            'description' => nullIfBlank($item['description'] ?? null),
            'url'         => $url,
            'imageUrl'    => nullIfBlank($item['image_url'] ?? null),
            'publishedAt' => $publishedAt,
            'source'      => [
                'id'      => (string) ($item['source_id'] ?? ''),
                'name'    => trim((string) ($item['source_name'] ?? '')) ?: '—',
                'iconUrl' => nullIfBlank($item['source_icon'] ?? null),
            ],
            'categories'  => array_values(array_filter(
                array_map('strval', (array) ($item['category'] ?? [])),
                static fn(string $c): bool => in_array($c, VALID_CATEGORIES, true)
            )),
        ];
    }

    return [
        'articles'   => $articles,
        'nextCursor' => nullIfBlank($upstream['nextPage'] ?? null),
        'cachedAt'   => gmdate('c'),
    ];
}

/** The provider sends "2026-09-02 05:41:00" in UTC, which is not ISO 8601. */
function toIso8601(mixed $raw): ?string
{
    if (!is_string($raw) || trim($raw) === '') {
        return null;
    }
    $date = DateTimeImmutable::createFromFormat('Y-m-d H:i:s', trim($raw), new DateTimeZone('UTC'));
    return $date === false ? null : $date->format('Y-m-d\TH:i:s\Z');
}

function nullIfBlank(mixed $value): ?string
{
    if (!is_string($value)) {
        return null;
    }
    $value = trim($value);
    return $value === '' ? null : $value;
}

// ---------------------------------------------------------------------------
// Cache — plain files, kept outside the web root
// ---------------------------------------------------------------------------

function cachePath(string $key): string
{
    if (!is_dir(CACHE_DIR)) {
        @mkdir(CACHE_DIR, 0770, true);
    }
    return CACHE_DIR . '/' . $key . '.json';
}

function cacheRead(string $key, int $maxAgeSeconds): ?string
{
    $file = cachePath($key);
    if (!is_file($file)) {
        return null;
    }
    if (time() - (int) filemtime($file) > $maxAgeSeconds) {
        return null;
    }
    $contents = @file_get_contents($file);
    return $contents === false ? null : $contents;
}

function cacheWrite(string $key, string $payload): void
{
    $file = cachePath($key);
    // Write to a temporary file then rename, so a concurrent reader never sees a
    // half-written response.
    $tmp = $file . '.' . bin2hex(random_bytes(4)) . '.tmp';
    if (@file_put_contents($tmp, $payload) !== false) {
        @rename($tmp, $file);
    }
    if (random_int(1, 50) === 1) {
        cacheSweep();
    }
}

/** Occasional cleanup; shared hosting has no cron guarantee for this. */
function cacheSweep(): void
{
    foreach (glob(CACHE_DIR . '/*.json') ?: [] as $file) {
        if (time() - (int) filemtime($file) > STALE_TTL_SECONDS) {
            @unlink($file);
        }
    }
}

// ---------------------------------------------------------------------------
// Rate limiting
// ---------------------------------------------------------------------------

/**
 * A coarse per-IP limit. This is not authentication — any secret shipped in an APK is
 * extractable, so pretending otherwise would be theatre. It exists to stop a single
 * client from hammering the origin; the cache absorbs the rest.
 */
function rateLimitOk(): bool
{
    $ip = $_SERVER['HTTP_CF_CONNECTING_IP'] ?? $_SERVER['REMOTE_ADDR'] ?? 'unknown';
    $bucket = CACHE_DIR . '/rl_' . sha1((string) $ip . gmdate('YmdHi')) . '.txt';

    if (!is_dir(CACHE_DIR)) {
        @mkdir(CACHE_DIR, 0770, true);
    }

    $count = is_file($bucket) ? (int) @file_get_contents($bucket) : 0;
    if ($count >= RATE_LIMIT_PER_MINUTE) {
        return false;
    }
    @file_put_contents($bucket, (string) ($count + 1));
    return true;
}

// ---------------------------------------------------------------------------
// Responses
// ---------------------------------------------------------------------------

function respond(int $status, array $body): never
{
    http_response_code($status);
    echo json_encode($body, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

function respondRaw(int $status, string $payload): never
{
    http_response_code($status);
    echo $payload;
    exit;
}
