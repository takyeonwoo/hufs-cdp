package com.scanpang.app.data

import com.scanpang.app.R

data class Place(
    val id: String,
    val name: String,
    val category: String,
    val subCategory: String = "",
    val categoryKey: String = "",
    val distance: String,
    val address: String,
    val phone: String = "",
    val openHours: String = "",
    val isOpen: Boolean = true,
    val description: String = "",
    val tags: List<String> = emptyList(),
    val images: List<Int> = emptyList(),
    val rating: Float = 0f,
    val latitude: Double = 37.5636,
    val longitude: Double = 126.9869,
    val floor: String = "",
    val website: String = "",
    val parking: String = "",
)

data class MenuItem(
    val name: String,
    val price: String,
)

data class RestaurantPlace(
    val place: Place,
    val halalCategory: String,
    val menuItems: List<MenuItem> = emptyList(),
    val lastOrder: String = "",
    val isMoslemChef: Boolean = false,
    val noAlcohol: Boolean = false,
)

data class ExchangeRate(
    val currency: String,
    val rate: String,
    val flag: String,
)

data class LockerTier(
    val label: String,
    val price: String,
    val available: Boolean,
)

/** drawable이 없을 때 Coil용 URL 폴백과 병합 */
fun Place.galleryModels(fallbackUrls: List<String>): List<Any> =
    if (images.isEmpty()) fallbackUrls else images

private val placeholderImage = listOf(R.drawable.ic_launcher_foreground)

data class StoreMenuItem(
    val name: String,
    val nameEn: String = "",
    val price: String,
)

data class DemoChatMessage(val text: String, val isUser: Boolean)

data class ArExploreDemoHit(
    val name: String,
    val category: String,
    val distance: String,
    val isHalal: Boolean = false,
)

enum class HalalCategory(val label: String) {
    HALAL_MEAT("HALAL MEAT"),
    SEAFOOD("SEAFOOD"),
    VEGGIE("VEGGIE"),
    SALAM_SEOUL("SALAM SEOUL"),
}

data class StoreDetail(
    val name: String,
    val nameEn: String = "",
    val cuisineLabel: String,
    val distance: String,
    val isOpen: Boolean? = null,
    val openHours: String? = null,
    val lastOrder: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val floor: String? = null,
    val website: String? = null,
    val description: String,
    val isHalal: Boolean = false,
    val halalCategory: HalalCategory? = null,
    val showTrustBadges: Boolean = false,
    val menus: List<StoreMenuItem> = emptyList(),
    val imageCount: Int = 3,
    val exchangeRates: List<ExchangeRate> = emptyList(),
    val convenienceServices: String? = null,
    val departments: String? = null,
)

data class ArBuildingStore(val name: String, val category: String)

data class ArBuildingFloor(val floor: String, val stores: List<ArBuildingStore>)

data class ArBuildingPoi(
    val ufid: String,
    val name: String,
    val category: String,
    val distance: String,
    val floorInfo: List<ArBuildingFloor>,
)

data class ArFloorStoreLine(
    val name: String,
    val category: String,
    val isHalal: Boolean,
    val hasDetail: Boolean = false,
)

data class ArFloorSectionUi(
    val label: String,
    val storeCount: Int,
    val categories: List<String>,
    val stores: List<ArFloorStoreLine>,
)

object DummyData {

