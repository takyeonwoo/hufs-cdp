# ScanPang API 명세서

> 작성일: 2026-05-17  
> 현재 앱은 `DummyData.kt` / `QiblaDataProviders.kt`의 하드코딩 값을 사용.  
> 아래 명세대로 API를 붙이면 각 파일의 TODO 주석 위치만 교체하면 됨.

---

## 공통

| 항목 | 내용 |
|---|---|
| Base URL | `https://api.scanpang.com/v1` |
| 인증 방식 | Bearer Token (`Authorization: Bearer {token}` 헤더) |
| 응답 형식 | `Content-Type: application/json` |
| 언어 파라미터 | `Accept-Language: ko` or `en` (헤더) |

### 공통 에러 응답

```json
{
  "code": "ERROR_CODE",
  "message": "에러 설명"
}
```

| HTTP 코드 | 의미 |
|---|---|
| 400 | 잘못된 요청 |
| 401 | 인증 필요 / 토큰 만료 |
| 403 | 권한 없음 |
| 404 | 리소스 없음 |
| 500 | 서버 에러 |

---

## 1. 인증 (Auth)

### 1.1 소셜 로그인

```
POST /auth/login
```
```json
// Request
{
  "provider": "kakao" | "google",
  "id_token": "OAuth ID 토큰"
}

// Response 200
{
  "access_token": "eyJ...",
  "refresh_token": "eyJ...",
  "expires_in": 3600,
  "is_new_user": true
}
```

> `is_new_user: true` 이면 클라이언트는 온보딩 플로우로 진입한다.

### 1.2 토큰 갱신

```
POST /auth/refresh
```
```json
// Request
{ "refresh_token": "eyJ..." }

// Response 200
{ "access_token": "eyJ...", "expires_in": 3600 }
```

### 1.3 로그아웃

```
POST /auth/logout
```
```
Response 204 (No Content)
```

### 1.4 회원 탈퇴

```
DELETE /auth/withdraw
```
```
Response 204 (No Content)
```

---

## 2. 사용자 (User)

### 2.1 내 프로필 조회

```
GET /users/me
```
```json
// Response 200
{
  "id": "user_abc123",
  "display_name": "Yeonwoo",
  "language": "ko" | "en",
  "value_added": "halal" | "vegan" | "general",
  "provider": "kakao" | "google",
  "created_at": "2025-01-01T00:00:00Z"
}
```

### 2.2 프로필 수정

```
PATCH /users/me
```
```json
// Request (변경할 필드만 포함)
{
  "display_name": "Yeonwoo",
  "language": "en",
  "value_added": "halal"
}

// Response 200 — 수정된 프로필 전체 반환
```

---

## 3. 장소 (Places)

### 3.1 장소 목록 조회

```
GET /places
```

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `lat` | float | ✅ | 현재 위도 |
| `lng` | float | ✅ | 현재 경도 |
| `category_key` | string | | 카테고리 필터 (아래 표 참고) |
| `radius` | int | | 반경(m), 기본 1000 |
| `value_added` | string | | `halal` \| `vegan` — 부가가치 필터 |
| `limit` | int | | 기본 20, 최대 50 |
| `offset` | int | | 페이지네이션 오프셋 |

#### category_key 목록

| category_key | 설명 |
|---|---|
| `cafe` | 카페, 베이커리 |
| `restaurant` | 음식점 (한식·양식·일식·중식 등) |
| `pharmacy` | 약국 |
| `hospital` | 병원, 의원, 치과, 한의원 |
| `bank` | 은행 |
| `atm` | ATM / 현금인출기 |
| `exchange` | 환전소 |
| `subway` | 지하철역 |
| `restroom` | 공중화장실 |
| `locker` | 물품보관함 |
| `prayer_room` | 기도실 |
| `accommodation` | 호텔, 숙박, 게스트하우스, 모텔 |
| `cultural` | 영화관, 박물관, 미술관, 공연장 |
| `tourist` | 관광지, 명소, 전망대 |
| `shopping` | 백화점, 쇼핑몰, 패션·뷰티 |
| `convenience_store` | 편의점 |

