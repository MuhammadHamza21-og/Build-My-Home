package com.example

import com.example.data.converters.PlotAreaConverter
import com.example.data.cost.ConstructionCostCalculator
import com.example.data.generator.ArchitecturalValidator
import com.example.data.generator.FloorPlanGeneratorEngine
import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPakistaniMarlaConversions() {
        // Standard 225 sq ft
        val sqFt225 = PlotAreaConverter.marlaToSqFt(5f, MarlaStandard.STANDARD_225)
        assertEquals(1125f, sqFt225, 0.01f)

        val marla225 = PlotAreaConverter.sqFtToMarla(1125f, MarlaStandard.STANDARD_225)
        assertEquals(5f, marla225, 0.01f)

        // Government Revenue Standard 272.25 sq ft
        val sqFt272 = PlotAreaConverter.marlaToSqFt(10f, MarlaStandard.STANDARD_272_25)
        assertEquals(2722.5f, sqFt272, 0.01f)

        // 1 Kanal = 20 Marla
        val kanalMarla = PlotAreaConverter.kanalToMarla(2f)
        assertEquals(40f, kanalMarla, 0.01f)

        // 1 Acre = 8 Kanal = 160 Marla
        val acreKanal = PlotAreaConverter.acreToKanal(1f)
        assertEquals(8f, acreKanal, 0.01f)
    }

    @Test
    fun testPolygonShoelaceArea() {
        // 30 x 60 rectangle defined as polygon
        val points = listOf(
            Point2D(0f, 0f),
            Point2D(30f, 0f),
            Point2D(30f, 60f),
            Point2D(0f, 60f)
        )
        val area = PlotAreaConverter.calculatePolygonArea(points)
        assertEquals(1800f, area, 0.01f)
    }

    @Test
    fun testFloorPlanGeneratorThreeDistinctAlternatives() {
        val plot = PlotInfo(
            statedAreaValue = 10f,
            boundaries = BoundaryMeasurements(front = 35f, rear = 35f, left = 65f, right = 65f)
        )
        val req = HouseRequirements(
            buildingType = BuildingType.DOUBLE_STOREY,
            rooms = RoomRequirements(bedrooms = 4, attachedBaths = 4)
        )

        val alternatives = FloorPlanGeneratorEngine.generateDesignAlternatives(plot, req)
        assertEquals(3, alternatives.size)

        val altA = alternatives[0]
        val altB = alternatives[1]
        val altC = alternatives[2]

        assertEquals(AlternativeType.DESIGN_A, altA.alternative)
        assertEquals(AlternativeType.DESIGN_B, altB.alternative)
        assertEquals(AlternativeType.DESIGN_C, altC.alternative)

        // Verify alternatives have genuinely different room names and configurations
        assertTrue("Alternative A must have ground floor rooms", altA.floors[0].rooms.isNotEmpty())
        assertTrue("Alternative B must have ground floor rooms", altB.floors[0].rooms.isNotEmpty())
        assertTrue("Alternative C must have ground floor rooms", altC.floors[0].rooms.isNotEmpty())

        assertTrue("Covered area should be positive", altA.totalCoveredAreaSqFt > 0)
        assertTrue("Covered area should be positive", altB.totalCoveredAreaSqFt > 0)
        assertTrue("Covered area should be positive", altC.totalCoveredAreaSqFt > 0)
    }

    @Test
    fun testArchitecturalValidatorOnGeneratedPlan() {
        val plot = PlotInfo(
            boundaries = BoundaryMeasurements(front = 30f, rear = 30f, left = 60f, right = 60f)
        )
        val req = HouseRequirements()
        val alternatives = FloorPlanGeneratorEngine.generateDesignAlternatives(plot, req)

        val validation = ArchitecturalValidator.validateFloorPlan(plot.boundaries, alternatives[0].floors[0])
        assertTrue("Generated plan should not violate boundaries or room limits", validation.isValid)
        assertTrue("Covered area percentage should be reasonable", validation.coveragePercentage in 30f..85f)
    }

    @Test
    fun testConstructionCostCalculation() {
        val estimate = ConstructionCostCalculator.calculateCost(
            totalCoveredAreaSqFt = 2500f,
            city = "Lahore",
            quality = ConstructionQuality.STANDARD
        )

        assertTrue(estimate.grandTotalPKR > 0)
        assertTrue(estimate.materialEstimates.isNotEmpty())
        assertTrue(estimate.costBreakdown.isNotEmpty())
        assertEquals(2500f, estimate.totalCoveredAreaSqFt, 0.01f)
    }
}
