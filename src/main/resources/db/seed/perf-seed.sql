-- Synthetic performance dataset.
-- Load only with the perf profile. These rows are not product/demo catalog data.
-- Target size: 100,000 rows in each primary table used by list/search queries.

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

SELECT setval(pg_get_serial_sequence('destination', 'id'), COALESCE((SELECT MAX(id) FROM destination), 1), TRUE);
SELECT setval(pg_get_serial_sequence('distillery', 'id'), COALESCE((SELECT MAX(id) FROM distillery), 1), TRUE);
SELECT setval(pg_get_serial_sequence('alcohol', 'id'), COALESCE((SELECT MAX(id) FROM alcohol), 1), TRUE);
SELECT setval(pg_get_serial_sequence('site_user', 'user_id'), COALESCE((SELECT MAX(user_id) FROM site_user), 1), TRUE);
SELECT setval(pg_get_serial_sequence('review', 'review_id'), COALESCE((SELECT MAX(review_id) FROM review), 1), TRUE);