```json
// Response 200
{
  "total": 42,
  "places": [
    {
      "id": "r1",
      "name": "봉추찜닭 명동점",
      "category_key": "restaurant",
      "category_label": "식당",
      "sub_category": "",
      "distance_m": 120,
      "distance_label": "120m",
      "address": "서울 중구 명동길 26",
      "phone": "02-318-0000",
      "open_hours": "월-일 11:00-22:00",
      "is_open": true,
      "description": "명동 한복판에서 할랄 한식을 즐길 수 있는 공간입니다.",
      "tags": ["할랄 인증", "무슬림 조리사", "주류 미판매"],
      "images": ["https://cdn.scanpang.com/places/r1/1.jpg"],
      "rating": 4.5,
      "latitude": 37.5636,
      "longitude": 126.9869
    }
  ]
}
```

### 3.2 장소 상세 조회

```
GET /places/{id}
```
```json
// Response 200 — 3.1 단건과 동일한 구조
```

---

## 4. 카테고리별 추가 상세

> 각 카테고리에 고유한 필드가 있는 경우 별도 엔드포인트로 조회한다.

### 4.1 식당 상세 (`category_key: restaurant`)

```
GET /places/{id}/restaurant
```
```json
// Response 200
{
  "place": { /* 3.2와 동일 */ },
  "halal_category": "HALAL_MEAT" | "SEAFOOD" | "VEGGIE" | "SALAM_SEOUL",
  "menu_items": [
    { "name": "한우 불고기 정식", "price": "15,000원" }
  ],
  "last_order": "21:20",
  "is_moslem_chef": true,
  "no_alcohol": true
}
```

#### halal_category 정의

| 값 | 의미 |
|---|---|
| `HALAL_MEAT` | 할랄 인증 육류 사용 |
| `SEAFOOD` | 해산물 (무슬림 허용) |
| `VEGGIE` | 채식 / 비건 |
| `SALAM_SEOUL` | 살람서울 인증 |

### 4.2 카페 상세 (`category_key: cafe`)

```
GET /places/{id}/cafe
```
```json
// Response 200
{
  "place": { /* 3.2와 동일 */ },
  "representative_menus": [
    { "name": "아메리카노", "price": "4,500원" },
    { "name": "카페 라떼",  "price": "5,000원" }
  ]
}
```

### 4.3 환전소 상세 (`category_key: exchange`)

```
GET /places/{id}/exchange
```
```json
// Response 200
{
  "place": { /* 3.2와 동일 */ },
  "supported_currencies": ["USD", "EUR", "JPY", "MYR", "SAR"],
  "fee_free": true,
  "current_rates": [
    { "currency": "USD", "rate": "1,320", "flag": "🇺🇸" }
  ]
}
```

### 4.4 물품보관함 상세 (`category_key: locker`)

```
GET /places/{id}/locker
```
```json
// Response 200
{
  "place": { /* 3.2와 동일 */ },
  "tiers": [
    { "label": "소형", "price": "2,000원 / 4시간", "available": true },
    { "label": "중형", "price": "3,000원 / 4시간", "available": true },
    { "label": "대형", "price": "4,000원 / 4시간", "available": false }
  ]
}
```

---

## 5. 환율

```
GET /exchange-rates
```
```json
// Response 200
// 캐시 TTL: 30분 권장 (Cache-Control: max-age=1800)
{
  "updated_at": "2026-05-13T09:00:00Z",
  "rates": [
    { "currency": "USD", "rate": "1,320", "flag": "🇺🇸" },
    { "currency": "MYR", "rate": "285",   "flag": "🇲🇾" },
    { "currency": "SAR", "rate": "352",   "flag": "🇸🇦" },
    { "currency": "EUR", "rate": "1,430", "flag": "🇪🇺" },
    { "currency": "JPY", "rate": "8.9",   "flag": "🇯🇵" }
  ]
}
```

---

## 6. 검색

```
GET /search
```

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `q` | string | ✅ | 검색어 |
| `lat` | float | ✅ | 현재 위도 |
| `lng` | float | ✅ | 현재 경도 |
| `category_key` | string | | 카테고리 필터 |
| `value_added` | string | | `halal` \| `vegan` |
| `limit` | int | | 기본 20, 최대 50 |
| `offset` | int | | 페이지네이션 오프셋 |

```json
// Response 200
{
  "query": "환전",
  "total": 3,
  "results": [
    {
      "id": "e1",
      "name": "명동 외환센터",
      "category_key": "exchange",
      "category_label": "환전소",
      "distance_m": 180,
      "distance_label": "180m",
      "is_open": true
    }
  ]
}
```