    val halalRestaurants = listOf(
        RestaurantPlace(
            place = Place(
                id = "r1",
                name = "봉추찜닭 명동점",
                category = "식당",
                categoryKey = "restaurant",
                distance = "120m",
                address = "서울 중구 명동길 26",
                phone = "02-318-0000",
                openHours = "월-일 11:00-22:00",
                isOpen = true,
                description = "명동 한복판에서 한우와 전통 한식을 할랄 기준으로 즐길 수 있는 공간입니다.",
                tags = listOf("할랄 인증", "무슬림 조리사", "주류 미판매"),
                images = placeholderImage,
                floor = "1F",
            ),
            halalCategory = "HALAL MEAT",
            menuItems = listOf(
                MenuItem("한우 불고기 정식", "15,000원"),
                MenuItem("된장찌개 세트", "9,000원"),
                MenuItem("비빔밥", "10,000원"),
            ),
            lastOrder = "21:20",
            isMoslemChef = true,
            noAlcohol = true,
        ),
        RestaurantPlace(
            place = Place(
                id = "r2",
                name = "레팍라식당",
                category = "식당",
                categoryKey = "restaurant",
                distance = "500m",
                address = "서울 중구 을지로 12",
                phone = "02-777-1234",
                openHours = "월-일 10:00-21:00",
                isOpen = true,
                description = "말레이시아 전통 요리와 한식을 함께 즐길 수 있는 할랄 식당입니다.",
                tags = listOf("할랄 인증", "말레이시아 요리", "주류 미판매"),
                images = placeholderImage,
                floor = "1F",
            ),
            halalCategory = "HALAL MEAT",
            menuItems = listOf(
                MenuItem("나시르막", "12,000원"),
                MenuItem("사테꼬치", "8,000원"),
            ),
            lastOrder = "20:30",
            isMoslemChef = true,
            noAlcohol = true,
        ),
        RestaurantPlace(
            place = Place(
                id = "r3",
                name = "명동해산물",
                category = "식당",
                categoryKey = "restaurant",
                distance = "350m",
                address = "서울 중구 명동8나길 5",
                phone = "02-318-5678",
                openHours = "월-일 11:00-22:00",
                isOpen = true,
                description = "신선한 해산물 요리를 제공하는 할랄 인증 식당입니다.",
                tags = listOf("해산물", "주류 미판매"),
                images = placeholderImage,
                floor = "1F",
            ),
            halalCategory = "SEAFOOD",
            menuItems = listOf(
                MenuItem("해물파전", "13,000원"),
                MenuItem("조개찜", "18,000원"),
            ),
            lastOrder = "21:00",
            noAlcohol = true,
        ),
        RestaurantPlace(
            place = Place(
                id = "r4",
                name = "그린가든 명동",
                category = "식당",
                categoryKey = "restaurant",
                distance = "280m",
                address = "서울 중구 충무로 15",
                phone = "02-318-9999",
                openHours = "월-일 10:00-21:00",
                isOpen = true,
                description = "채식주의자를 위한 비건/채식 메뉴를 제공합니다.",
                tags = listOf("채식", "비건", "주류 미판매"),
                images = placeholderImage,
                floor = "1F",
            ),
            halalCategory = "VEGGIE",
            menuItems = listOf(
                MenuItem("채식 비빔밥", "11,000원"),
                MenuItem("두부 된장찌개", "9,000원"),
            ),
            lastOrder = "20:30",
            noAlcohol = true,
        ),
        RestaurantPlace(
            place = Place(
                id = "r5",
                name = "살람서울 레스토랑",
                category = "식당",
                categoryKey = "restaurant",
                distance = "450m",
                address = "서울 중구 명동길 40",
                phone = "02-318-7777",
                openHours = "월-일 11:00-22:00",
                isOpen = true,
                description = "무슬림 여행자를 위한 한국-중동 퓨전 요리 레스토랑입니다.",
                tags = listOf("살람서울 인증", "할랄", "주류 미판매"),
                images = placeholderImage,
                floor = "1F",
            ),
            halalCategory = "SALAM SEOUL",
            menuItems = listOf(
                MenuItem("후무스 플레이트", "14,000원"),
                MenuItem("케밥 라이스", "13,000원"),
            ),
            lastOrder = "21:00",
            isMoslemChef = true,
            noAlcohol = true,
        ),
    )

    val prayerRooms = listOf(
        Place(
            id = "p1",
            name = "서울중앙성원 기도실",
            category = "기도실",
            categoryKey = "prayer_room",
            distance = "350m",
            address = "서울 용산구 이태원로 255",
            phone = "02-792-1234",
            openHours = "24시간",
            isOpen = true,
            description = "한국 최초의 이슬람 사원으로 1976년에 건립되었습니다.",
            tags = listOf("남녀분리", "우두시설", "기도매트", "주차가능"),
            images = placeholderImage,
            parking = "가능",
        ),
        Place(
            id = "p2",
            name = "명동 무슬림 기도공간",
            category = "기도실",
            categoryKey = "prayer_room",
            distance = "520m",
            address = "서울 중구 명동길 14",
            phone = "",
            openHours = "09:00-21:00",
            isOpen = true,
            description = "명동 쇼핑 중 기도할 수 있는 무슬림 친화 공간입니다.",
            tags = listOf("남녀분리", "우두시설", "기도매트"),
            images = placeholderImage,
            floor = "3F",
        ),
        Place(
            id = "p3",
            name = "남산타워 기도실",
            category = "기도실",
            categoryKey = "prayer_room",
            distance = "1.2km",
            address = "서울 용산구 남산공원길 105",
            phone = "02-3455-9277",
            openHours = "10:00-23:00",
            isOpen = false,
            description = "남산타워 내 무슬림 여행자를 위한 기도 공간입니다.",
            tags = listOf("기도매트"),
            images = placeholderImage,
        ),
    )

    val cafes = listOf(
        Place(
            id = "c1",
            name = "스타벅스 명동점",
            category = "카페",
            categoryKey = "cafe",
            distance = "80m",
            address = "서울 중구 명동길 14",
            phone = "02-318-1234",
            openHours = "07:00-22:00",
            isOpen = true,
            description = "명동 중심에 위치한 스타벅스입니다.",
            tags = listOf("와이파이", "콘센트", "테이크아웃"),
            images = placeholderImage,
            floor = "1F",
        ),
        Place(
            id = "c2",
            name = "투썸플레이스 명동",
            category = "카페",
            categoryKey = "cafe",
            distance = "150m",
            address = "서울 중구 을지로 10",
            phone = "02-777-5678",
            openHours = "08:00-22:00",
            isOpen = true,
            description = "케이크와 커피를 즐길 수 있는 카페입니다.",
            tags = listOf("와이파이", "케이크", "테이크아웃"),
            images = placeholderImage,
            floor = "1F",
        ),
    )

    val cafeRepresentativeMenus: Map<String, List<MenuItem>> = mapOf(
        "c1" to listOf(
            MenuItem("아메리카노", "4,500원"),
            MenuItem("카페 라떼", "5,000원"),
            MenuItem("자허블 크림 프라푸치노", "6,300원"),
        ),
        "c2" to listOf(
            MenuItem("스트로베리 초콜릿 생크림", "7,500원"),
            MenuItem("아이스 밀크티", "5,500원"),
        ),
    )

