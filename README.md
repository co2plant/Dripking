# Dripking Backend

술 테마 여행 콘텐츠를 탐색하고, 관심 항목을 여행 일정으로 연결하는 Dripking의 Spring Boot 백엔드 API입니다.

이 저장소는 백엔드만 포함합니다. 프론트엔드는 같은 상위 워크스페이스의 `../Dripking_front/`에 별도 Git 저장소로 관리됩니다.

## 프로젝트 소개

Dripking은 술과 여행을 좋아하는 사용자가 술, 양조장, 여행지를 발견하고 실제 여행 계획으로 옮길 수 있도록 돕는 여행 계획 서비스입니다.

일반 여행 서비스는 장소 중심으로 움직이고, 술 관련 정보는 제품 정보와 방문지 정보가 분리되어 있는 경우가 많습니다. Dripking은 이 흐름을 하나로 묶어 사용자가 아래 과정을 자연스럽게 이어갈 수 있게 합니다.

1. 술, 양조장, 여행지를 탐색합니다.
2. 관심 있는 항목을 위시리스트에 저장합니다.
3. 위시리스트 항목을 여행 일정에 추가합니다.
4. 방문하거나 관심 있는 항목에 리뷰를 남깁니다.
5. 관리자는 콘텐츠와 리뷰, 인기 신호를 운영합니다.

MVP 1은 추천/탐색과 일정 관리 중에서 "탐색한 콘텐츠를 실제 여행 일정으로 구성하는 경험"을 우선합니다. AI 여행 코스 자동 생성, 크롤링, 소셜 로그인, 알림은 향후 확장 범위입니다.

## 서비스 화면 예시

아래 이미지는 프론트엔드 로컬 실행과 백엔드 dev seed data를 기준으로 캡처한 예시 화면입니다.

![Dripking 랜딩 화면](docs/images/dripking-landing.png)

## 백엔드 역할

이 백엔드는 Dripking 서비스의 핵심 데이터와 사용자 흐름을 API로 제공합니다.

- 술, 양조장, 여행지, 국가, 도시, 카테고리 카탈로그 관리
- 회원가입, 로그인, JWT 인증, 사용자 프로필 관리
- 위시리스트 저장과 조회
- 여행과 일정 생성, 수정, 삭제
- 술, 양조장, 여행지 리뷰와 리뷰 신고
- 관리자 콘텐츠 관리, 사용자 관리, 리뷰 검수
- 사용자 상호작용 이벤트 기반 인기 콘텐츠 추천
- Swagger/OpenAPI 기반 API 명세 제공

## 핵심 구현 포인트

| 구현 영역 | 설명 |
| --- | --- |
| 인증과 권한 | Spring Security와 JWT로 일반 사용자와 관리자 권한을 분리합니다. |
| 여행 일정 관리 | `Trip`과 `Plan`을 분리해 여행 묶음과 세부 일정을 관리합니다. |
| 위시리스트 연동 | 술, 양조장, 여행지를 위시리스트에 저장하고 일정으로 전환할 수 있는 API를 제공합니다. |
| 리뷰와 검수 | 사용자는 리뷰를 작성/신고하고, 관리자는 신고된 리뷰를 숨김/삭제/해결 처리할 수 있습니다. |
| 인기 콘텐츠 추천 | 상세 조회, 위시리스트 추가, 일정 추가 같은 상호작용을 이벤트로 저장하고 가중치 기반 인기 점수를 계산합니다. |
| 관리자 운영 | 콘텐츠, 카테고리, 사용자, 리뷰 신고, 인기 지표를 관리하는 관리자 API를 제공합니다. |
| 이미지 업로드 | 관리자 콘텐츠 이미지 업로드를 S3-compatible storage 설정으로 처리합니다. |

## 주요 도메인

Dripking의 콘텐츠 구조는 여행지를 기준으로 술과 생산지를 연결합니다.