---

## 7. 저장한 장소

### 7.1 목록 조회

```
GET /users/me/saved-places
```
```json
// Response 200
{
  "saved_places": [
    {
      "id": "r1",
      "name": "봉추찜닭 명동점",
      "category_key": "restaurant",
      "category_label": "식당",
      "distance_label": "120m",
      "tags": ["할랄 인증"],
      "saved_at": "2026-05-13T10:00:00Z"
    }
  ]
}
```

### 7.2 저장

```
PUT /users/me/saved-places/{place_id}
```
```
Response 204 (No Content)
```

### 7.3 저장 취소

```
DELETE /users/me/saved-places/{place_id}
```
```
Response 204 (No Content)
```

---

## 8. 최근 본 장소

### 8.1 목록 조회

```
GET /users/me/recently-viewed
```
```json
// Response 200
{
  "items": [
    {
      "id": "r1",
      "name": "봉추찜닭 명동점",
      "category_key": "restaurant",
      "category_label": "식당",
      "distance_label": "120m",
      "viewed_at": "2026-05-13T12:00:00Z"
    }
  ]
}
```

> 서버는 최대 20건 보관, 동일 `place_id` 재진입 시 `viewed_at` 만 갱신한다.

### 8.2 기록

```
POST /users/me/recently-viewed
```
```json
// Request
{ "place_id": "r1" }

// Response 204
```

### 8.3 단건 삭제

```
DELETE /users/me/recently-viewed/{place_id}
```
```
Response 204 (No Content)
```

### 8.4 전체 삭제

```
DELETE /users/me/recently-viewed
```
```
Response 204 (No Content)
```

---

## 9. 지하철 상세

> `GET /places/{id}`에서 `category_key: subway`일 때 아래 필드가 추가로 포함됨.  
> 코드 위치: `PlaceDetailScreen.kt` → `SubwayExitsSection`, `SubwayScheduleSection`, `SubwayFastAlightsSection`  
> 데이터 클래스: `SubwayDetail`, `SubwayExit`, `SubwayScheduleDir`, `SubwayFastAlight`

```json
// GET /places/sub1 응답 내 subway 추가 필드
{
  "subway_line": "4호선",
  "exit_count": 10,
  "exits": [
    { "exit_no": "1", "facilities": ["남산돈가스거리", "대한적십자사"] },
    { "exit_no": "6", "facilities": ["명동 거리", "눈스퀘어"] }
  ],
  "schedule_up": {
    "toward": "불암산(당고개)",
    "first": "00:01",
    "last":  "20:57"
  },
  "schedule_down": {
    "toward": "오이도",
    "first": "00:00",
    "last":  "20:50"
  },
  "fast_alights": [
    {
      "direction": "회현",
      "updown": "하행",
      "door": "10-4",
      "facility": "에스컬레이터",
      "walk_pos": "명동 B4",
      "fac_pos": "회현 방면 10-4, 충무로 방면 1-1"
    },
    {
      "direction": "충무로",
      "updown": "상행",
      "door": "1-1",
      "facility": "엘리베이터",
      "walk_pos": "명동 B3",
      "fac_pos": "당고개 방면 1-1, 오이도 방면 10-4"
    }
  ]
}
```

---

## 10. 기도 시간 & 키블라

### 10-1. 기도 시간 조회

```
GET /prayer-times?lat={lat}&lng={lng}&date={YYYY-MM-DD}
```

> `date` 생략 시 오늘 날짜. 매일 자정 이후 첫 호출 시 갱신 권장.

```json
// Response 200
{
  "date": "2026-05-17",
  "location": { "city": "Seoul", "latitude": 37.5636, "longitude": 126.9869 },
  "times": {
    "Fajr":    "05:12",
    "Dhuhr":   "12:15",
    "Asr":     "15:45",
    "Maghrib": "18:32",
    "Isha":    "20:05"
  }
}
```

**앱 연동 위치**: `QiblaDataProviders.kt` → `getPrayerTimes()`

```kotlin
// 교체 전 (하드코딩)
val schedule = buildTodaySchedule("Fajr" to "05:12", ...)

// 교체 후
val res = api.getPrayerTimes(lat, lng, today)
val schedule = buildTodaySchedule(
    "Fajr"    to res.times.Fajr,
    "Dhuhr"   to res.times.Dhuhr,
    "Asr"     to res.times.Asr,
    "Maghrib" to res.times.Maghrib,
    "Isha"    to res.times.Isha,
)
```