    val shoppingPlaces = listOf(
        Place(
            id = "s1",
            name = "눈스퀘어",
            category = "쇼핑",
            categoryKey = "shopping",
            distance = "15m",
            address = "서울 중구 명동 중앙로 26",
            phone = "02-778-1234",
            openHours = "10:00-22:00",
            isOpen = true,
            description = "명동 중심 대형 복합 쇼핑몰. 지하2층~지상8층, 패션·뷰티·F&B 입점.",
            tags = listOf("주차가능", "무료입장", "ATM"),
            images = placeholderImage,
            parking = "가능",
            website = "noonssquare.com",
        ),
        Place(
            id = "s2",
            name = "롯데백화점 명동본점",
            category = "쇼핑",
            categoryKey = "shopping",
            distance = "300m",
            address = "서울 중구 남대문로 81",
            phone = "02-771-2500",
            openHours = "10:30-20:00",
            isOpen = true,
            description = "국내 최대 규모의 백화점 중 하나입니다.",
            tags = listOf("주차가능", "무슬림 친화", "ATM", "환전"),
            images = placeholderImage,
            parking = "가능",
            website = "lotteshopping.com",
        ),
    )

    val convenienceStores = listOf(
        Place(
            id = "cv1",
            name = "CU 명동중앙점",
            category = "편의점",
            categoryKey = "convenience_store",
            distance = "50m",
            address = "서울 중구 명동길 26",
            phone = "02-318-0001",
            openHours = "24시간",
            isOpen = true,
            description = "24시간 운영 편의점입니다.",
            tags = listOf("24시간", "ATM", "택배"),
            images = placeholderImage,
        ),
        Place(
            id = "cv2",
            name = "GS25 명동점",
            category = "편의점",
            categoryKey = "convenience_store",
            distance = "120m",
            address = "서울 중구 명동8나길 3",
            phone = "02-318-0002",
            openHours = "24시간",
            isOpen = true,
            description = "24시간 운영 편의점입니다.",
            tags = listOf("24시간", "무인계산대"),
            images = placeholderImage,
        ),
    )

    val atmPlaces = listOf(
        Place(
            id = "a1",
            name = "KEB하나은행 ATM 명동점",
            category = "ATM",
            categoryKey = "atm",
            distance = "80m",
            address = "서울 중구 명동길 14",
            openHours = "24시간",
            isOpen = true,
            description = "외국 카드 사용 가능한 ATM입니다.",
            tags = listOf("VISA", "MASTERCARD", "UnionPay", "24시간"),
            images = placeholderImage,
            floor = "1F",
        ),
        Place(
            id = "a2",
            name = "신한은행 ATM 명동중앙점",
            category = "ATM",
            categoryKey = "atm",
            distance = "200m",
            address = "서울 중구 을지로 12",
            openHours = "24시간",
            isOpen = true,
            description = "외국 카드 사용 가능한 ATM입니다.",
            tags = listOf("VISA", "MASTERCARD", "24시간"),
            images = placeholderImage,
            floor = "1F",
        ),
    )

    val bankPlaces = listOf(
        Place(
            id = "b1",
            name = "KEB하나은행 명동점",
            category = "은행",
            categoryKey = "bank",
            distance = "200m",
            address = "서울 중구 을지로 35",
            phone = "02-777-1000",
            openHours = "09:00-16:00",
            isOpen = true,
            description = "외국인 전용 창구 운영 중입니다.",
            tags = listOf("환전", "외국인계좌", "ATM"),
            images = placeholderImage,
        ),
    )

    val exchangePlaces = listOf(
        Place(
            id = "e1",
            name = "명동 외환센터",
            category = "환전소",
            categoryKey = "exchange",
            distance = "180m",
            address = "서울 중구 명동길 26",
            phone = "02-318-2000",
            openHours = "09:00-20:00",
            isOpen = true,
            description = "명동 최고 환율의 환전소입니다.",
            tags = listOf("수수료 없음", "현장환전"),
            images = placeholderImage,
            floor = "2F",
        ),
    )

    val exchangeRates = listOf(
        ExchangeRate("USD", "1,320원", "🇺🇸"),
        ExchangeRate("MYR", "285원", "🇲🇾"),
        ExchangeRate("SAR", "352원", "🇸🇦"),
        ExchangeRate("EUR", "1,430원", "🇪🇺"),
        ExchangeRate("JPY", "8.9원", "🇯🇵"),
    )

    val subwayPlaces = listOf(
        Place(
            id = "sub1",
            name = "명동역",
            category = "지하철역",
            categoryKey = "subway",
            distance = "250m",
            address = "서울 중구 퇴계로 지하 163",
            phone = "02-6110-4314",
            openHours = "05:30-24:00",
            isOpen = true,
            description = "4호선 명동역입니다. 6번 출구: 명동 거리. 7번: 남대문·회현. 8번: 남산 케이블카 연결.",
            tags = listOf("4호선", "엘리베이터", "에스컬레이터", "화장실"),
            images = placeholderImage,
        ),
    )

