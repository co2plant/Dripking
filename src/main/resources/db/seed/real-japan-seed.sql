-- Curated starter catalog for Japanese alcohol travel data.
-- Keep this file small and reviewable. Do not add synthetic performance rows here.
-- Descriptions are original summaries written for Dripking seed data.

INSERT INTO destination (id, name, description, img_url, latitude, longitude, city_id, category_id, rating, img_object_key)
VALUES
    (1, '야마자키', '오사카와 교토 경계에 있는 일본 위스키 여행 거점입니다.', NULL, 34.8928, 135.6745, 1, 1, 0, NULL),
    (2, '하쿠슈', '야마나시현 산림 지대의 맑은 물과 숲을 배경으로 한 위스키 생산지입니다.', NULL, 35.8265, 138.3020, 2, 1, 0, NULL),
    (3, '요이치', '홋카이도 서부의 해안 기후를 배경으로 한 일본 위스키 생산지입니다.', NULL, 43.1907, 140.7944, 3, 1, 0, NULL),
    (4, '미야기쿄', '센다이 인근 계곡 지형에 자리한 일본 위스키 생산지입니다.', NULL, 38.3120, 140.6500, 4, 1, 0, NULL),
    (5, '치치부', '사이타마현 산간 지역의 소규모 위스키 생산지입니다.', NULL, 35.9920, 139.0840, 5, 1, 0, NULL),
    (6, '신슈', '나가노현 중앙알프스 인근의 고지대 위스키 생산지입니다.', NULL, 35.7630, 137.9440, 6, 1, 0, NULL),
    (7, '가노스케', '가고시마현 서부 해안의 기후를 살린 위스키 생산지입니다.', NULL, 31.7210, 130.2810, 7, 1, 0, NULL),
    (8, '사쿠라오', '히로시마현 세토내해 인근의 위스키와 진 생산지입니다.', NULL, 34.3510, 132.3310, 8, 1, 0, NULL),
    (9, '고노스', '이바라키현의 맥주와 사케 생산 문화가 만나는 지역입니다.', NULL, 36.4570, 140.5000, 9, 8, 0, NULL),
    (10, '후시미', '교토의 대표적인 사케 양조 지역입니다.', NULL, 34.9320, 135.7620, 10, 9, 0, NULL),
    (11, '이와쿠니', '야마구치현의 프리미엄 사케 생산지입니다.', NULL, 34.1660, 132.2190, 11, 9, 0, NULL),
    (12, '우오누마', '니가타현의 쌀 산지와 사케 생산지가 만나는 지역입니다.', NULL, 37.0650, 138.8760, 12, 9, 0, NULL),
    (13, '기조', '미야자키현의 쇼추 생산지입니다.', NULL, 32.1100, 131.4870, 13, 11, 0, NULL)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    img_url = EXCLUDED.img_url,
    latitude = EXCLUDED.latitude,
    longitude = EXCLUDED.longitude,
    city_id = EXCLUDED.city_id,
    category_id = EXCLUDED.category_id,
    rating = EXCLUDED.rating,
    img_object_key = EXCLUDED.img_object_key;

INSERT INTO distillery (id, name, address, description, img_url, destination_id, latitude, longitude, rating, img_object_key)
VALUES
    (1, 'Yamazaki Distillery', '5-2-1 Yamazaki, Shimamoto, Mishima District, Osaka, Japan', '일본 상업 위스키 역사의 출발점으로 알려진 산토리 증류소입니다.', NULL, 1, 34.8928, 135.6745, 0, NULL),
    (2, 'Hakushu Distillery', '2913-1 Torihara, Hakushu-cho, Hokuto, Yamanashi, Japan', '숲과 물을 강조하는 산토리의 싱글몰트 위스키 생산지입니다.', NULL, 2, 35.8265, 138.3020, 0, NULL),
    (3, 'Yoichi Distillery', '7-6 Kurokawacho, Yoichi, Hokkaido, Japan', '닛카 위스키의 홋카이도 증류소입니다.', NULL, 3, 43.1907, 140.7944, 0, NULL),
    (4, 'Miyagikyo Distillery', '1 Nikka, Aoba-ku, Sendai, Miyagi, Japan', '닛카 위스키의 미야기현 증류소입니다.', NULL, 4, 38.3120, 140.6500, 0, NULL),
    (5, 'Chichibu Distillery', '49 Midorigaoka, Chichibu, Saitama, Japan', '이치로즈 몰트로 알려진 소규모 위스키 생산지입니다.', NULL, 5, 35.9920, 139.0840, 0, NULL),
    (6, 'Mars Shinshu Distillery', '4752-31 Miyada, Kamiina District, Nagano, Japan', '중앙알프스 인근에 있는 혼보주조의 위스키 생산지입니다.', NULL, 6, 35.7630, 137.9440, 0, NULL),
    (7, 'Kanosuke Distillery', 'Hioki, Kagoshima, Japan', '가고시마 해안가에 자리한 일본 위스키 생산지입니다.', NULL, 7, 31.7210, 130.2810, 0, NULL),
    (8, 'Sakurao Distillery', 'Hatsukaichi, Hiroshima, Japan', '사쿠라오 브랜드의 위스키와 진을 생산하는 증류소입니다.', NULL, 8, 34.3510, 132.3310, 0, NULL),
    (9, 'Kiuchi Brewery', 'Naka, Ibaraki, Japan', '히타치노 네스트 맥주와 일본식 양조 문화를 함께 다루는 양조장입니다.', NULL, 9, 36.4570, 140.5000, 0, NULL),
    (10, 'Gekkeikan Okura Sake Museum', 'Fushimi-ku, Kyoto, Japan', '후시미 사케 문화와 월계관 양조 역사를 볼 수 있는 거점입니다.', NULL, 10, 34.9320, 135.7620, 0, NULL),
    (11, 'Asahi Shuzo', 'Iwakuni, Yamaguchi, Japan', '닷사이 브랜드로 알려진 야마구치현 사케 양조장입니다.', NULL, 11, 34.1660, 132.2190, 0, NULL),
    (12, 'Hakkaisan Brewery', 'Minamiuonuma, Niigata, Japan', '니가타 쌀 산지의 사케 생산지입니다.', NULL, 12, 37.0650, 138.8760, 0, NULL),
    (13, 'Kuroki Honten', 'Kijo, Miyazaki, Japan', '미야자키현의 보리와 고구마 쇼추 생산지입니다.', NULL, 13, 32.1100, 131.4870, 0, NULL)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    address = EXCLUDED.address,
    description = EXCLUDED.description,
    img_url = EXCLUDED.img_url,
    destination_id = EXCLUDED.destination_id,
    latitude = EXCLUDED.latitude,
    longitude = EXCLUDED.longitude,
    rating = EXCLUDED.rating,
    img_object_key = EXCLUDED.img_object_key;

