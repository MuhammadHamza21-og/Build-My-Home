package com.example.data.generator

import com.example.data.model.BoundaryMeasurements
import com.example.data.model.FloorLevelPlan
import com.example.data.model.RoomGeometry
import com.example.data.model.RoomType

data class ValidationResult(
    val isValid: Boolean,
    val issues: List<String>,
    val warnings: List<String>,
    val coveredAreaSqFt: Float,
    val coveragePercentage: Float
)

object ArchitecturalValidator {

    fun validateFloorPlan(
        boundaries: BoundaryMeasurements,
        floorPlan: FloorLevelPlan
    ): ValidationResult {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        val plotWidth = minOf(boundaries.front, boundaries.rear)
        val plotLength = minOf(boundaries.left, boundaries.right)
        val totalPlotArea = boundaries.calculateCalculatedSqFt()

        var totalCovered = 0f

        val rooms = floorPlan.rooms

        for (i in rooms.indices) {
            val r1 = rooms[i]

            // Check non-open area for covered footprint
            if (r1.roomType != RoomType.FRONT_LAWN &&
                r1.roomType != RoomType.OTS_SHAFT &&
                r1.roomType != RoomType.REAR_PASSAGE &&
                r1.roomType != RoomType.TERRACE_BALCONY
            ) {
                totalCovered += r1.areaSqFt
            }

            // Boundary containment check
            if (r1.x < -0.1f || (r1.x + r1.width) > (plotWidth + 0.5f)) {
                issues.add("${r1.name} exceeds horizontal boundary (Width: ${r1.width}ft, X: ${r1.x}ft, Plot: ${plotWidth}ft)")
            }
            if (r1.y < -0.1f || (r1.y + r1.length) > (plotLength + 0.5f)) {
                issues.add("${r1.name} exceeds longitudinal boundary (Length: ${r1.length}ft, Y: ${r1.y}ft, Plot: ${plotLength}ft)")
            }

            // Minimum architectural sizes
            when (r1.roomType) {
                RoomType.BEDROOM, RoomType.MASTER_BEDROOM -> {
                    if (r1.width < 9.0f || r1.length < 9.5f) {
                        warnings.add("${r1.name} is below recommended bedroom size (min 10' x 10')")
                    }
                }
                RoomType.BATHROOM -> {
                    if (r1.width < 3.5f || r1.length < 5.0f) {
                        warnings.add("${r1.name} is very narrow (min 4' x 6')")
                    }
                }
                RoomType.KITCHEN -> {
                    if (r1.width < 6.0f || r1.length < 7.0f) {
                        warnings.add("${r1.name} is undersized for a functional kitchen (min 7' x 8')")
                    }
                }
                else -> {}
            }

            // Pairwise overlap check
            for (j in (i + 1) until rooms.size) {
                val r2 = rooms[j]

                // OTS shafts or open passages are allowed inside specific zones if designed as inner cutouts
                if (r1.roomType == RoomType.OTS_SHAFT && r2.roomType == RoomType.OTS_SHAFT) continue

                val overlapX = maxOf(0f, minOf(r1.x + r1.width, r2.x + r2.width) - maxOf(r1.x, r2.x))
                val overlapY = maxOf(0f, minOf(r1.y + r1.length, r2.y + r2.length) - maxOf(r1.y, r2.y))

                // If overlap is greater than standard wall tolerance (0.5 ft)
                if (overlapX > 0.5f && overlapY > 0.5f) {
                    // Check if one is a courtyard/lawn and the other is car porch
                    if (!(r1.roomType == RoomType.FRONT_LAWN && r2.roomType == RoomType.CAR_PORCH)) {
                        issues.add("Spatial overlap detected between ${r1.name} and ${r2.name}")
                    }
                }
            }
        }

        val coveragePercentage = if (totalPlotArea > 0f) (totalCovered / totalPlotArea) * 100f else 0f
        if (coveragePercentage > 85f) {
            warnings.add("Ground covered area is ${coveragePercentage.toInt()}%. LDA/CDA residential bylaws recommend at least 20-25% open space for ventilation.")
        }

        return ValidationResult(
            isValid = issues.isEmpty(),
            issues = issues,
            warnings = warnings,
            coveredAreaSqFt = totalCovered,
            coveragePercentage = coveragePercentage
        )
    }
}