    val restroomPlaces = listOf(
        Place(
            id = "rest1",
            name = "명동 공중화장실",
            category = "화장실",
            categoryKey = "restroom",
            distance = "100m",
            address = "서울 중구 명동길 26 인근",
            openHours = "24시간",
            isOpen = true,
            description = "명동 중심가 공중화장실입니다.",
            tags = listOf("남녀분리", "장애인화장실", "기저귀교환대"),
            images = placeholderImage,
        ),
    )

    val lockerPlaces = listOf(
        Place(
            id = "l1",
            name = "명동역 물품보관함",
            category = "물품보관함",
            categoryKey = "locker",
            distance = "250m",
            address = "서울 중구 명동역",
            phone = "02-318-5900",
            openHours = "05:30-24:00",
            isOpen = true,
            description = "명동역 내 물품보관함입니다.",
            tags = listOf("소형 2,000원", "중형 3,000원", "대형 4,000원", "카드결제"),
            images = placeholderImage,
            floor = "지하 1층",
        ),
    )

    val lockerTiers: Map<String, List<LockerTier>> = mapOf(
        "l1" to listOf(
            LockerTier("소형", "2,000원 / 4시간", true),
            LockerTier("중형", "3,000원 / 4시간", true),
            LockerTier("대형", "4,000원 / 4시간", false),
        ),
    )

    val hospitalPlaces = listOf(
        Place(
            id = "h1",
            name = "을지병원 명동",
            category = "병원",
            categoryKey = "hospital",
            distance = "400m",
            address = "서울 중구 을지로 170",
            phone = "02-2760-1114",
            openHours = "09:00-18:00",
            isOpen = true,
            description = "외국인 환자 전용 창구 운영 중입니다.",
            tags = listOf("외국어 가능", "내과", "외과", "응급실"),
            images = placeholderImage,
            website = "emc.ac.kr",
        ),
    )

    val pharmacyPlaces = listOf(
        Place(
            id = "ph1",
            name = "명동 온누리약국",
            category = "약국",
            categoryKey = "pharmacy",
            distance = "90m",
            address = "서울 중구 명동길 14",
            phone = "02-318-3000",
            openHours = "09:00-22:00",
            isOpen = true,
            description = "외국어 가능한 약사가 상주합니다.",
            tags = listOf("영어가능", "외국약품"),
            images = placeholderImage,
            floor = "1F",
        ),
    )

    val touristPlaces = listOf(
        Place(
            id = "t1",
            name = "N서울타워",
            category = "관광지",
            categoryKey = "tourist",
            subCategory = "전망대 성인 21,000원~ (현장 기준)",
            distance = "1.8km",
            address = "서울 용산구 남산공원길 105",
            phone = "02-3455-9277",
            openHours = "10:00-22:00",
            isOpen = true,
            description = "서울 전경을 한눈에 담을 수 있는 랜드마크입니다. 케이블카·산책로와 함께 둘러보기 좋습니다.",
            tags = listOf("야경", "필수 코스"),
            images = placeholderImage,
        ),
    )

    fun findPlaceById(categoryKey: String, placeId: String): Place? = when (categoryKey) {
        "restaurant" -> halalRestaurants.firstOrNull { it.place.id == placeId }?.place
        "prayer_room" -> prayerRooms.firstOrNull { it.id == placeId }
        "cafe" -> cafes.firstOrNull { it.id == placeId }
        "shopping" -> shoppingPlaces.firstOrNull { it.id == placeId }
        "convenience_store" -> convenienceStores.firstOrNull { it.id == placeId }
        "atm" -> atmPlaces.firstOrNull { it.id == placeId }
        "bank" -> bankPlaces.firstOrNull { it.id == placeId }
        "exchange" -> exchangePlaces.firstOrNull { it.id == placeId }
        "subway" -> subwayPlaces.firstOrNull { it.id == placeId }
        "restroom" -> restroomPlaces.firstOrNull { it.id == placeId }
        "locker" -> lockerPlaces.firstOrNull { it.id == placeId }
        "hospital" -> hospitalPlaces.firstOrNull { it.id == placeId }
        "pharmacy" -> pharmacyPlaces.firstOrNull { it.id == placeId }
        "tourist", "cultural" -> touristPlaces.firstOrNull { it.id == placeId }
        else -> null
    }

    // ── AR building POI data ──────────────────────────────────────────────────

    val arBuildingPois: List<ArBuildingPoi> = listOf(
        ArBuildingPoi(
            ufid = "noon_square",
            name = "눈스퀘어",
            category = "쇼핑",
            distance = "10m",
            floorInfo = listOf(
                ArBuildingFloor("B1", listOf(
                    ArBuildingStore("스타벅스", "카페"),
                    ArBuildingStore("맥도날드", "식당"),
                    ArBuildingStore("GS25", "편의점"),
                )),
                ArBuildingFloor("1F", listOf(
                    ArBuildingStore("나이키", "쇼핑"),
                )),
                ArBuildingFloor("2F", listOf(
                    ArBuildingStore("이디야커피", "카페"),
                )),
            ),
        ),
        ArBuildingPoi(
            ufid = "lotte_young_plaza",
            name = "롯데영플라자",
            category = "쇼핑",
            distance = "25m",
            floorInfo = listOf(
                ArBuildingFloor("1F", listOf(
                    ArBuildingStore("세븐일레븐", "편의점"),
                    ArBuildingStore("올리브영", "쇼핑"),
                )),
                ArBuildingFloor("2F", listOf(
                    ArBuildingStore("탐앤탐스", "카페"),
                    ArBuildingStore("버거킹", "식당"),
                )),
                ArBuildingFloor("3F", listOf(
                    ArBuildingStore("우리은행", "은행"),
                    ArBuildingStore("우리은행 ATM", "ATM"),
                )),
            ),
        ),
    )

