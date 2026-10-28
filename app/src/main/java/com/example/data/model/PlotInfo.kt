package com.example.data.model

import kotlin.math.abs
import kotlin.math.sqrt

enum class MarlaStandard(val sqFt: Float, val label: String, val description: String) {
    STANDARD_225(225.0f, "225 Sq Ft / Marla", "Private Housing Societies (Bahria, DHA, Gulberg, etc.)"),
    STANDARD_272_25(272.25f, "272.25 Sq Ft / Marla", "Official Government & Revenue Standard (LDA, CDA, Punjab)")
}

enum class PlotAreaUnit(val label: String, val shortLabel: String) {
    MARLA("Marla", "Marla"),
    KANAL("Kanal", "Kanal"),
    SQ_FT("Square Feet", "sq ft"),
    SQ_YARDS("Square Yards (Guz)", "sq yd"),
    ACRES("Acres", "acre")
}

enum class PlotShape(val label: String, val description: String) {
    RECTANGLE("Rectangular", "Uniform front/rear and left/right lengths"),
    SQUARE("Square", "Equal lengths on all 4 boundaries"),
    CORNER("Corner Plot", "Two adjacent sides open to roads with chamfered or standard corner"),
    TRAPEZOID("Trapezoid (Tilted)", "Non-parallel sides or wider front/back"),
    IRREGULAR("Irregular Polygon", "Asymmetrical boundaries with custom vertex coordinates")
}

enum class RoadFacingSide(val label: String) {
    FRONT("Front Road"),
    REAR("Rear Road"),
    CORNER_FRONT_LEFT("Corner (Front & Left Road)"),
    CORNER_FRONT_RIGHT("Corner (Front & Right Road)"),
    FRONT_AND_REAR("Double Road (Front & Back)")
}

enum class NorthDirection(val angleDegrees: Float, val label: String) {
    NORTH_TOP(0f, "North (Facing Up)"),
    NORTH_RIGHT(90f, "North (Facing Right / East)"),
    NORTH_BOTTOM(180f, "North (Facing Down / South)"),
    NORTH_LEFT(270f, "North (Facing Left / West)")
}

data class Point2D(
    val x: Float,
    val y: Float
)

data class BoundaryMeasurements(
    val front: Float = 30.0f,    // in feet
    val rear: Float = 30.0f,     // in feet
    val left: Float = 60.0f,     // in feet
    val right: Float = 60.0f,    // in feet
    val diagonal: Float = 0.0f,  // optional survey diagonal for irregular trapezoid
    val polygonPoints: List<Point2D> = emptyList()
) {
    /**
     * Calculates the dimensional area in square feet.
     * For rectangle/square: average front/rear * average left/right (Bretschneider approximation or exact for rectangle).
     */
    fun calculateCalculatedSqFt(): Float {
        if (polygonPoints.size >= 3) {
            // Shoelace formula for polygon
            var area = 0.0f
            val n = polygonPoints.size
            for (i in 0 until n) {
                val j = (i + 1) % n
                area += polygonPoints[i].x * polygonPoints[j].y
                area -= polygonPoints[j].x * polygonPoints[i].y
            }
            return abs(area) / 2.0f
        }
        val avgWidth = (front + rear) / 2.0f
        val avgLength = (left + right) / 2.0f
        return avgWidth * avgLength
    }
}

data class PlotInfo(
    val projectName: String = "My Dream Residence",
    val country: String = "Pakistan",
    val city: String = "Lahore",
    val statedAreaValue: Float = 5.0f,
    val statedAreaUnit: PlotAreaUnit = PlotAreaUnit.MARLA,
    val marlaStandard: MarlaStandard = MarlaStandard.STANDARD_225,
    val plotShape: PlotShape = PlotShape.RECTANGLE,
    val roadFacing: RoadFacingSide = RoadFacingSide.FRONT,
    val northDirection: NorthDirection = NorthDirection.NORTH_TOP,
    val boundaries: BoundaryMeasurements = BoundaryMeasurements(
        front = 25f,
        rear = 25f,
        left = 45f,
        right = 45f
    )
) {
    /**
     * Converts the stated plot area into square feet based on the selected Marla standard.
     */
    fun getStatedAreaInSqFt(): Float {
        val marlaSqFt = marlaStandard.sqFt
        return when (statedAreaUnit) {
            PlotAreaUnit.MARLA -> statedAreaValue * marlaSqFt
            PlotAreaUnit.KANAL -> statedAreaValue * 20.0f * marlaSqFt
            PlotAreaUnit.SQ_FT -> statedAreaValue
            PlotAreaUnit.SQ_YARDS -> statedAreaValue * 9.0f
            PlotAreaUnit.ACRES -> statedAreaValue * 160.0f * marlaSqFt
        }
    }

    /**
     * Converts a given square footage to Marlas under the current standard.
     */
    fun sqFtToMarla(sqFt: Float): Float = sqFt / marlaStandard.sqFt

    /**
     * Area derived directly from the physical boundaries entered.
     */
    fun getCalculatedAreaInSqFt(): Float = boundaries.calculateCalculatedSqFt()

    /**
     * Area discrepancy percentage between stated and dimensional area.
     */
    fun getAreaDiscrepancyPercentage(): Float {
        val stated = getStatedAreaInSqFt()
        val calculated = getCalculatedAreaInSqFt()
        if (stated <= 0.0f) return 0.0f
        return (abs(calculated - stated) / stated) * 100.0f
    }

    fun hasSignificantDiscrepancy(): Boolean = getAreaDiscrepancyPercentage() > 8.0f
}
