# ScanPang 프론트(Android) 연동용 API 명세 가이드

이 문서는 **현재 저장소의 Android 앱 코드(`versionName` 1.0, `com.scanpang.app`)**를 기준으로, 백엔드·외부 API와 맞추면 되는 **계약(제안 스펙)**과 **앱 쪽 데이터 모델**을 정리합니다. 경로·클래스명은 실제 코드와 일치하도록 갱신했습니다.

---

## 1. 앱 네트워크 현황 (事實)

| 항목 | 상태 |
|------|------|
| HTTP 클라이언트 | `app/build.gradle.kts` 기준 **Retrofit/OkHttp 미사용** — 원격 API 호출 코드 없음 |
| 위치 | `play-services-location`만 의존성에 포함 (실시간 좌표는 클라이언트) |
| 이미지 | Coil로 URL 로딩 (`RestaurantDetailScreen` 등). 상당수 URL은 `ScanPangFigmaAssets`에 고정 |

즉, 아래 API는 **아직 구현되지 않은 백엔드와의 합의용 초안**이며, 연동 시 Gradle에 HTTP 스택 추가 및 Repository/UseCase 도입이 전제입니다.

---

## 2. 연동이 필요한 화면·데이터 (코드 기준)

| 구분 | 현재 구현 요약 | 서버/외부 연동 시 기대 |
|------|----------------|------------------------|
| 검색 결과 | `SearchResultsScreen` — `resultItems` 고정, 상세로 이동 시 **항상 동일 경로** | `q`·좌표 기반 검색 API, 항목마다 `place_id`로 상세 네비게이션 |
| 주변 할랄 | `NearbyHalalRestaurantsScreen` — `allPlaces` 고정, 칩으로 **클라이언트 필터** | 근처 목록 API (필요 시 서버에서 카테고리 필터) |
| 주변 기도실 | `NearbyPrayerRoomsScreen` — 목록·칩 고정 | 근처 기도실 API, 필터(`전체`/`거리순`/`남녀 분리` 등)와 쿼리 매핑 |
| 식당 상세 | `RestaurantDetailScreen` — `DetailPlaceId` 등 상수 + `ScanPangFigmaAssets` 갤러리 | `GET /places/{id}` 형태로 치환 |
| 기도실 상세 | `PrayerRoomDetailScreen` — 플레이스홀더 히어로 + 고정 문구 | 동일하게 `place_id` 또는 `prayer_room_id` 상세 API |
| 저장 | `SavedPlacesStore` — SharedPreferences + JSON (`SavedPlaceEntry`) | 로그인 시 서버 동기화 권장 |
| 최근 검색 | `SearchHistoryPreferences` — 문자열 배열만 (최대 30건) | 로컬 유지 또는 `/me/search-history` 선택 |
| 온보딩·프로필 표시 | `OnboardingPreferences` — 표시명·언어·여행 성향 | 계정 서버와 맞추면 `ProfileScreen`·홈 인사에 반영 |
| 홈 위치 문구 | `HomeScreen` — **「현재 위치: 명동역 6번 출구 근처」** 고정 | 역지오코딩(클라 또는 서버) 또는 `/me/home` |
| 키블라·기도시간 | `qibla/QiblaDataProviders.kt` — `getPrayerTimes()`, `getQiblaDirection()`, `getMeccaDistanceKm()` 고정값 | 외부 기도시간 API 또는 자체 래핑 API |
| AR 탐색 | `ArExploreScreen` — 필터 칩·검색 히트·POI 핀·채팅 시드 **모두 로컬**; 응답은 `DummyAgentService` | POI/검색/메타 API + 에이전트 API |
| AR 내비 | `ArNavigationMapScreen` — 턴 안내·POI 이름·하단 AI 문구 고정 | 내비 세션/경로 스텝 API (또는 지도 SDK + 서버) |

**센서·카메라**는 API가 아니라 OS(CameraX, `SensorManager`, Fused Location)를 그대로 사용하고, 서버에는 **좌표·장소 ID·세션 ID**만 주고받는 구성이 일반적입니다.

---

## 3. 앱 모델 ↔ JSON 매핑 (백엔드 합의용)

### 3.1 목록·검색 카드 (`SearchResultPlaceCard`)

Kotlin: `components/SearchResultPlaceCard.kt` — `SearchResultBadgeKind`, `SearchResultTrustTag`.