```text
Country -> City -> Destination -> Distillery -> Alcohol
```

| 도메인 | 의미 |
| --- | --- |
| `User` | 회원, 인증, 권한, 프로필 |
| `Alcohol` | 술 카탈로그 항목 |
| `Distillery` | 양조장, 증류소 등 방문 가능한 생산지 |
| `Destination` | 술 테마 여행지 또는 지역 |
| `Trip` | 사용자의 여행 계획 묶음 |
| `Plan` | 여행 안의 일정 항목 |
| `WishlistItem` | 사용자가 저장한 관심 항목 |
| `Review` | 술, 양조장, 여행지에 대한 리뷰와 평점 |
| `InteractionEvent` | 인기 점수 계산에 사용하는 사용자 상호작용 |

## 주요 API 그룹

모든 API는 `/api` 하위에 있습니다.

| API | 역할 |
| --- | --- |
| `/api/user` | 회원가입, 로그인, 사용자 상태, 프로필, 비밀번호 변경 |
| `/api/alcohols` | 술 목록, 상세, 검색, 관리 |
| `/api/distilleries` | 양조장 목록, 상세, 검색, 관리 |
| `/api/destinations` | 여행지 목록, 상세, 검색, 관리 |
| `/api/categories` | 술 카테고리 조회와 관리 |
| `/api/countries`, `/api/cities` | 국가와 도시 메타데이터 |
| `/api/wishlist` | 사용자 위시리스트 |
| `/api/trips` | 여행 계획 생성, 조회, 수정, 삭제 |
| `/api/trips/{tripId}/plans` | 여행 세부 일정 생성, 조회, 수정, 삭제 |
| `/api/reviews` | 리뷰 조회, 작성, 수정, 삭제, 신고 |
| `/api/interaction-events` | 인기 점수용 상호작용 이벤트 기록 |
| `/api/recommendations` | 인기 여행지와 인기 술 추천 |
| `/api/admin` | 관리자 사용자/리뷰/대시보드 API |

세부 요청과 응답은 로컬 실행 후 Swagger UI에서 확인할 수 있습니다.

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.3.4 |
| Build | Gradle Wrapper 8.10.2 |
| API | Spring Web, Validation, Springdoc OpenAPI |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Local DB | H2 |
| Production DB | PostgreSQL |
| Image Storage | S3-compatible object storage |
| Test | JUnit 5, Spring Boot Test, Spring Security Test |

## 빠른 실행

로컬 개발 환경은 기본적으로 `dev` profile과 H2 인메모리 DB를 사용합니다.

```sh
cd Dripking
sh gradlew bootRun
```

실행 후 확인할 수 있는 주소:

- API base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

프론트엔드까지 함께 실행하려면 별도 터미널에서:

```sh
cd ../Dripking_front
npm install
npm run serve
```

프론트엔드의 API 주소는 `Dripking_front/.env` 또는 `.env.example`의 `VUE_APP_API_URL`이 `http://localhost:8080/api`를 가리키도록 맞춥니다.

## 검증

```sh
cd Dripking
sh gradlew test
sh gradlew build
```

프론트엔드와 계약을 함께 확인해야 하는 변경이라면 `../Dripking_front/`에서 `npm run lint`와 `npm run build`도 실행합니다.

## 문서 안내

- `docs/PRD.md`: Dripking이 어떤 문제를 풀고 어떤 사용자 흐름을 제공하는지 정리한 제품 소개 문서
- `docs/deployment.md`: Docker, 환경 변수, 운영 실행 기준
- `../agents/specs/product-spec.md`: 상위 워크스페이스의 제품 방향과 MVP 범위
- `../agents/specs/api-contract.md`: 백엔드와 프론트엔드 API 계약 기준

배포 문서는 운영 참고용입니다. 채용 또는 포트폴리오 검토자가 처음 읽을 문서는 이 README와 `docs/PRD.md`를 기준으로 정리했습니다.