INSERT INTO alcohol (id, name, strength, size, description, distillery_id, category_id, img_url, rating, datetime, stated_age, img_object_key)
VALUES
    (1, 'Yamazaki Distiller''s Reserve', 43, 700, '야마자키 증류소의 기본 싱글몰트 라인입니다.', 1, 1, NULL, 0, NULL, NULL, NULL),
    (2, 'Yamazaki 12 Year Old', 43, 700, '12년 숙성 표기를 가진 야마자키 싱글몰트 위스키입니다.', 1, 1, NULL, 0, NULL, '12', NULL),
    (3, 'Hakushu Distiller''s Reserve', 43, 700, '하쿠슈 증류소의 허브와 산림 이미지를 가진 싱글몰트 라인입니다.', 2, 1, NULL, 0, NULL, NULL, NULL),
    (4, 'Hakushu 12 Year Old', 43, 700, '12년 숙성 표기를 가진 하쿠슈 싱글몰트 위스키입니다.', 2, 1, NULL, 0, NULL, '12', NULL),
    (5, 'Yoichi Single Malt', 45, 700, '요이치 증류소의 싱글몰트 위스키입니다.', 3, 1, NULL, 0, NULL, NULL, NULL),
    (6, 'Miyagikyo Single Malt', 45, 700, '미야기쿄 증류소의 싱글몰트 위스키입니다.', 4, 1, NULL, 0, NULL, NULL, NULL),
    (7, 'Ichiro''s Malt & Grain', 46, 700, '치치부 증류소와 벤처 위스키 계열을 대표하는 블렌디드 위스키 라인입니다.', 5, 1, NULL, 0, NULL, NULL, NULL),
    (8, 'Mars Komagatake Single Malt', 48, 700, '신슈 증류소의 싱글몰트 위스키 라인입니다.', 6, 1, NULL, 0, NULL, NULL, NULL),
    (9, 'Kanosuke Single Malt', 48, 700, '가노스케 증류소의 싱글몰트 위스키입니다.', 7, 1, NULL, 0, NULL, NULL, NULL),
    (10, 'Sakurao Single Malt', 43, 700, '사쿠라오 증류소의 싱글몰트 위스키입니다.', 8, 1, NULL, 0, NULL, NULL, NULL),
    (11, 'Hitachino Nest White Ale', 5.5, 330, '기우치 브루어리의 대표적인 화이트 에일입니다.', 9, 8, NULL, 0, NULL, NULL, NULL),
    (12, 'Gekkeikan Traditional', 15.6, 720, '후시미 사케 문화를 대표하는 월계관 계열 사케입니다.', 10, 9, NULL, 0, NULL, NULL, NULL),
    (13, 'Dassai 45', 16, 720, '정미보합 45% 계열로 알려진 닷사이 사케입니다.', 11, 9, NULL, 0, NULL, NULL, NULL),
    (14, 'Hakkaisan Tokubetsu Honjozo', 15.5, 720, '니가타 쌀 산지 이미지를 가진 핫카이산 사케 라인입니다.', 12, 9, NULL, 0, NULL, NULL, NULL),
    (15, 'Nakanaka', 25, 720, '구로키혼텐의 보리 쇼추 라인입니다.', 13, 11, NULL, 0, NULL, NULL, NULL)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    strength = EXCLUDED.strength,
    size = EXCLUDED.size,
    description = EXCLUDED.description,
    distillery_id = EXCLUDED.distillery_id,
    category_id = EXCLUDED.category_id,
    img_url = EXCLUDED.img_url,
    rating = EXCLUDED.rating,
    datetime = EXCLUDED.datetime,
    stated_age = EXCLUDED.stated_age,
    img_object_key = EXCLUDED.img_object_key;

SELECT setval(pg_get_serial_sequence('destination', 'id'), COALESCE((SELECT MAX(id) FROM destination), 1), TRUE);
SELECT setval(pg_get_serial_sequence('distillery', 'id'), COALESCE((SELECT MAX(id) FROM distillery), 1), TRUE);
SELECT setval(pg_get_serial_sequence('alcohol', 'id'), COALESCE((SELECT MAX(id) FROM alcohol), 1), TRUE);