| UI 필드 | 제안 JSON | 비고 |
|---------|-----------|------|
| 제목 | `name` | |
| 뱃지 종류 | `badge_kind` | 아래 enum과 **동일 문자열** 권장 |
| 뱃지 표시문구 | `badge_label` | 예: `HALAL MEAT` |
| 요리/메타 한 줄 | `cuisine_label` | |
| 거리 | `distance_m` **또는** `distance_label` | 앱은 현재 `"120m"` 형태 문자열 사용. 숫자만 주면 클라에서 포맷 가능 |
| 영업 중 | `is_open` | |
| 신뢰 태그 | `trust_tags[]` | 아래 스키마 |

**`badge_kind` (앱 enum과 매핑)**

| 앱 (`SearchResultBadgeKind`) | 제안 값 |
|------------------------------|---------|
| `HalalMeat` | `HALAL_MEAT` |
| `Seafood` | `SEAFOOD` |
| `Veggie` | `VEGGIE` |
| `SalamSeoul` | `SALAM_SEOUL` |

**`trust_tags[]` (서버는 문자열만 주고 아이콘은 클라 매핑 권장)**

```json
{ "label": "할랄 인증", "kind": "verified" }
```

| `kind` | 앱에서 매핑 예시 (Material Icons) |
|--------|-----------------------------------|
| `verified` | `Icons.Rounded.Verified` |
| `star` | `Icons.Rounded.Star` |
| `restaurant` | `Icons.Rounded.Restaurant` |
| `no_drinks` | `Icons.Rounded.NoDrinks` |
| (생략/기타) | 기본 아이콘 또는 `label`만 표시 |

`SearchResultsScreen`은 현재 `Restaurant`·`NoDrinks` 조합을 쓰고, `NearbyHalalRestaurantsScreen`은 `Verified`·`Star`를 씁니다. API는 **`kind`로 통일**하는 편이 안전합니다.

### 3.2 저장소 (`SavedPlacesStore` / `SavedPlaceEntry`)

파일: `data/SavedPlacesStore.kt`.

로컬 JSON 키: `id`, `name`, `category`, `distanceLine` (구버전 `distance` 호환), `tags` (문자열 배열), `target`, `savedOrder`.

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | string | 상세·북마크 공통 키 |
| `name` | string | |
| `category` | string | 카드/상세에 표시되는 한 줄 분류 |
| `distanceLine` | string | 예: `명동 · 도보 2분` |
| `tags` | string[] | |
| `target` | string | `Restaurant` \| `PrayerRoom` (Kotlin enum 이름과 동일하게 저장 중) |
| `savedOrder` | long | 정렬용 타임스탬프(ms) |

서버 동기화 시 `target`을 `RESTAURANT` / `PRAYER_ROOM` 등으로 바꾸면 앱 매퍼에서 변환하면 됩니다.

### 3.3 식당 상세 (현재 상수 기준)

`RestaurantDetailScreen`: `DetailPlaceId`, `DetailPlaceName`, `DetailPlaceCategory`, `DetailPlaceDistanceLine`, `DetailPlaceTags`, 갤러리 `ScanPangFigmaAssets.RestaurantDetailGallery`.

제안 응답 필드 (기존 초안 유지 + 앱 필드명 병기):

- `id`, `name`, `category`, `distance_line`, `meta_line` (선택), `tags[]`
- `hero_images[]` (HTTPS 정적 URL, 만료 없는 CDN 권장)
- `address`, `phone`, `hours_label`, `is_open`, `intro`
- `menus[]`: `{ "name", "price" }` (표시는 문자열 그대로도 가능)

### 3.4 키블라 (`QiblaDataProviders.kt`)

앱 데이터 클래스 `PrayerTimes`:

- `next_prayer_name` (앱 예: `Dhuhr`)
- `next_prayer_time` (로컬 시각 문자열, 예: `12:15`)
- `remaining_label` (사람이 읽는 문구, 예: `2시간 34분 남음`)

추가로 화면에서 쓰기 쉬운 값:

- `qibla_bearing_deg` (북 0°, 시계방향)
- `distance_to_kaaba_km`

### 3.5 AR 탐색 필터 카테고리 (서버 코드화 시)

`components/ar/ArExploreOverlays.kt` — `arExploreCategoryChipSpecs()` 라벨 (한국어):

쇼핑, 편의점, 식당, 카페, 환전소, 은행, ATM, 병원, 지하철역, 화장실, 물품보관함, 약국

