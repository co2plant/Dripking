INSERT INTO category (category_id, name, description)
SELECT seed.id, seed.name,
       '맥아 효소로 녹말을 포함하고 있는 곡물 재료를 당화시키고 발효[5] 및 증류하여 오크통에 숙성시킨 증류주. 간단히 말해서 목통숙성곡물증류주(木桶熟成穀物蒸溜酒)라고 할 수 있다. ' || seed.id
FROM (
    VALUES
        (1, '위스키'),
        (2, '럼'),
        (3, '보드카'),
        (4, '진'),
        (5, '데킬라'),
        (6, '브랜디'),
        (7, '리큐르'),
        (8, '맥주'),
        (9, '사케'),
        (10, '전통주')
) AS seed(id, name)
ON CONFLICT (category_id) DO NOTHING;

INSERT INTO country (country_id, name, description)
VALUES
    (1, '일본', '국가 설명 1'),
    (2, '한국', '국가 설명 2'),
    (3, '미국', '국가 설명 3')
ON CONFLICT (country_id) DO NOTHING;

INSERT INTO city (city_id, name, description, country_id)
VALUES
    (1, '오사카', '일본 오사카부의 현청 소재지이자, 일본 제2의 도시', 1)
ON CONFLICT (city_id) DO NOTHING;

INSERT INTO destination (id, name, description, img_url, latitude, longitude, city_id, category_id, rating, img_object_key)
VALUES
    (
        1,
        '오야마자키',
        '오야마자키정은 교토부 오토쿠니군의 정이다. 오사카부 미시마군 시마모토정과 접한다. 교토부에서 가장 면적의 작은 정이다. 옛 야마시로국의 오토쿠니군에 속했다',
        'https://upload.wikimedia.org/wikipedia/commons/8/80/Oyamazaki_stn.jpg',
        34.9023747484815,
        135.68551060039104,
        1,
        NULL,
        0,
        NULL
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO distillery (id, name, address, description, img_url, destination_id, latitude, longitude, rating, img_object_key)
VALUES
    (
        1,
        'Yamazaki Distillery',
        '5 Chome-2-1 Yamazaki, Shimamoto, Mishima District, Osaka 618-0001 일본',
        'Distillery Description ',
        'https://upload.wikimedia.org/wikipedia/commons/f/f0/Yamazaki_Distillery_%E5%B1%B1%E5%B4%8E%E8%92%B8%E7%95%99%E6%89%8005.jpg',
        1,
        34.89279866199688,
        135.6744575716837,
        0,
        NULL
    ),
    (
        2,
        'Asahi Beer Oyamazaki Villa Museum of Art',
        '일본 〒618-0071 Kyoto, Otokuni District, Oyamazaki, Zenihara−５−3',
        'Distillery Description ',
        'https://upload.wikimedia.org/wikipedia/commons/f/f0/Yamazaki_Distillery_%E5%B1%B1%E5%B4%8E%E8%92%B8%E7%95%99%E6%89%8005.jpg',
        1,
        34.89564237533231,
        135.6797393092974,
        0,
        NULL
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO destination (id, name, description, img_url, latitude, longitude, city_id, category_id, rating, img_object_key)
SELECT
    gs + 1,
    'Destination ' || gs,
    'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.' || gs,
    'https://upload.wikimedia.org/wikipedia/commons/e/ea/Taipei_Skyline_2022.06.29.jpg',
    37.5665,
    126.9780,
    1,
    NULL,
    0,
    NULL
FROM generate_series(1, 1000) AS gs
ON CONFLICT (id) DO NOTHING;

INSERT INTO distillery (id, name, address, description, img_url, destination_id, latitude, longitude, rating, img_object_key)
SELECT
    gs + 2,
    'Distillery ' || gs,
    'Address ' || gs,
    'Distillery Description ' || gs,
    'https://upload.wikimedia.org/wikipedia/commons/f/f0/Yamazaki_Distillery_%E5%B1%B1%E5%B4%8E%E8%92%B8%E7%95%99%E6%89%8005.jpg',
    gs,
    37.5665,
    126.9780,
    0,
    NULL
FROM generate_series(1, 1000) AS gs
ON CONFLICT (id) DO NOTHING;

INSERT INTO tag (tag_id, name, description)
SELECT gs, 'Tag ' || gs, 'Description ' || gs
FROM generate_series(1, 1000) AS gs
ON CONFLICT (tag_id) DO NOTHING;

INSERT INTO alcohol (id, name, strength, size, description, distillery_id, category_id, img_url, rating, datetime, stated_age, img_object_key)
SELECT
    gs,
    'Alcohol ' || gs,
    40 + gs,
    700 + gs,
    'Alcohol Description ' || gs,
    gs,
    (gs % 10) + 1,
    'https://upload.wikimedia.org/wikipedia/commons/e/e5/Jim_Beam_White_Label.jpg',
    0,
    NULL,
    NULL,
    NULL
FROM generate_series(1, 1000) AS gs
ON CONFLICT (id) DO NOTHING;

INSERT INTO site_user (user_id, email, password, nickname, is_email_verified, is_locked, address, phone_number, created_at)
SELECT
    gs,
    'User' || gs || '@example.com',
    'password',
    'User' || gs,
    FALSE,
    FALSE,
    NULL,
    NULL,
    CURRENT_TIMESTAMP
FROM generate_series(1, 1000) AS gs
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO review (review_id, user_id, item_type, target_id, rating, contents, status, created_at, modified_at)
SELECT
    gs,
    gs,
    'ALCOHOL',
    gs,
    5,
    '리버시블이라활용도가좋네요.어느정도두께감이있어서따뜻할것같아요.플리스촉감도좋고,부드럽네요.착용해보니양쪽다무난하니다예쁘네요.블랙색상이라때도안타고좋아요.무난해서암때나착용가능해서좋아요.' || E'\n\n' || gs,
    'VISIBLE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM generate_series(1, 1000) AS gs
ON CONFLICT (review_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('category', 'category_id'), COALESCE((SELECT MAX(category_id) FROM category), 1), TRUE);
SELECT setval(pg_get_serial_sequence('country', 'country_id'), COALESCE((SELECT MAX(country_id) FROM country), 1), TRUE);
SELECT setval(pg_get_serial_sequence('city', 'city_id'), COALESCE((SELECT MAX(city_id) FROM city), 1), TRUE);
SELECT setval(pg_get_serial_sequence('destination', 'id'), COALESCE((SELECT MAX(id) FROM destination), 1), TRUE);
SELECT setval(pg_get_serial_sequence('distillery', 'id'), COALESCE((SELECT MAX(id) FROM distillery), 1), TRUE);
SELECT setval(pg_get_serial_sequence('tag', 'tag_id'), COALESCE((SELECT MAX(tag_id) FROM tag), 1), TRUE);
SELECT setval(pg_get_serial_sequence('alcohol', 'id'), COALESCE((SELECT MAX(id) FROM alcohol), 1), TRUE);
SELECT setval(pg_get_serial_sequence('site_user', 'user_id'), COALESCE((SELECT MAX(user_id) FROM site_user), 1), TRUE);
SELECT setval(pg_get_serial_sequence('review', 'review_id'), COALESCE((SELECT MAX(review_id) FROM review), 1), TRUE);
