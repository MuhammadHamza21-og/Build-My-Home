package com.example.data.cost

import com.example.data.model.ConstructionCostEstimate
import com.example.data.model.ConstructionQuality
import com.example.data.model.CostBreakdownItem
import com.example.data.model.MaterialItem

object ConstructionCostCalculator {

    private val cityMultipliers = mapOf(
        "Lahore" to 1.0f,
        "Islamabad" to 1.05f,
        "Rawalpindi" to 1.03f,
        "Karachi" to 1.02f,
        "Peshawar" to 1.04f,
        "Multan" to 0.97f,
        "Faisalabad" to 0.98f
    )

    fun calculateCost(
        totalCoveredAreaSqFt: Float,
        city: String = "Lahore",
        quality: ConstructionQuality = ConstructionQuality.STANDARD
    ): ConstructionCostEstimate {
        val multiplier = cityMultipliers[city] ?: 1.0f
        val greyRate = quality.greyRatePerSqFt * multiplier
        val finishRate = quality.finishRatePerSqFt * multiplier

        val totalGrey = totalCoveredAreaSqFt * greyRate
        val totalFinishing = totalCoveredAreaSqFt * finishRate
        val subtotal = totalGrey + totalFinishing
        val contingency = subtotal * 0.05f // 5% contingency reserve for price fluctuations
        val grandTotal = subtotal + contingency

        // Itemized Cost Breakdown
        val breakdown = listOf(
            CostBreakdownItem(
                title = "Structure & Civil Works (RCC & Masonry)",
                costPKR = totalGrey * 0.65f,
                percentage = 35.0f,
                description = "Excavation, Grade 60 steel reinforcement, OPC cement, bricks, and concrete casting"
            ),
            CostBreakdownItem(
                title = "Plaster, Plinth & Waterproofing",
                costPKR = totalGrey * 0.20f,
                percentage = 11.0f,
                description = "Internal/external sand-cement plaster, DPC, roof heat & water insulation treatment"
            ),
            CostBreakdownItem(
                title = "Rough Plumbing & Electrical Conduits",
                costPKR = totalGrey * 0.15f,
                percentage = 8.0f,
                description = "PPRC water supply pipes, UPVC sewerage lines, electrical pipes & wall junction boxes"
            ),
            CostBreakdownItem(
                title = "Flooring & Wall Cladding",
                costPKR = totalFinishing * 0.30f,
                percentage = 16.0f,
                description = "Glazed porcelain tiles, granite thresholds, marble stair treads & skirting"
            ),
            CostBreakdownItem(
                title = "Woodwork, Doors & Kitchen Cabinets",
                costPKR = totalFinishing * 0.25f,
                percentage = 13.0f,
                description = "Semi-solid / Ash wood door leaves, UV/acrylic kitchen cabinets, bedroom wardrobes"
            ),
            CostBreakdownItem(
                title = "Windows, Glass & Metalworks",
                costPKR = totalFinishing * 0.18f,
                percentage = 9.0f,
                description = "Aluminium powder-coated sections, tempered tinted glazing, staircase stainless railings"
            ),
            CostBreakdownItem(
                title = "Sanitary Fittings & Electrical Fixtures",
                costPKR = totalFinishing * 0.17f,
                percentage = 9.0f,
                description = "Master/Grohe faucets, commodes, vanity basins, LED downlights, and switches"
            ),
            CostBreakdownItem(
                title = "Paint & False Ceilings",
                costPKR = totalFinishing * 0.10f,
                percentage = 5.0f,
                description = "Gypsum board false ceilings with LED strip coves, weather sheet exterior & silk interior paint"
            )
        )

        // Estimated Material Quantities based on Pakistani engineering rules-of-thumb:
        // Steel: ~3.5 to 4.5 kg per sq ft of covered area (approx 0.004 tons / sq ft)
        // Cement: ~0.45 bags per sq ft
        // Bricks: ~25 to 30 bricks per sq ft of covered area
        // Sand: ~1.8 to 2.2 cft per sq ft
        // Crush: ~1.2 to 1.5 cft per sq ft
        val steelTons = (totalCoveredAreaSqFt * 0.0038f).coerceAtLeast(1.0f)
        val cementBags = (totalCoveredAreaSqFt * 0.45f).coerceAtLeast(100f)
        val bricksCount = (totalCoveredAreaSqFt * 26f).coerceAtLeast(5000f)
        val sandCft = (totalCoveredAreaSqFt * 2.0f)
        val crushCft = (totalCoveredAreaSqFt * 1.3f)

        val materials = listOf(
            MaterialItem(
                name = "Grade 60 Deformed Steel (Rebar)",
                quantity = steelTons,
                unit = "Tons",
                unitRatePKR = 265_000f,
                totalCostPKR = steelTons * 265_000f,
                category = "Grey Structure"
            ),
            MaterialItem(
                name = "Portland Cement (Bestway / Maple Leaf)",
                quantity = cementBags,
                unit = "Bags",
                unitRatePKR = 1_420f,
                totalCostPKR = cementBags * 1_420f,
                category = "Grey Structure"
            ),
            MaterialItem(
                name = "Awwal Clay Burnt Bricks",
                quantity = bricksCount,
                unit = "Bricks",
                unitRatePKR = 14.5f,
                totalCostPKR = bricksCount * 14.5f,
                category = "Grey Structure"
            ),
            MaterialItem(
                name = "Chenab River Coarse Sand",
                quantity = sandCft,
                unit = "Cubic Feet",
                unitRatePKR = 55f,
                totalCostPKR = sandCft * 55f,
                category = "Grey Structure"
            ),
            MaterialItem(
                name = "Margalla Mountain Crush (Aggregates)",
                quantity = crushCft,
                unit = "Cubic Feet",
                unitRatePKR = 135f,
                totalCostPKR = crushCft * 135f,
                category = "Grey Structure"
            ),
            MaterialItem(
                name = "Porcelain Floor & Wall Tiles",
                quantity = totalCoveredAreaSqFt * 0.85f,
                unit = "Sq Feet",
                unitRatePKR = if (quality == ConstructionQuality.PREMIUM_EXECUTIVE) 420f else 280f,
                totalCostPKR = (totalCoveredAreaSqFt * 0.85f) * (if (quality == ConstructionQuality.PREMIUM_EXECUTIVE) 420f else 280f),
                category = "Finishing"
            ),
            MaterialItem(
                name = "Premium Emulsion & Weather Paint",
                quantity = (totalCoveredAreaSqFt * 3.2f),
                unit = "Sq Feet Surface",
                unitRatePKR = 48f,
                totalCostPKR = (totalCoveredAreaSqFt * 3.2f) * 48f,
                category = "Finishing"
            )
        )

        return ConstructionCostEstimate(
            totalCoveredAreaSqFt = totalCoveredAreaSqFt,
            city = city,
            quality = quality,
            greyStructureRatePerSqFt = greyRate,
            finishingRatePerSqFt = finishRate,
            totalGreyCostPKR = totalGrey,
            totalFinishingCostPKR = totalFinishing,
            contingencyReservePKR = contingency,
            grandTotalPKR = grandTotal,
            costBreakdown = breakdown,
            materialEstimates = materials
        )
    }
}