API에서는 `category_code` (영문 슬러그) + `label_ko` 형태를 권장합니다.

### 3.6 AR 에이전트 (현재 코드)

- 인터페이스: `ar/AgentService.kt` — `sendMessage(String)`, `sendVoice(ByteArray)` (후자는 미연결 시나리오 대비)
- 구현: `DummyAgentService` — 짧은 딜레이 후 고정 문구
- STT 경로: `ar/VoiceAgent.kt` — `sendVoiceMessage`가 결국 `sendMessage`로 위임

연동 시 `AgentService` 구현체만 HTTP(또는 SSE)로 교체하면 `ArExploreScreen`의 `onSend` / STT 콜백을 유지하기 쉽습니다.

---

## 4. 공통 규칙 (제안)

- **Base URL**: 환경별 `https://api.scanpang.example/v1` 등 (실제 값은 배포 설정으로 분리)
- **인증**  
  - `Authorization: Bearer <access_token>`  
  - 비로그인: 검색/목록만, `/me/*` 생략
- **에러**  
  - `{ "code": "STRING", "message": "사용자용 메시지" }`
- **페이지네이션**  
  - `page`/`size` 또는 `cursor`/`limit`
- **위치**  
  - `lat`, `lng` (WGS84). 할랄·기도실·AR POI 공통

**요청 헤더 (권장)**

| 헤더 | 설명 |
|------|------|
| `Accept-Language` | `OnboardingPreferences`의 언어(`ko`, `en`, `ms`, `ar`)와 맞추면 응답 로컬라이즈에 유리 |

---

## 5. 도메인별 API 초안

### 5.1 검색

| Method | Path | 설명 |
|--------|------|------|
| GET | `/search/places` | 키워드·위치 기반 검색 |

**Query (예시)**  
`q`, `lat`, `lng`, `radius_m`, `page`, `size`

**응답 `items[]`**  
3.1 스키마. **`id` 필수** — 상세·북마크·딥링크에 사용.

---

### 5.2 주변 할랄 식당

| Method | Path | 설명 |
|--------|------|------|
| GET | `/places/halal/nearby` | 주변 할랄 식당 |

**Query**  
`lat`, `lng`, `radius_m`, `category` (`HALAL_MEAT` … 앱 칩과 동일)

**응답**  
5.1과 동일 `items[]` 재사용 가능.

---

### 5.3 주변 기도실

| Method | Path | 설명 |
|--------|------|------|
| GET | `/places/prayer-rooms/nearby` | 주변 기도실 |

**Query**  
`lat`, `lng`, `radius_m`, `sort` (`distance` 등), `filter` (`gender_separated` 등 UI 칩과 매핑)

**응답**  
`id`, `name`, `distance_m` 또는 `distance_label`, `address`, `tags[]`, `is_open`

---

### 5.4 장소 상세

| Method | Path | 설명 |
|--------|------|------|
| GET | `/places/{placeId}` | 식당·상점 등 |
| GET | `/prayer-rooms/{roomId}` | 기도실 전용 분리 시 (또는 `places` 단일 모델 + `type` 필드) |

스키마는 3.3 참고.

---

### 5.5 저장(북마크) 동기화

| Method | Path | 설명 |
|--------|------|------|
| GET | `/me/saved-places` | 목록 |
| PUT | `/me/saved-places` | 전체 치환 (MVP) |
| POST | `/me/saved-places` | 단건 추가 |
| DELETE | `/me/saved-places/{placeId}` | 삭제 |

페이로드는 3.2와 호환되게 설계하고, 서버 전용 필드 `saved_at` 등을 추가할 수 있습니다.

---

### 5.6 최근 검색

- **옵션 A**: `SearchHistoryPreferences` 유지 (API 없음)  
- **옵션 B**: `GET/POST/DELETE /me/search-history` 로 기기 간 동기화

---

### 5.7 홈 / 프로필

| Method | Path | 설명 |
|--------|------|------|
| GET | `/me/home` | 인사·추천 카피·근처 요약 (선택) |
| GET/PATCH | `/me/profile` | 표시명, 선호 언어, 여행 성향 등 (`OnboardingPreferences` 대체·동기화) |

홈의 위치 한 줄은 **클라 역지오코딩만으로도** 구현 가능합니다.

---

### 5.8 키블라 · 기도시간 · 메카 거리