> 응답의 `times` 값은 `PrayerAlarmScheduler`의 AlarmManager 등록에 바로 사용됨.

---

### 10-2. 키블라 방향 조회

```
GET /qibla?lat={lat}&lng={lng}
```

```json
// Response 200
{
  "direction_degrees": 292.4,
  "mecca_distance_km": 8565
}
```

**앱 연동 위치**: `QiblaDataProviders.kt`

```kotlin
// 교체 전
fun getQiblaDirection(): Float = 292f
fun getMeccaDistanceKm(): Float = 8565f

// 교체 후 — QiblaDirectionScreen.kt에서 lat/lng 받아 호출
val res = api.getQibla(lat, lng)
```

---

## 11. AR 탐색 / 길안내

### 11-1. 주변 POI 조회 (AR 탐색용)

```
GET /ar/nearby
```

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `lat` | float | ✅ | 현재 위도 |
| `lng` | float | ✅ | 현재 경도 |
| `heading` | float | | 방위각 (0~360), AR 뷰 방향 필터링용 |
| `radius` | int | | 반경(m), 기본 200 |
| `value_added` | string | | `halal` \| `vegan` |

```json
// Response 200
{
  "pois": [
    {
      "id": "s1",
      "name": "눈스퀘어",
      "category_key": "shopping",
      "category_label": "쇼핑",
      "latitude": 37.5636,
      "longitude": 126.9869,
      "distance_m": 15,
      "is_open": true
    }
  ]
}
```

### 11-2. AR 빌딩 POI 목록

```
GET /ar/buildings?lat={lat}&lng={lng}&radius={meters}
```

```json
// Response 200
{
  "buildings": [
    {
      "ufid": "noon_square",
      "name": "눈스퀘어",
      "category": "쇼핑",
      "distance_m": 10,
      "latitude": 37.5630,
      "longitude": 126.9844,
      "floors": [
        {
          "floor": "B1",
          "store_count": 15,
          "categories": ["식당", "쇼핑"],
          "stores": [
            { "name": "무궁화식당", "category": "한식", "is_halal": false, "has_detail": true }
          ]
        }
      ]
    }
  ]
}
```

> `ArBuildingPoi`, `ArBuildingFloor`, `ArBuildingStore` 데이터 클래스와 매핑.  
> `has_detail: true`인 매장은 `GET /ar/stores/{name}` 호출 가능.

---

### 11-3. AR 매장 상세

```
GET /ar/stores/{name}?building_ufid={ufid}
```

> `StoreDetail` 데이터 클래스 전체 필드를 그대로 반환.  
> 코드 위치: `ArPoiFloatingPanel.kt`, `PlaceDetailScreen.kt`

```json
// Response 200
{
  "name": "알리바바 케밥",
  "name_en": "Ali Baba Kebab",
  "cuisine_label": "할랄 · 케밥",
  "distance_m": 15,
  "is_open": true,
  "open_hours": "오늘 11:00–22:00",
  "last_order": "라스트오더 21:30",
  "address": "서울 중구 명동8길 8-3",
  "phone": "02-318-4221",
  "floor": "1F",
  "website": "alibaba-kebab.com",
  "description": "...",
  "is_halal": true,
  "halal_category": "HALAL_MEAT",
  "show_trust_badges": true,
  "latitude": 37.5636,
  "longitude": 126.9869,
  "menus": [
    { "name": "치킨 케밥", "name_en": "Chicken Kebab", "price": "₩12,000" }
  ],
  "image_urls": ["https://cdn.scanpang.com/stores/kebab/1.jpg"],
  "exchange_rates": [],
  "convenience_services": null,
  "departments": null,
  "toilet_male": null,
  "toilet_female": null,
  "facility_tags": null,
  "safety_tags": null,
  "subway_schedule_up": null,
  "subway_schedule_down": null
}
```

---

### 11-4. AR 길안내 경로 요청

```
POST /ar/navigation/route
```
```json
// Request
{
  "origin": { "lat": 37.5636, "lng": 126.9869 },
  "destination_place_id": "s1"
}

// Response 200
{
  "destination": { "name": "눈스퀘어", "lat": 37.5636, "lng": 126.9869 },
  "total_distance_m": 200,
  "estimated_seconds": 180,
  "steps": [
    {
      "direction": "straight" | "left" | "right" | "u_turn",
      "distance_m": 60,
      "distance_label": "60m",
      "message": "정면으로 60m 직진하세요"
    }
  ]
}
```

