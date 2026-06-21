-- Shared lookup data loaded by both normal development and performance profiles.

INSERT INTO category (category_id, name, description)
VALUES
    (1, '위스키', '곡물 원액을 증류한 뒤 오크통에서 숙성하는 증류주입니다.'),
    (2, '럼', '사탕수수 부산물이나 즙을 발효, 증류해 만드는 증류주입니다.'),
    (3, '보드카', '곡물이나 감자 등을 원료로 하는 무색 증류주입니다.'),
    (4, '진', '주니퍼베리와 식물성 재료 향을 입힌 증류주입니다.'),
    (5, '데킬라', '멕시코산 블루 아가베를 주원료로 하는 증류주입니다.'),
    (6, '브랜디', '과실주를 증류해 숙성하는 증류주입니다.'),
    (7, '리큐르', '증류주에 과일, 허브, 향신료, 당분 등을 더한 주류입니다.'),
    (8, '맥주', '맥아와 홉을 중심으로 발효해 만드는 양조주입니다.'),
    (9, '사케', '쌀, 누룩, 물을 발효해 만드는 일본 청주 계열 주류입니다.'),
    (10, '전통주', '지역의 원료와 제조법을 기반으로 한 전통 양조주 또는 증류주입니다.'),
    (11, '쇼추/아와모리', '고구마, 보리, 쌀, 흑당 등을 증류해 만드는 일본 증류주 계열입니다.')
ON CONFLICT (category_id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description;

INSERT INTO country (country_id, name, description)
VALUES
    (1, '일본', '사케, 쇼추, 위스키, 맥주 등 다양한 주류 생산지가 밀집한 국가입니다.'),
    (2, '한국', '전통주와 맥주, 증류주 문화가 함께 성장하고 있는 국가입니다.'),
    (3, '미국', '크래프트 맥주, 버번, 와인 등 다양한 주류 생산지가 있는 국가입니다.')
ON CONFLICT (country_id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description;

INSERT INTO city (city_id, name, description, country_id)
VALUES
    (1, '시마모토', '오사카부 미시마군의 위스키 생산지입니다.', 1),
    (2, '호쿠토', '야마나시현 북서부의 산림 지대와 위스키 생산지입니다.', 1),
    (3, '요이치', '홋카이도 서부의 위스키 증류소 방문지입니다.', 1),
    (4, '센다이', '미야기현의 도시이자 위스키 생산 거점입니다.', 1),
    (5, '치치부', '사이타마현의 산간 위스키 생산지입니다.', 1),
    (6, '미야다', '나가노현 중앙알프스 인근의 위스키 생산지입니다.', 1),
    (7, '히오키', '가고시마현 서부 해안의 위스키 생산지입니다.', 1),
    (8, '하츠카이치', '히로시마현의 위스키와 진 생산지입니다.', 1),
    (9, '나카', '이바라키현의 맥주와 사케 생산지입니다.', 1),
    (10, '교토', '후시미 사케 양조 지역을 포함하는 역사 도시입니다.', 1),
    (11, '이와쿠니', '야마구치현의 사케 생산지입니다.', 1),
    (12, '미나미우오누마', '니가타현의 쌀과 사케 생산지입니다.', 1),
    (13, '기조', '미야자키현의 쇼추 생산지입니다.', 1)
ON CONFLICT (city_id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    country_id = EXCLUDED.country_id;

INSERT INTO tag (tag_id, name, description, tag_group, sort_order, active)
VALUES
    (1, '과일', '과일 계열의 향', 'TASTING_AROMA', 1, TRUE),
    (2, '꽃', '꽃 계열의 향', 'TASTING_AROMA', 2, TRUE),
    (3, '곡물', '쌀, 보리, 밀 등 곡물 계열의 향', 'TASTING_AROMA', 3, TRUE),
    (4, '견과', '아몬드, 밤, 호두 등 견과 계열의 향', 'TASTING_AROMA', 4, TRUE),
    (5, '향신료', '계피, 후추 등 향신료 계열의 향', 'TASTING_AROMA', 5, TRUE),
    (6, '허브', '풀, 민트 등 허브 계열의 향', 'TASTING_AROMA', 6, TRUE),
    (7, '나무', '오크, 나무, 숙성감이 느껴지는 향', 'TASTING_AROMA', 7, TRUE),
    (8, '훈연', '스모키하거나 그을린 향', 'TASTING_AROMA', 8, TRUE),
    (9, '단맛', '입안에서 느껴지는 단맛', 'TASTING_PALATE', 1, TRUE),
    (10, '산미', '입안에서 느껴지는 산미', 'TASTING_PALATE', 2, TRUE),
    (11, '쓴맛', '입안에서 느껴지는 쓴맛', 'TASTING_PALATE', 3, TRUE),
    (12, '감칠맛', '입안에서 느껴지는 감칠맛', 'TASTING_PALATE', 4, TRUE),
    (13, '바디감', '무게감과 질감이 있는 맛', 'TASTING_PALATE', 5, TRUE),
    (14, '탄산감', '탄산이나 발포감이 느껴지는 맛', 'TASTING_PALATE', 6, TRUE),
    (15, '매운맛', '알코올감이나 향신료처럼 매운 느낌', 'TASTING_PALATE', 7, TRUE),
    (16, '깔끔함', '마무리가 맑고 깨끗한 맛', 'TASTING_PALATE', 8, TRUE),
    (17, '짧음', '빠르게 사라지는 여운', 'TASTING_FINISH', 1, TRUE),
    (18, '긴 여운', '오래 남는 여운', 'TASTING_FINISH', 2, TRUE),
    (19, '드라이', '단맛이 적고 건조한 여운', 'TASTING_FINISH', 3, TRUE),
    (20, '달콤함', '마지막에 남는 달콤한 여운', 'TASTING_FINISH', 4, TRUE),
    (21, '따뜻함', '목과 몸에 남는 따뜻한 느낌', 'TASTING_FINISH', 5, TRUE),
    (22, '부드러움', '거칠지 않고 둥근 여운', 'TASTING_FINISH', 6, TRUE),
    (23, '묵직함', '무게감 있게 남는 여운', 'TASTING_FINISH', 7, TRUE)
ON CONFLICT (tag_id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    tag_group = EXCLUDED.tag_group,
    sort_order = EXCLUDED.sort_order,
    active = EXCLUDED.active;

SELECT setval(pg_get_serial_sequence('category', 'category_id'), COALESCE((SELECT MAX(category_id) FROM category), 1), TRUE);
SELECT setval(pg_get_serial_sequence('country', 'country_id'), COALESCE((SELECT MAX(country_id) FROM country), 1), TRUE);
SELECT setval(pg_get_serial_sequence('city', 'city_id'), COALESCE((SELECT MAX(city_id) FROM city), 1), TRUE);
SELECT setval(pg_get_serial_sequence('tag', 'tag_id'), COALESCE((SELECT MAX(tag_id) FROM tag), 1), TRUE);

UPDATE alcohol
SET name = 'ZZZ Dummy Alcohol ' || lpad(id::text, 6, '0')
WHERE name ~ '^(Alcohol|Perf Alcohol) [0-9]+$'
   OR name ~ '^ZZZ Dummy Alcohol [0-9]+$';