- 클라 단독 계산 또는 `GET /prayer/summary?lat=&lng=&date=` 형태로 묶음 응답  
- 외부: Aladhan 등 — 백엔드 프록시 권장 (API 키·쿼터 은닉)

예시 응답 필드: `next_prayer`, `qibla_bearing_deg`, `distance_to_kaaba_km` (3.4와 맞출 것)

---

### 5.9 AR 탐색 (POI · 검색 · 메타 · 채팅)

| 구분 | 제안 |
|------|------|
| 필터 메타 | `GET /ar/explore/meta` — 카테고리 코드·라벨·정렬 키 (UI는 3.5 라벨과 정합) |
| 주변 POI | `GET /ar/pois?lat=&lng=&radius_m=&categories=` |
| POI 상세 | `GET /ar/pois/{poiId}/detail` — 건물/층/AI 탭 (`ArPoiTabBuilding`, `ArPoiTabFloors`, `ArPoiTabAi`에 대응하는 블록) |
| 에이전트 | `POST /ar/agent/chat` — `{ "session_id"?, "message", "context": { "lat", "lng", "locale" } }` → `{ "reply", "session_id" }` (스트리밍은 선택) |

음성 바이너리를 직접 올릴 경우 `POST /ar/agent/voice` (multipart) 등 별도 엔드포인트를 두고 `AgentService.sendVoice`와 연결합니다.

---

### 5.10 AR 길안내

| Method | Path | 설명 |
|--------|------|------|
| GET | `/navigation/sessions/{id}` 또는 `/navigation/active` | 현재 턴·거리·목적지 (`ArNavActionCardCluster`, `ArNavDestinationPill`에 매핑) |
| GET | `/navigation/pois` | 지도/AR 마커 |
| WS (선택) | `/navigation/stream` | 실시간 경로 업데이트 |

경로 계산은 Directions/OSRM 등과 연동한 뒤 **스텝 배열**만 내려주는 패턴이 흔합니다.

---

## 6. 정적 이미지

`ui/ScanPangFigmaAssets.kt`의 URL은 운영에서 **만료·CORS 이슈**가 있을 수 있으므로, 상세 API의 `hero_images[]` 등 **안정적인 CDN URL**로 대체하는 것을 권장합니다.

---

## 7. 우선순위 (MVP → 확장)

1. **MVP**  
   - `GET /search/places`, `GET /places/{id}`, `GET /places/halal/nearby`  
   - (선택) `GET/POST/DELETE /me/saved-places`
2. **2단계**  
   - 기도실 목록/상세, 홈/프로필 또는 역지오코딩
3. **3단계**  
   - AR POI + `POST /ar/agent/chat`, 내비 세션 API

---

## 8. 연동 시 수정·추가할 Android 위치

| 영역 | 패키지/파일 |
|------|-------------|
| Gradle HTTP 스택 | `app/build.gradle.kts` |
| 검색 결과 | `screens/SearchResultsScreen.kt` |
| 주변 할랄 | `screens/NearbyHalalRestaurantsScreen.kt` |
| 주변 기도실 | `screens/NearbyPrayerRoomsScreen.kt`, `screens/PrayerRoomDetailScreen.kt` |
| 식당 상세·북마크 | `screens/RestaurantDetailScreen.kt`, `data/SavedPlacesStore.kt` |
| 최근 검색 | `data/SearchHistoryPreferences.kt`, `screens/SearchDefaultScreen.kt` |
| 온보딩·프로필 | `data/OnboardingPreferences.kt`, `screens/ProfileScreen.kt` |
| 키블라 더미 | `qibla/QiblaDataProviders.kt` |
| AR 채팅·STT | `ar/AgentService.kt`, `ar/VoiceAgent.kt`, `screens/ar/ArExploreScreen.kt` |
| AR POI UI | `components/ar/ArPoiFloatingPanel.kt`, `components/ar/ArExploreOverlays.kt`, `screens/ar/ArNavigationMapScreen.kt` |
| 홈 문구 | `screens/HomeScreen.kt` |

---

## 9. 확정 후 권장 작업

팀에서 필드명·인증·페이지네이션을 확정하면 **OpenAPI 3** 또는 Postman Collection으로 옮기고, Android는 생성 클라이언트(Kotlin) 또는 수동 DTO를 그 스펙에 맞추면 됩니다.

---

*문서 갱신 기준: 프로젝트 내 Kotlin 소스 및 `app/build.gradle.kts` (네트워크 라이브러리 부재 포함).*