    // ── AR POI floor sections (눈스퀘어) ──────────────────────────────────────

    val noonSquareFloorSections: List<ArFloorSectionUi> = listOf(
        ArFloorSectionUi("B2", 6, listOf("식당"), emptyList()),
        ArFloorSectionUi(
            "B1", 15, listOf("식당", "쇼핑"),
            listOf(
                ArFloorStoreLine("무궁화식당", "한식", false, hasDetail = true),
                ArFloorStoreLine("올리브영", "뷰티", false, hasDetail = true),
            ),
        ),
        ArFloorSectionUi("1F", 12, listOf("쇼핑", "카페"), emptyList()),
        ArFloorSectionUi("2F", 8, listOf("쇼핑"), emptyList()),
        ArFloorSectionUi("3F", 10, listOf("카페"), emptyList()),
        ArFloorSectionUi("4F", 9, listOf("식당"), emptyList()),
        ArFloorSectionUi("5F", 6, listOf("F&B"), emptyList()),
        ArFloorSectionUi("6F", 5, listOf("문화"), emptyList()),
        ArFloorSectionUi("7F", 4, listOf("전망"), emptyList()),
        ArFloorSectionUi("8F", 3, listOf("루프탑"), emptyList()),
    )

    // ── AR store detail data ──────────────────────────────────────────────────