> 코드 위치: `ArNavigationMapScreen.kt`

---

## 12. 화면 ↔ 코드 파일 ↔ API 매핑

| 화면 (Figma) | 코드 파일 | 주요 API |
|---|---|---|
| 스플래시 | `SplashScreen.kt` | `GET /users/me` |
| 로그인 | `LoginScreen.kt` | `POST /auth/login` |
| 약관 동의 | `TermsAgreementScreen.kt` | — |
| 온보딩 언어 | `OnboardingLanguageScreen.kt` | `PATCH /users/me` |
| 온보딩 이름 | `OnboardingNameScreen.kt` | `PATCH /users/me` |
| 온보딩 선호도 | `OnboardingPreferenceScreen.kt` | `PATCH /users/me` |
| 홈 | `HomeScreen.kt` | `GET /prayer-times`, `GET /qibla`, `GET /places` |
| 검색 | `SearchDefaultScreen.kt` | `GET /search` |
| 주변 할랄 식당 | `NearbyHalalRestaurantsScreen.kt` | `GET /places?category_key=restaurant` |
| 주변 기도실 | `NearbyPrayerRoomsScreen.kt` | `GET /places?category_key=prayer_room` |
| 장소 상세 | `PlaceDetailScreen.kt` | `GET /places/{category_key}/{id}` |
| AR 탐색 | `ArExploreScreen.kt` | `GET /ar/nearby`, `GET /ar/buildings` |
| AR 내비게이션 | `ArNavigationMapScreen.kt` | `GET /ar/stores/{name}`, `POST /ar/navigation/route` |
| 키블라 방향 | `QiblaDirectionScreen.kt` | `GET /qibla`, `GET /prayer-times` |
| 저장된 장소 | `SavedPlacesScreen.kt` | `GET /users/me/saved-places` |
| 최근 본 장소 | `RecentlyViewedListScreen.kt` | `GET /users/me/recently-viewed` |
| 내 정보 | `ProfileScreen.kt` | `GET /users/me` |
| 알림 설정 | `NotificationSettingsScreen.kt` | — (로컬 SharedPrefs) |
| 언어 설정 | `LanguageSettingsScreen.kt` | `PATCH /users/me` |
| 부가가치 설정 | `ValueAddedSettingsScreen.kt` | `PATCH /users/me` |
| 회원탈퇴 | `WithdrawalScreen.kt` | `DELETE /auth/withdraw` |

---

## 에러 코드

| 코드 | HTTP 상태 | 설명 |
|---|---|---|
| `UNAUTHORIZED` | 401 | 토큰 없음 또는 만료 |
| `FORBIDDEN` | 403 | 권한 없음 |
| `PLACE_NOT_FOUND` | 404 | 장소 없음 |
| `INVALID_CATEGORY` | 400 | 잘못된 category_key |
| `MISSING_LOCATION` | 400 | lat / lng 누락 |
| `ROUTE_NOT_FOUND` | 404 | 경로 계산 불가 |
| `INTERNAL_ERROR` | 500 | 서버 내부 오류 |

---

## 구현 시 참고 사항

1. **저장한 장소 / 최근 본 장소** — 현재 앱이 로컬(SharedPreferences)로 동작 중이므로, 서버 연동 시 기존 로컬 데이터 마이그레이션 로직 필요.

2. **distance_label 포맷** — 서버가 `distance_m` 기준으로 계산해 내려주거나, 앱에서 `distance_m`을 받아 직접 포맷팅하는 방향 중 팀 합의 필요.

3. **accommodation / cultural** — 현재 앱 UI 미구현 상태. 백엔드 엔드포인트는 `/places?category_key=accommodation` 형태로 동일하게 준비하고, 앱 UI 구현 완료 후 연동.

4. **이미지 URL** — `images` 필드는 CDN 절대경로로 내려준다. 앱은 Coil로 로드.

5. **토큰 만료 처리** — 401 응답 수신 시 앱은 `/auth/refresh`로 갱신 시도 후 재요청, 갱신도 실패하면 로그인 화면으로 이동.
