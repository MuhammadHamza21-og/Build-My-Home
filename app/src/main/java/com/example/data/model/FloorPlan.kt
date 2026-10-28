package com.example.data.model

enum class RoomType(val displayName: String, val urduName: String, val colorHex: Long) {
    MASTER_BEDROOM("Master Bedroom", "ماسٹر بیڈ روم", 0xFF6C5CE7),
    BEDROOM("Bedroom", "بیڈ روم", 0xFF74B9FF),
    BATHROOM("Attached Bath", "اٹیچ باتھ", 0xFF81ECEC),
    POWDER_ROOM("Powder Bath", "پاؤڈر روم", 0xFF55EFC4),
    KITCHEN("Kitchen", "باورچی خانہ", 0xFFFF7675),
    DIRTY_KITCHEN("Dirty Kitchen", "ڈیرٹی کچن", 0xFFFAB1A0),
    TV_LOUNGE("TV Lounge", "ٹی وی لاؤنج", 0xFFFDCB6E),
    DRAWING_ROOM("Drawing Room", "ڈرائنگ روم", 0xFFFFEAA7),
    DINING("Dining Area", "ڈائننگ ہال", 0xFFDFE6E9),
    LOBBY_CORRIDOR("Lobby / Foyer", "لابی / راہداری", 0xFFB2BEC3),
    STAIRCASE("Staircase Hall", "سیڑھیاں", 0xFFA29BFE),
    CAR_PORCH("Car Porch", "کار پورچ", 0xFF636E72),
    FRONT_LAWN("Front Lawn", "فرنٹ لان", 0xFF55EFC4),
    REAR_PASSAGE("Rear Setback", "پچھلا راستہ", 0xFF00B894),
    OTS_SHAFT("OTS Shaft (Air/Light)", "ہوا و روشنی شافٹ", 0xFFE056FD),
    TERRACE_BALCONY("Terrace / Balcony", "چھت / ٹیرس", 0xFF00CEC9),
    STORE("Store Room", "اسٹور روم", 0xFF686DE0),
    LAUNDRY("Laundry Area", "لانڈری", 0xFF48DBFB),
    SERVANT_ROOM("Servant Quarter", "ملازم کا کمرہ", 0xFF95AFC0),
    PRAYER_ROOM("Prayer Room", "عبادت گاہ", 0xFFF8A5C2)
}

enum class WallSide {
    TOP, BOTTOM, LEFT, RIGHT
}

data class DoorInfo(
    val id: String,
    val wall: WallSide,
    val offsetRatio: Float = 0.5f,
    val widthFt: Float = 3.0f,
    val swingInside: Boolean = true
)

data class WindowInfo(
    val id: String,
    val wall: WallSide,
    val offsetRatio: Float = 0.5f,
    val widthFt: Float = 4.0f
)

enum class FurnitureType {
    DOUBLE_BED, SINGLE_BED, SOFA_3SEAT, SOFA_L_SHAPE, DINING_TABLE_6, DINING_TABLE_4,
    KITCHEN_STOVE_SINK, CAR_SEDAN, CAR_SUV, WARDROBE, TV_UNIT, COMMODE_BASIN, STAIRS_FLIGHT
}

data class FurnitureItem(
    val id: String,
    val type: FurnitureType,
    val relX: Float, // 0.0 to 1.0 relative to room width
    val relY: Float, // 0.0 to 1.0 relative to room length
    val widthFt: Float,
    val lengthFt: Float,
    val rotationDeg: Float = 0f
)

data class RoomGeometry(
    val id: String,
    val name: String,
    val roomType: RoomType,
    val floorLevel: Int, // 0: Ground, 1: First, 2: Roof
    val x: Float, // distance from left plot boundary in feet
    val y: Float, // distance from front plot boundary in feet
    val width: Float, // width in feet (X dimension)
    val length: Float, // length in feet (Y dimension)
    val doors: List<DoorInfo> = emptyList(),
    val windows: List<WindowInfo> = emptyList(),
    val furniture: List<FurnitureItem> = emptyList(),
    val notes: String = ""
) {
    val areaSqFt: Float get() = width * length

    fun formattedDimensions(): String {
        val wFt = width.toInt()
        val wIn = ((width - wFt) * 12).toInt()
        val lFt = length.toInt()
        val lIn = ((length - lFt) * 12).toInt()
        val wStr = if (wIn > 0) "$wFt'-$wIn\"" else "$wFt'"
        val lStr = if (lIn > 0) "$lFt'-$lIn\"" else "$lFt'"
        return "$wStr × $lStr"
    }

    fun containsPoint(px: Float, py: Float): Boolean {
        return px >= x && px <= (x + width) && py >= y && py <= (y + length)
    }
}

data class FloorLevelPlan(
    val floorLevel: Int,
    val floorName: String,
    val rooms: List<RoomGeometry>,
    val coveredAreaSqFt: Float,
    val openAreaSqFt: Float
)

enum class AlternativeType(val code: String, val title: String, val badge: String) {
    DESIGN_A("A", "Space-Efficient Layout", "Max Area Utilization"),
    DESIGN_B("B", "Modern Family Layout", "Balanced Luxury & Privacy"),
    DESIGN_C("C", "Executive Contemporary", "Premium Courtyard & Elegance")
}

data class FloorPlanDesign(
    val id: String,
    val alternative: AlternativeType,
    val title: String,
    val tagline: String,
    val conceptDescription: String,
    val advantages: List<String>,
    val tradeOffs: List<String>,
    val feasibilityStatus: String = "Feasibility Validated (Complies with setback & ventilation codes)",
    val totalCoveredAreaSqFt: Float,
    val groundFloorCoveredAreaSqFt: Float,
    val openAreaSqFt: Float,
    val floors: List<FloorLevelPlan>
) {
    fun getFloor(level: Int): FloorLevelPlan? {
        return floors.find { it.floorLevel == level } ?: floors.firstOrNull()
    }
}
