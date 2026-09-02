<?php
/**
 * Copy to config.php on the server and fill in the key.
 * config.php holds the secret and is never committed.
 */

declare(strict_types=1);

// --- Secret -----------------------------------------------------------------
const NEWSDATA_API_KEY = 'pub_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx';

// --- Upstream ---------------------------------------------------------------
const UPSTREAM_BASE = 'https://newsdata.io/api/1/latest';
const UPSTREAM_TIMEOUT_SECONDS = 15;

/** Free plan caps a page at 10 articles. */
const PAGE_SIZE = 10;

// --- Cache ------------------------------------------------------------------
/** Outside the web root: nothing here should ever be servable over HTTP. */
const CACHE_DIR = '/home/u210161763/infotify_cache';

/** Fresh window. This is what turns N users into 1 upstream credit. */
const CACHE_TTL_SECONDS = 600;

/** How long a stale entry may still rescue a failed upstream call. */
const STALE_TTL_SECONDS = 86400;

// --- Abuse control ----------------------------------------------------------
const RATE_LIMIT_PER_MINUTE = 60;
const MAX_QUERY_LENGTH = 120;

// --- Allowlists -------------------------------------------------------------
/** Verified against the live API: anything outside this list is rejected upstream. */
const VALID_CATEGORIES = [
    'top', 'world', 'politics', 'business', 'technology', 'science', 'health',
    'sports', 'entertainment', 'environment', 'education', 'food', 'tourism',
    'crime', 'lifestyle', 'domestic', 'other',
];

const VALID_LANGUAGES = ['en', 'fr', 'es', 'de', 'it', 'pt', 'nl', 'ru', 'ar'];

/** Provider caps a query at 5 categories and 5 countries. */
const MAX_CATEGORIES = 5;
const MAX_COUNTRIES = 5;

const DEFAULT_LANGUAGE = 'en';

/**
 * Countries the app groups into regions. "Africa" is not a category at the provider —
 * it is a set of country codes, which is how the app builds a regional interest.
 */
const VALID_COUNTRIES = [
    // Africa
    'ng', 'za', 'ke', 'cm', 'ci', 'sn', 'gh', 'ma', 'dz', 'tn', 'eg', 'et', 'tz', 'ug', 'cd',
    // Europe
    'fr', 'be', 'ch', 'gb', 'de', 'es', 'it', 'pt', 'nl',
    // Americas
    'us', 'ca', 'br', 'mx', 'ar',
    // Asia / Oceania
    'in', 'cn', 'jp', 'au', 'ae', 'sa',
];
