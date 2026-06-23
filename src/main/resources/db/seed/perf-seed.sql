-- Synthetic performance dataset.
-- Load only with the perf profile. These rows are not product/demo catalog data.
-- Target size: 100,000 rows in each primary table used by list/search queries.
-- The tasting_note batch intentionally concentrates rows under one user so owner-scoped
-- list/search queries can be measured against a worst-case personal history.

INSERT INTO destination (id, name, description, img_url, latitude, longitude, city_id, category_id, rating, img_object_key)
SELECT
    100000 + gs,
    'Perf Destination ' || gs,
    'Synthetic destination row for query and pagination performance testing. #' || gs,
    NULL,
    35.0000 + ((gs % 900)::double precision / 10000),
    135.0000 + ((gs % 900)::double precision / 10000),
    ((gs - 1) % 13) + 1,
    ((gs - 1) % 11) + 1,
    0,
    NULL
FROM generate_series(1, 100000) AS gs
ON CONFLICT (id) DO NOTHING;

INSERT INTO distillery (id, name, address, description, img_url, destination_id, latitude, longitude, rating, img_object_key)
SELECT
    100000 + gs,
    'Perf Distillery ' || gs,
    'Perf address ' || gs,
    'Synthetic distillery row for join and marker performance testing. #' || gs,
    NULL,
    100000 + gs,
    35.0000 + ((gs % 900)::double precision / 10000),
    135.0000 + ((gs % 900)::double precision / 10000),
    0,
    NULL
FROM generate_series(1, 100000) AS gs
ON CONFLICT (id) DO NOTHING;

INSERT INTO alcohol (id, name, strength, size, description, distillery_id, category_id, img_url, rating, datetime, stated_age, img_object_key)
SELECT
    100000 + gs,
    'ZZZ Dummy Alcohol ' || lpad(gs::text, 6, '0'),
    5 + (gs % 55),
    CASE
        WHEN gs % 3 = 0 THEN 330
        WHEN gs % 3 = 1 THEN 700
        ELSE 720
    END,
    'Synthetic alcohol row for search, filtering, and recommendation performance testing. #' || gs,
    100000 + (((gs - 1) % 100000) + 1),
    ((gs - 1) % 11) + 1,
    NULL,
    0,
    NULL,
    CASE WHEN gs % 5 = 0 THEN '12' ELSE NULL END,
    NULL
FROM generate_series(1, 100000) AS gs
ON CONFLICT (id) DO NOTHING;

INSERT INTO site_user (user_id, email, password, nickname, is_email_verified, is_locked, address, phone_number, created_at)
SELECT
    100000 + gs,
    'perf-user-' || gs || '@example.com',
    'perf-password-not-for-login',
    'PerfUser' || gs,
    FALSE,
    FALSE,
    NULL,
    NULL,
    CURRENT_TIMESTAMP
FROM generate_series(1, 100000) AS gs
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO review (review_id, user_id, item_type, target_id, rating, contents, status, created_at, modified_at)
SELECT
    100000 + gs,
    100000 + (((gs - 1) % 100000) + 1),
    'ALCOHOL',
    100000 + gs,
    (gs % 5) + 1,
    'Synthetic review content for aggregate and moderation performance testing. #' || gs,
    'VISIBLE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM generate_series(1, 100000) AS gs
ON CONFLICT (review_id) DO NOTHING;

INSERT INTO tasting_note (
    tasting_note_id,
    user_id,
    alcohol_id,
    alcohol_name,
    tasted_at,
    place_name,
    place_lat,
    place_lng,
    appearance,
    aroma,
    palate,
    finish,
    overall,
    pairing,
    memo,
    created_at,
    updated_at
)
SELECT
    500000 + gs,
    100001,
    100000 + (((gs - 1) % 100000) + 1),
    CASE
        WHEN gs % 10 = 0 THEN 'Perf Match Sake '
        ELSE 'ZZZ Dummy Alcohol '
    END || lpad(gs::text, 6, '0'),
    CURRENT_DATE - (gs % 365),
    CASE
        WHEN gs % 10 = 0 THEN 'Tokyo perfmatch bar'
        ELSE 'Osaka Test Place'
    END,
    35.0000000,
    135.0000000,
    3,
    3,
    3,
    3,
    3,
    CASE
        WHEN gs % 10 = 0 THEN 'perfmatch pairing'
        ELSE 'normal pairing'
    END,
    CASE
        WHEN gs % 10 = 0 THEN 'perfmatch memo'
        ELSE 'normal memo'
    END,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM generate_series(1, 100000) AS gs
ON CONFLICT (tasting_note_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('destination', 'id'), COALESCE((SELECT MAX(id) FROM destination), 1), TRUE);
SELECT setval(pg_get_serial_sequence('distillery', 'id'), COALESCE((SELECT MAX(id) FROM distillery), 1), TRUE);
SELECT setval(pg_get_serial_sequence('alcohol', 'id'), COALESCE((SELECT MAX(id) FROM alcohol), 1), TRUE);
SELECT setval(pg_get_serial_sequence('site_user', 'user_id'), COALESCE((SELECT MAX(user_id) FROM site_user), 1), TRUE);
SELECT setval(pg_get_serial_sequence('review', 'review_id'), COALESCE((SELECT MAX(review_id) FROM review), 1), TRUE);
SELECT setval(pg_get_serial_sequence('tasting_note', 'tasting_note_id'), COALESCE((SELECT MAX(tasting_note_id) FROM tasting_note), 1), TRUE);

CREATE INDEX IF NOT EXISTS idx_tasting_note_user_recent
ON tasting_note (user_id, tasted_at DESC, tasting_note_id DESC);

CREATE INDEX IF NOT EXISTS idx_tasting_note_user_alcohol_recent
ON tasting_note (user_id, alcohol_id, tasted_at DESC, tasting_note_id DESC);

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_tasting_note_alcohol_name_trgm
ON tasting_note USING gin (lower(alcohol_name) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_tasting_note_place_name_trgm
ON tasting_note USING gin (lower(COALESCE(place_name, '')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_tasting_note_pairing_trgm
ON tasting_note USING gin (lower(COALESCE(pairing, '')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_tasting_note_memo_trgm
ON tasting_note USING gin (lower(COALESCE(memo, '')) gin_trgm_ops);
