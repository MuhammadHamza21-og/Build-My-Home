package com.example.data.model

data class MaterialItem(
    val name: String,
    val quantity: Float,
    val unit: String,
    val unitRatePKR: Float,
    val totalCostPKR: Float,
    val category: String
)

data class CostBreakdownItem(
    val title: String,
    val costPKR: Float,
    val percentage: Float,
    val description: String
)

data class ConstructionCostEstimate(
    val totalCoveredAreaSqFt: Float,
    val city: String,
    val quality: ConstructionQuality,
    val greyStructureRatePerSqFt: Float,
    val finishingRatePerSqFt: Float,
    val totalGreyCostPKR: Float,
    val totalFinishingCostPKR: Float,
    val contingencyReservePKR: Float,
    val grandTotalPKR: Float,
    val costBreakdown: List<CostBreakdownItem>,
    val materialEstimates: List<MaterialItem>,
    val disclaimer: String = "Preliminary construction estimate based on standard regional material & labour indices. Actual costs vary with soil conditions, seismic zone, structural engineer specifications, and brand selections."
) {
    fun formatPKR(amount: Float): String {
        val lakhs = amount / 100_000f
        return if (lakhs >= 100f) {
            val crores = lakhs / 100f
            String.format("%.2f Crore PKR", crores)
        } else {
            String.format("%.1f Lakh PKR", lakhs)
        }
    }
}