    val arStoreDetails: Map<String, StoreDetail> = mapOf(
        "알리바바 케밥" to StoreDetail(
            name = "알리바바 케밥", nameEn = "Ali Baba Kebab",
            cuisineLabel = "할랄 · 케밥", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00", lastOrder = "라스트오더 21:30",
            address = "서울 중구 명동8길 8-3", phone = "02-318-4221", floor = "1F",
            website = "alibaba-kebab.com",
            description = "할랄 인증 케밥 전문점 — 엄격한 기준의 무슬림 친화 레스토랑\n터키식 정통 케밥을 할랄 재료로 즐길 수 있어요.",
            isHalal = true, halalCategory = HalalCategory.HALAL_MEAT, showTrustBadges = true,
            menus = listOf(StoreMenuItem("치킨 케밥", "Chicken Kebab", "₩12,000"), StoreMenuItem("팔라펠 플레이트", "Falafel Plate", "₩10,000")),
        ),
        "명동부산집" to StoreDetail(
            name = "명동부산집", nameEn = "Myeongdong Busanjib",
            cuisineLabel = "한식 · 해산물", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00", lastOrder = "라스트오더 21:20",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            website = "youscan.com",
            description = "할랄 인증 한식 명소 — 엄격한 기준의 한국 맛집\n명동 한복판에서 비빔밥, 찌개, 간장게장 등 정통 한식을 할랄 기준으로 즐길 수 있습니다.",
            isHalal = true, halalCategory = HalalCategory.HALAL_MEAT, showTrustBadges = true,
            menus = listOf(StoreMenuItem("비빔밥", "Bibimbap", "₩18,000"), StoreMenuItem("해물된장찌개", "Seafood Miso Soup", "₩12,000")),
        ),
        "무궁화식당" to StoreDetail(
            name = "무궁화식당", nameEn = "Mugungwha Restaurant",
            cuisineLabel = "한식 · 가정식", distance = "20m",
            isOpen = true, openHours = "오늘 10:00–21:00", lastOrder = "라스트오더 20:30",
            address = "서울 중구 명동8길 11-4", phone = "02-778-3456", floor = "B1",
            website = "mugungwha.com",
            description = "명동 한복판에서 즐기는 정통 한국 가정식.\n비빔밥, 된장찌개 등 다양한 메뉴를 합리적인 가격에 즐길 수 있어요.",
            menus = listOf(StoreMenuItem("비빔밥", "Bibimbap", "₩13,000"), StoreMenuItem("된장찌개 정식", "Doenjang Jjigae Set", "₩15,000")),
        ),
        "명동교자 본점" to StoreDetail(
            name = "명동교자 본점", nameEn = "Myeongdong Gyoja Main",
            cuisineLabel = "식당", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00", lastOrder = "라스트오더 21:20",
            address = "서울 중구 명동8길 11-6", phone = "010-0142-0278", floor = "1F",
            website = "youscan.com",
            description = "1966년 창업한 서울 명동의 대표 노포 '명동교자 본점'은 진한 닭 육수로 끓인 칼국수와 만두로 유명한 맛집입니다.",
            menus = listOf(StoreMenuItem("칼국수", "Knife-cut Noodles", "₩11,000"), StoreMenuItem("만두", "Dumplings", "₩12,000")),
        ),
        "맷차 명동본점" to StoreDetail(
            name = "맷차 명동본점", nameEn = "Matcha Myeongdong Main",
            cuisineLabel = "카페", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00", lastOrder = "라스트오더 21:20",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            website = "youscan.com",
            description = "맷차 명동본점은 서울 중구 명동에 위치한 4층 규모의 대형 말차(Matcha) 전문 카페로, 맷돌로 직접 간 찻잎을 사용한 깊은 풍미의 말차와 밀크티가 유명합니다.",
            menus = listOf(StoreMenuItem("말차 크림라떼", "Matcha Cream Latte", "₩6,000"), StoreMenuItem("쑥 크림라떼", "Ssuk Cream Latte", "₩7,000")),
        ),
        "올리브영" to StoreDetail(
            name = "올리브영", nameEn = "Olive Young",
            cuisineLabel = "뷰티 · 헬스", distance = "25m",
            isOpen = true, openHours = "오늘 10:00–22:00",
            address = "서울 중구 명동길 53", phone = "02-778-9900", floor = "B1",
            website = "oliveyoung.com",
            description = "국내 최대 H&B 스토어.\nK-뷰티 제품부터 건강식품까지 다양한 상품을 만날 수 있어요.",
        ),
        "나이키 명동눈스퀘어점" to StoreDetail(
            name = "나이키 명동눈스퀘어점", nameEn = "Nike Seoul NoonSquare",
            cuisineLabel = "쇼핑", distance = "15m",
            isOpen = false, openHours = "오늘 11:00–22:00",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "2F - 3F",
            website = "youscan.com",
            description = "명동 눈스퀘어에 위치한 나이키 서울(NIKE SEOUL)은 국내 최대 나이키 플래그십 스토어로, 최신 스포츠 및 라이프스타일 제품을 판매합니다.",
        ),
        "세븐일레븐 명동역점" to StoreDetail(
            name = "세븐일레븐 명동역점", nameEn = "7-Eleven Myeongdong Station",
            cuisineLabel = "편의점", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            website = "youscan.com",
            description = "세븐일레븐 명동역점은 2025년 10월, 서울 중구 명동역 8번 출구 인근에 약 110평 규모로 개점한 대형 복합문화형 편의점입니다.",
            convenienceServices = "택배, 반값택배",
        ),
        "명동 제일환전센터" to StoreDetail(
            name = "명동 제일환전센터", nameEn = "Myeongdong First Exchange",
            cuisineLabel = "환전소", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            website = "youscan.com",
            description = "명동 제일환전센터는 서울 명동에 위치한 사설 환전소로, 시중 은행 대비 높은 환율 우대율과 다양한 외화 보유량으로 유명합니다.",
            exchangeRates = exchangeRates,
        ),
        "하나은행 본점" to StoreDetail(
            name = "하나은행 본점", nameEn = "Hana Bank HQ",
            cuisineLabel = "은행", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            website = "youscan.com",
            description = "하나은행 본점(KEB하나은행 본점)은 서울특별시 중구 을지로 66에 위치한 KEB하나은행의 본사 영업점입니다.",
            exchangeRates = exchangeRates,
        ),
        "신한은행 ATM 명동" to StoreDetail(
            name = "신한은행 ATM 명동금융센터",
            cuisineLabel = "ATM", distance = "15m",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            description = "신한은행 명동금융센터(서울특별시 중구 소공로 94)의 점두365 자동화코너는 24시간 이용 가능한 ATM입니다.",
            exchangeRates = exchangeRates,
        ),
        "서울중앙의료의원" to StoreDetail(
            name = "서울중앙의료의원", nameEn = "Seoul Central Medical Clinic",
            cuisineLabel = "병원", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00", lastOrder = "접수 마감 21:20",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            website = "youscan.com",
            description = "서울중앙메디컬센터는 서울 명동 포스트타워 내 위치한 종합의원으로 영어 안내 서비스를 제공합니다.",
            departments = "이비인후과, 내과, 산부인과, 영상의학과",
        ),
        "명동역 4호선" to StoreDetail(
            name = "명동역 4호선",
            cuisineLabel = "지하철역", distance = "15m",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278",
            description = "명동역(424)은 서울특별시 중구에 위치한 서울교통공사 4호선 역으로, 명동 거리와 바로 연결되는 주요 교통 거점입니다.",
        ),
        "서울중앙우체국 공중화장실" to StoreDetail(
            name = "서울중앙우체국 공중화장실",
            cuisineLabel = "화장실", distance = "15m",
            address = "서울 중구 명동8길 11-6", floor = "1F",
            description = "서울중앙우체국(포스트타워)은 명동 중심가에 위치하여 무료로 이용 가능한 공중화장실을 운영합니다.",
        ),
        "시청역1호선 물품보관함" to StoreDetail(
            name = "시청역1호선 B1 물품보관함",
            cuisineLabel = "물품보관함", distance = "15m",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "B1",
            description = "서울 지하철 1호선 시청역 B1층 물품보관함으로, 또타라커(T-Locker) 앱으로 짐 보관·찾기·결제가 가능합니다.",
        ),
        "명동퓨어약국" to StoreDetail(
            name = "명동퓨어약국",
            cuisineLabel = "약국", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            website = "youscan.com",
            description = "명동 퓨어약국은 2026년 2월 개설된 서울 중구 명동 소재 약국으로, 외국인 여행객을 위한 영어 안내 서비스를 제공합니다.",
        ),
        "웨스틴 조선 서울" to StoreDetail(
            name = "웨스틴 조선 서울", nameEn = "Westin Josun Seoul",
            cuisineLabel = "호텔", distance = "15m",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            website = "youscan.com",
            description = "웨스틴 조선 서울은 1914년 개관한 한국 최고(最古)의 5성급 럭셔리 호텔로, 명동 중심에 위치합니다.",
        ),
        "Adnan Kebab 기도실" to StoreDetail(
            name = "Adnan Kebab 기도실",
            cuisineLabel = "기도실", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "1F",
            description = "서울 남대문시장에 위치한 Adnan Kebab 매장 뒤편 건물에 무슬림 방문객을 위한 별도의 기도실이 마련되어 있습니다.",
        ),
        "스타벅스" to StoreDetail(
            name = "스타벅스", cuisineLabel = "카페", distance = "10m",
            isOpen = true, openHours = "오늘 07:30–22:00", lastOrder = "라스트오더 21:30",
            address = "눈스퀘어 B1F", phone = "02-318-0001", floor = "B1",
            description = "스타벅스 눈스퀘어점입니다. 리저브 음료와 다양한 푸드를 즐길 수 있어요.",
            menus = listOf(StoreMenuItem("아메리카노", "Americano", "₩4,500"), StoreMenuItem("카페라떼", "Cafe Latte", "₩5,000")),
        ),
        "맥도날드" to StoreDetail(
            name = "맥도날드", cuisineLabel = "식당", distance = "10m",
            isOpen = true, openHours = "오늘 24시간 영업",
            address = "눈스퀘어 B1F", phone = "02-318-0002", floor = "B1",
            description = "맥도날드 눈스퀘어점입니다. 버거, 치킨, 커피 등을 즐길 수 있어요.",
            menus = listOf(StoreMenuItem("빅맥", "Big Mac", "₩6,000"), StoreMenuItem("상하이 스파이시", "Shanghai Spicy", "₩6,500")),
        ),
        "GS25" to StoreDetail(
            name = "GS25", cuisineLabel = "편의점", distance = "10m",
            isOpen = true, openHours = "오늘 24시간 영업",
            address = "눈스퀘어 B1F", floor = "B1",
            description = "GS25 눈스퀘어점입니다.",
            convenienceServices = "택배, 복합기, ATM",
        ),
        "나이키" to StoreDetail(
            name = "나이키", cuisineLabel = "쇼핑", distance = "10m",
            isOpen = true, openHours = "오늘 11:00–22:00",
            address = "눈스퀘어 1F", phone = "02-318-0004", floor = "1F",
            website = "nike.com",
            description = "나이키 눈스퀘어점입니다. 최신 스포츠 및 라이프스타일 제품을 만날 수 있어요.",
        ),
        "이디야커피" to StoreDetail(
            name = "이디야커피", cuisineLabel = "카페", distance = "10m",
            isOpen = true, openHours = "오늘 08:00–22:00",
            address = "눈스퀘어 2F", phone = "02-318-0005", floor = "2F",
            description = "이디야커피 눈스퀘어점입니다.",
            menus = listOf(StoreMenuItem("아메리카노", "Americano", "₩2,500"), StoreMenuItem("바닐라라떼", "Vanilla Latte", "₩3,500")),
        ),
        "세븐일레븐" to StoreDetail(
            name = "세븐일레븐", cuisineLabel = "편의점", distance = "25m",
            isOpen = true, openHours = "오늘 24시간 영업",
            address = "롯데영플라자 1F", floor = "1F",
            description = "세븐일레븐 롯데영플라자점입니다.",
            convenienceServices = "택배, 반값택배",
        ),
        "탐앤탐스" to StoreDetail(
            name = "탐앤탐스", cuisineLabel = "카페", distance = "25m",
            isOpen = true, openHours = "오늘 08:00–22:00",
            address = "롯데영플라자 2F", phone = "02-318-0007", floor = "2F",
            description = "탐앤탐스 롯데영플라자점입니다. 다양한 커피와 베이커리를 즐길 수 있어요.",
            menus = listOf(StoreMenuItem("탐탐커피", "TamTam Coffee", "₩3,500"), StoreMenuItem("프레첼", "Pretzel", "₩2,500")),
        ),
        "버거킹" to StoreDetail(
            name = "버거킹", cuisineLabel = "식당", distance = "25m",
            isOpen = true, openHours = "오늘 10:00–23:00",
            address = "롯데영플라자 2F", phone = "02-318-0008", floor = "2F",
            description = "버거킹 롯데영플라자점입니다. 와퍼, 치킨버거 등 다양한 메뉴를 즐길 수 있어요.",
            menus = listOf(StoreMenuItem("와퍼", "Whopper", "₩7,500"), StoreMenuItem("치킨버거", "Chicken Burger", "₩5,500")),
        ),
        "우리은행" to StoreDetail(
            name = "우리은행", cuisineLabel = "은행", distance = "25m",
            isOpen = true, openHours = "오늘 09:00–16:00",
            address = "롯데영플라자 3F", phone = "1588-5000", floor = "3F",
            website = "wooribank.com",
            description = "우리은행 명동지점입니다. 외화환전, 계좌개설 등 각종 은행 업무를 처리할 수 있어요.",
            exchangeRates = exchangeRates,
        ),
        "우리은행 ATM" to StoreDetail(
            name = "우리은행 ATM", cuisineLabel = "ATM", distance = "25m",
            address = "롯데영플라자 3F", floor = "3F",
            description = "우리은행 ATM 롯데영플라자점입니다. 24시간 이용 가능합니다.",
            exchangeRates = exchangeRates,
        ),
        "CGV 명동" to StoreDetail(
            name = "CGV 명동", cuisineLabel = "문화시설", distance = "15m",
            isOpen = true, openHours = "오늘 11:00–22:00",
            address = "서울 중구 명동8길 11-6", phone = "010-3142-0278", floor = "8F",
            website = "youscan.com",
            description = "CGV 명동(눈스퀘어)은 서울 중구 명동길 14 눈스퀘어 8층에 위치한 복합 상영관으로, 최신 영화와 4DX 상영 서비스를 제공합니다.",
        ),
    )

