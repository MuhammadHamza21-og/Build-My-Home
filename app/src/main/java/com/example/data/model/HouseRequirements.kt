package com.example.data.model

enum class BuildingType(val floors: Int, val label: String, val shortDesc: String) {
    SINGLE_STOREY(1, "Single Storey", "Ground floor only, compact and accessible"),
    DOUBLE_STOREY(2, "Double Storey", "Ground + 1st Floor, most popular for families in Pakistan"),
    TRIPLE_STOREY(3, "Triple Storey", "Ground + 1st + 2nd Floor, multi-generational residence"),
    CUSTOM_STOREY(4, "4-Storey (Basement/Penthouse)", "Maximum utilization for urban luxury living")
}

enum class ArchitecturalStyle(val label: String, val description: String, val facadePalette: String) {
    MODERN_PAKISTANI("Modern Pakistani", "Travertine stone cladding, wood louvers, expansive glass, and LED cove accents", "Warm Stone & Teak Wood"),
    CONTEMPORARY_LUXURY("Contemporary Luxury", "Cantilevered balconies, charcoal stone, double-height glass lobby, water feature", "Charcoal Slate & Marble"),
    MINIMALIST("Minimalist Zen", "Crisp cubic volumes, off-white render, seamless flush glazing, understated elegance", "Pure White & Concrete"),
    TRADITIONAL("Traditional Mughal / Colonial", "Arched porticos, jali screens, pitched terracotta tile accents, brick highlights", "Antique Brick & Terracotta"),
    MODERN_VILLA("Modern Mediterranean Villa", "Pergolas, light stucco, arched entryways, panoramic terraces, lush greenery", "Cream Stucco & Bronze"),
    BUDGET_FRIENDLY("Cost-Efficient Modern", "Optimized structural spans, minimal cantilever cost, efficient brickwork", "Durable Plaster & Accent Paint")
}

enum class ConstructionQuality(val label: String, val description: String, val greyRatePerSqFt: Float, val finishRatePerSqFt: Float) {
    ECONOMY("Economy Grade", "Standard local brickwork, Grade 40 steel, standard tiles, local fittings", 2200f, 1800f),
    STANDARD("Standard Grade", "Grade 60 steel (Mughal/Amreli), Maple Leaf/Bestway cement, porcelain tiles, Faisal sanitary", 2600f, 2500f),
    PREMIUM_EXECUTIVE("Premium Executive", "A-grade structural concrete, Master/Spanish porcelain, Grohe/Porta sanitary, solid Ash wood doors", 3100f, 3600f),
    ULTRA_LUXURY("Ultra Luxury / Imported", "Earthquake-engineered RCC, Italian marble, imported double-glazed Schuco windows, smart home automation", 3800f, 5200f)
}

enum class BudgetRange(val label: String, val approxRangePKR: String) {
    BUDGET("Budget Friendly", "PKR 5M - 12M"),
    MID_TIER("Mid-Range Family", "PKR 12M - 25M"),
    PREMIUM("Premium Residence", "PKR 25M - 50M"),
    LUXURY("Luxury Estate", "PKR 50M+")
}

data class RoomRequirements(
    val bedrooms: Int = 3,
    val attachedBaths: Int = 3,
    val commonBaths: Int = 1,
    val kitchens: Int = 1,
    val tvLounges: Int = 1,
    val drawingRooms: Int = 1,
    val diningAreas: Int = 1,
    val guestRooms: Int = 0,
    val studyRooms: Int = 0,
    val prayerRooms: Int = 1,
    val storeRooms: Int = 1,
    val laundryRooms: Int = 1,
    val servantQuarters: Int = 0,
    val carParkingSlots: Int = 1, // 1 car, 2 cars
    val lawnCourtyard: Boolean = true,
    val roofTerrace: Boolean = true,
    val balcony: Boolean = true
)

data class HouseRequirements(
    val buildingType: BuildingType = BuildingType.DOUBLE_STOREY,
    val rooms: RoomRequirements = RoomRequirements(),
    val familyMembers: Int = 5,
    val separateGuestEntrance: Boolean = true,
    val openPlanKitchen: Boolean = false, // false = separate closed kitchen, highly favored in Pakistan
    val naturalLightVentilationShafts: Boolean = true,
    val architecturalStyle: ArchitecturalStyle = ArchitecturalStyle.MODERN_PAKISTANI,
    val constructionQuality: ConstructionQuality = ConstructionQuality.STANDARD,
    val budgetRange: BudgetRange = BudgetRange.MID_TIER,
    val notes: String = ""
)
