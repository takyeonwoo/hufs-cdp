# ScanPang API 명세서

**Base URL:** `https://api.scanpang.com/v1`  
**인증:** 모든 요청 헤더에 `Authorization: Bearer {access_token}` 포함  
**Content-Type:** `application/json`

---

## 공통 응답 형식

```json
// 성공
{ "success": true, "data": { ... } }

// 실패
{ "success": false, "error": { "code": "PLACE_NOT_FOUND", "message": "..." } }
```

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

## 9. AR 탐색 / 길안내

### 9.1 주변 POI 조회 (AR 탐색용)

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

### 9.2 AR 길안내 경로 요청

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
  "destination": {
    "name": "눈스퀘어",
    "lat": 37.5636,
    "lng": 126.9869
  },
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