    fun storeDetailFor(name: String): StoreDetail? = arStoreDetails[name]

    fun storeDetailOrFallback(name: String, category: String): StoreDetail =
        arStoreDetails[name] ?: StoreDetail(
            name = name,
            cuisineLabel = category,
            distance = "",
            description = "${name}입니다.",
        )

    // ── Home screen ───────────────────────────────────────────────────────────────
    val homeAreaName: String = "명동"
    val currentLocationLabel: String = "명동역 6번 출구 근처"
    val qiblaDirectionLabel: String = "키블라 방향: 남서 232°"
    val nextPrayerLabel: String = "다음 기도: Dhuhr 12:15"

    // ── Filter labels ─────────────────────────────────────────────────────────────
    val halalFilterLabels: List<String> = listOf("전체", "HALAL MEAT", "SEAFOOD", "VEGGIE", "SALAM SEOUL")
    val prayerRoomFilterLabels: List<String> = listOf("전체", "거리순", "남녀 분리")

    // ── AR demo data ──────────────────────────────────────────────────────────────
    val arExploreDemoChatMessages: List<DemoChatMessage> = listOf(
        DemoChatMessage("안녕하세요! 스캔팡입니다. 주변 장소를 AR로 안내해 드릴게요.", false),
        DemoChatMessage("아미나님, 오늘은 어떤 할랄 맛집을 찾으세요?", true),
    )
    val arExploreDemoHits: List<ArExploreDemoHit> = listOf(
        ArExploreDemoHit("알리바바 케밥", "식당", "52m", true),
        ArExploreDemoHit("할랄가든 명동점", "식당", "120m", true),
        ArExploreDemoHit("명동성당", "관광지", "350m", false),
        ArExploreDemoHit("우리은행 환전소", "환전", "80m", false),
        ArExploreDemoHit("세븐일레븐 명동점", "편의점", "30m", false),
    )
    val arNavDemoUserMessage: String = "눈스퀘어가 뭐야?"
    val arNavDemoAgentMessage: String = "거의 다 왔어요! 입구는 정면 오른쪽이에요."

    /**
     * 통합 검색·결과 화면에서 더미에 어떤 목록이 들어 있는지 표시할 때 사용.
     * 각 줄: `카테고리명 개수 — 이름들`
     */
    fun searchDummySourceCatalogLines(): List<String> {
        fun joinNames(places: List<Place>): String = places.joinToString(separator = ", ") { it.name }
        fun joinRestaurantNames(list: List<RestaurantPlace>): String =
            list.joinToString(separator = ", ") { it.place.name }
        return listOf(
            "할랄 식당 ${halalRestaurants.size} — ${joinRestaurantNames(halalRestaurants)}",
            "기도실 ${prayerRooms.size} — ${joinNames(prayerRooms)}",
            "카페 ${cafes.size} — ${joinNames(cafes)}",
            "쇼핑 ${shoppingPlaces.size} — ${joinNames(shoppingPlaces)}",
            "편의점 ${convenienceStores.size} — ${joinNames(convenienceStores)}",
            "ATM ${atmPlaces.size} — ${joinNames(atmPlaces)}",
            "은행 ${bankPlaces.size} — ${joinNames(bankPlaces)}",
            "환전소 ${exchangePlaces.size} — ${joinNames(exchangePlaces)}",
            "지하철역 ${subwayPlaces.size} — ${joinNames(subwayPlaces)}",
            "화장실 ${restroomPlaces.size} — ${joinNames(restroomPlaces)}",
            "물품보관함 ${lockerPlaces.size} — ${joinNames(lockerPlaces)}",
            "병원 ${hospitalPlaces.size} — ${joinNames(hospitalPlaces)}",
            "약국 ${pharmacyPlaces.size} — ${joinNames(pharmacyPlaces)}",
            "관광지 ${touristPlaces.size} — ${joinNames(touristPlaces)}",
            "환율표(검색 제외) ${exchangeRates.size}종 — ${exchangeRates.joinToString { it.currency }}",
        )
    }
}
